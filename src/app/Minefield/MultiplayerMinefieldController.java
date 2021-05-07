package app.Minefield;

import SupportingFiles.Audio.Sound;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

import static app.PublicDefinitions.*;
import static Extensions.Misc.Print.print;
import static Extensions.TypeCasting.CastString.String;

public class MultiplayerMinefieldController extends MinefieldController {

    //region Scores Computing & Player Switching

    /**
     * Compute the scores every time a player flags somewhere and change currentPlayerIndex if needed.
     * @param row the row that is clicked just now
     * @param column the column that is clicked just now
     */
    public void computeScores(int row, int column) {
        //Compute scores and mistakes.
        if (manipulatedMinefield[row][column] == LabelType.BOMBED) {
            scores[currentPlayerIndex]--;
        }else if (manipulatedMinefield[row][column] == LabelType.CORRECT) {
            scores[currentPlayerIndex]++;
        }else if (manipulatedMinefield[row][column] == LabelType.WRONG) {
            mistakes[currentPlayerIndex]++;
        }

        switchPlayer();
    }

    public void switchPlayer() {
        stepsNum++;
        if (stepsNum >= clicksPerMove) {
            currentPlayerIndex++;
            if (currentPlayerIndex >= numberOfPlayers) {
                currentPlayerIndex = 0;
            }
            stepsNum = 0;
            checkIfShouldStopEveryBout();
        }
        System.out.println("Current player's index:"+currentPlayerIndex);
        updateVBoxUI();
        playerStartTime = System.currentTimeMillis();
        playerStopTime = playerStartTime + 1000L*timeout;
        startPlayerTimer();
    }

    //endregion

    //region Variable Declaration

    int[] scores;
    int[] mistakes;

    int clicksPerMove = 1;
    int numberOfPlayers = 2;
    int timeout = 30;

    int currentPlayerIndex = 0;
    int stepsNum = 0;
    int winnerIndex;

    long startTime;
    long stopTime;

    long playerStartTime;
    long playerStopTime;

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            while (true) {
                stopTime = System.currentTimeMillis();
                Platform.runLater(() -> updateInformativeLabels());
                if (shouldStop) {
                    music.stop();
                    try {
                        Sound.gameOver();
                    } catch (Exception ignored) {}
                    return;
                }
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            }
        }
    });

    Thread playerThread;

    //endregion

    //region Initializer & Date Generation

     public MultiplayerMinefieldController(int rows, int columns, int mines, int numberOfPlayers, int clicksPerMove, int timeout) throws IOException {
        super(rows, columns, mines);
        this.clicksPerMove = clicksPerMove;
        this.numberOfPlayers = numberOfPlayers;
        scores = new int[numberOfPlayers];
        mistakes = new int[numberOfPlayers];
    }

    //endregion

    //region Click Handling

    @Override
    void clickedOnLabel(MouseClickType type, int row, int column) {

         if (shouldStop) { return; }
         switch (manipulatedMinefield[row][column]) {
             case CLICKED:
                 System.out.println("The square has been clicked");
                 break;
             case NOT_CLICKED:
                 switch (type) {

                     case PRIMARY:
                         if (minefield[row][column] == MinefieldType.MINE) {
                             if (isFirstClick) {
                                 print("First clicked on a mine! Regenerate minefield.");
                                 generateMinefieldData(rows, columns, mines);
                                 clickedOnLabel(MouseClickType.PRIMARY, row, column);
                                 return;
                             }else {
                                 Sound.flagWrongly();
                                 discoveredMines += 1;
                                 markGridLabel(row, column, LabelType.BOMBED);
                                 computeScores(row,column);
                             }
                         }else {
                             Sound.uncover();
                             clickRecursively(row, column, row, column);
                             markGridLabel(row, column, LabelType.CLICKED);
                             computeScores(row,column);
                         }
                         break;

                     case SECONDARY:
                         if (minefield[row][column] == MinefieldType.MINE) {
                             Sound.flagCorrectly();
                             discoveredMines += 1;
                             markGridLabel(row,column,LabelType.CORRECT);
                         }else {
                             Sound.flagWrongly();
                             markGridLabel(row,column,LabelType.WRONG);
                         }
                         computeScores(row,column);
                         break;
                     default:
                         break;
                 }
                 break;
             default:
                 System.out.println("Default");
                 break;
         }
         updateInformativeLabels();
         if (isFirstClick) {
             thread.start();
         }
         isFirstClick = false;
         checkIfShouldStop();
         System.out.printf("Clicked Type: %s, Row: %d, Column: %d\n", type, row + 1, column + 1);
    }

    //endregion

    //region UI Updates

    public void checkIfShouldStop() {
        if (mines == discoveredMines) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (manipulatedMinefield[i][j] == LabelType.NOT_CLICKED ) {
                        return;
                    }
                }
            }
            if ( scores[0] != scores[1]) {
                winnerIndex = (scores[0] > scores[1]) ? 0 : 1;
            }else {
                if (mistakes[0] != mistakes[1]) {
                    winnerIndex = (mistakes[0] < mistakes[1]) ? 0 : 1;
                }else {
                    winnerIndex = -1;
                }
            }
            shouldStop = true;
        }
    }

    /**
     * Check if the game should stop every time a player end his bout.
     */
    public void checkIfShouldStopEveryBout() {
        for (int i = 0;i<numberOfPlayers-1;i++) {
            for (int j = i;j<numberOfPlayers-1;j++) {
                if (Math.abs(scores[j] - scores[j+1]) > mines - discoveredMines) {
                    shouldStop = true;
                }
            }
        }
    }

    /**
     * <p>A UI method to update informative labels on the right side of the minefield.</p>
     * <p><i>startTime</i> and <i>stopTime</i> are used to calculate elapsed time.</p>
     * <p><i>discoveredMines</i> is used to calculate remaining mines.</p>
     */
    @Override
    public void updateInformativeLabels() {

        long duration = stopTime - startTime;
        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;
        long hour = (duration / (1000 * 60 * 60)) % 24;
        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        timerLabel.setText(time);

        mineLabel.setText(String(mines - discoveredMines));
        switch (numberOfPlayers) {
            case 4:
                playerInformationVBox3.scoreLabel.setText(String(scores[3]));
                playerInformationVBox3.mistakesLabel.setText(String(mistakes[3]));
                playerInformationVBox3.timeLabel.setText("00:00");
            case 3:
                playerInformationVBox2.scoreLabel.setText(String(scores[2]));
                playerInformationVBox2.mistakesLabel.setText(String(mistakes[2]));
                playerInformationVBox2.timeLabel.setText("00:00");
            case 2:
                playerInformationVBox1.scoreLabel.setText(String(scores[1]));
                playerInformationVBox1.mistakesLabel.setText(String(mistakes[1]));
                playerInformationVBox1.timeLabel.setText("00:00");
                playerInformationVBox0.scoreLabel.setText(String(scores[0]));
                playerInformationVBox0.mistakesLabel.setText(String(mistakes[0]));
                playerInformationVBox0.timeLabel.setText("00:00");
                break;
        }

        long playerDuration = playerStopTime - System.currentTimeMillis();
        long playerSecond = (playerDuration / 1000) % 60;
        long playerMinute = (playerDuration / (1000 * 60)) % 60;
        String playerTime = String.format("%02d:%02d", playerMinute, playerSecond);

        switch (currentPlayerIndex) {
            case 0 -> playerInformationVBox0.timeLabel.setText(playerTime);
            case 1 -> playerInformationVBox1.timeLabel.setText(playerTime);
            case 2 -> playerInformationVBox2.timeLabel.setText(playerTime);
            case 3 -> playerInformationVBox3.timeLabel.setText(playerTime);
        }
    }

    public void updateVBoxUI() {
        playerInformationVBox0.setStyle("-fx-border-radius: 0;-fx-border-color: -system-orange;-fx-border-width: 0;");
        playerInformationVBox1.setStyle("-fx-border-radius: 0;-fx-border-color: -system-orange;-fx-border-width: 0;");
        playerInformationVBox2.setStyle("-fx-border-radius: 0;-fx-border-color: -system-orange;-fx-border-width: 0;");
        playerInformationVBox3.setStyle("-fx-border-radius: 0;-fx-border-color: -system-orange;-fx-border-width: 0;");
        switch (currentPlayerIndex) {
            case 0:
//                playerInformationVBox0.getStyleClass().add("currentPlayerVBox");
                playerInformationVBox0.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 5;");
                break;
            case 1:
//                playerInformationVBox1.getStyleClass().add("currentPlayerVBox");
                playerInformationVBox1.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 5;");
                break;
            case 2:
//                playerInformationVBox2.getStyleClass().add("currentPlayerVBox");
                playerInformationVBox2.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 5;");
                break;
            case 3:
//                playerInformationVBox3.getStyleClass().add("currentPlayerVBox");
                playerInformationVBox3.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 5;");
                break;
        }
    }

    public void startPlayerTimer() {
        playerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (System.currentTimeMillis() >= playerStopTime) {
                        // Time is up.
                        switchPlayer();
                        return;
                    }
                    try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                }
            }
        });
        playerThread.start();
    }

    //endregion

    //region Menu Items

    @Override
    void closeStage() {
        print("closeStage");
        if (Stage.getWindows().size() > 1) {
            print("More than 1 window, keep playing.");
        } else {
            music.stop();
        }
        thread.stop();
        playerThread.stop();
        mainStage.close();
        print("Stage closed");
    }

    public static class PlayerInformationVBox extends VBox {
        Label iconLabel;
        Label scoreNameLabel = new Label("Score: ");
        Label scoreLabel = new Label("0");
        Label mistakesNameLabel = new Label("Mistakes: ");
        Label mistakesLabel = new Label("0");
        Label timeNameLabel = new Label("Time: ");
        Label timeLabel = new Label("00:00");
        Label stepsNameLabel = new Label("Steps: ");
        Label stepsLabel = new Label("0");

        public PlayerInformationVBox(int playerIndex) {
            switch (playerIndex) {
                case 0 -> iconLabel = new Label("\uDBC0\uDC05");
                case 1 -> iconLabel = new Label("\uDBC0\uDC07");
                case 2 -> iconLabel = new Label("\uDBC0\uDC09");
                case 3 -> iconLabel = new Label("\uDBC0\uDC0B");
            }
            iconLabel.setFont(new Font("SF Pro Display Regular",80));
            iconLabel.getStyleClass().add("playerIcon");

            scoreNameLabel.setFont(new Font("SF Mono Regular",18));
            scoreNameLabel.getStyleClass().add("playerInformationLabel");
            scoreLabel.setFont(new Font("SF Mono Regular",18));
            scoreLabel.getStyleClass().add("playerInformationLabel");
            HBox scoreHBox = new HBox(scoreNameLabel,scoreLabel);
            scoreHBox.setAlignment(Pos.CENTER);

            mistakesNameLabel.setFont(new Font("SF Mono Regular",18));
            mistakesNameLabel.getStyleClass().add("playerInformationLabel");
            mistakesLabel.setFont(new Font("SF Mono Regular",18));
            mistakesLabel.getStyleClass().add("playerInformationLabel");
            HBox mistakesHBox = new HBox(mistakesNameLabel,mistakesLabel);
            mistakesHBox.setAlignment(Pos.CENTER);

            timeNameLabel.setFont(new Font("SF Mono Regular",18));
            timeNameLabel.getStyleClass().add("playerInformationLabel");
            timeLabel.setFont(new Font("SF Mono Regular",18));
            timeLabel.getStyleClass().add("playerInformationLabel");
            HBox timeHBox = new HBox(timeNameLabel,timeLabel);
            timeHBox.setAlignment(Pos.CENTER);

            stepsNameLabel.setFont(new Font("SF Mono Regular",18));
            stepsNameLabel.getStyleClass().add("playerInformationLabel");
            stepsLabel.setFont(new Font("SF Mono Regular",18));
            stepsLabel.getStyleClass().add("playerInformationLabel");
            HBox stepsHBox = new HBox(stepsNameLabel,stepsLabel);
            stepsHBox.setAlignment(Pos.CENTER);

            this.setSpacing(4);
            this.getChildren().addAll(iconLabel,scoreHBox,mistakesHBox,timeHBox,stepsHBox);
            this.setAlignment(Pos.CENTER);
            GridPane.setMargin(this,new Insets(10,10,10,10));
        }

    }

    PlayerInformationVBox playerInformationVBox0;
    PlayerInformationVBox playerInformationVBox1;
    PlayerInformationVBox playerInformationVBox2;
    PlayerInformationVBox playerInformationVBox3;

    @Override
    public void initializeRightBorderPane() {
        playerInformationVBox0 = new PlayerInformationVBox(0);
        playerInformationVBox1 = new PlayerInformationVBox(1);
        playerInformationVBox2 = new PlayerInformationVBox(2);
        playerInformationVBox3 = new PlayerInformationVBox(3);

        playerInformationGridPane.setGridLinesVisible(false);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.ALWAYS);
        rowConstraints.setValignment(VPos.CENTER);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        columnConstraints.setHalignment(HPos.CENTER);

        switch (numberOfPlayers) {
            case 2 -> {
                rowConstraints.setPercentHeight(50.0);
                columnConstraints.setPercentWidth(100.0);
                playerInformationGridPane.getRowConstraints().addAll(rowConstraints,rowConstraints);
                playerInformationGridPane.getColumnConstraints().addAll(columnConstraints);
                playerInformationGridPane.add(playerInformationVBox0, 0, 0);
                playerInformationGridPane.add(playerInformationVBox1, 0, 1);
            }
            case 3 -> {
                rowConstraints.setPercentHeight(50.0);
                columnConstraints.setPercentWidth(50.0);
                playerInformationGridPane.getRowConstraints().addAll(rowConstraints, rowConstraints);
                playerInformationGridPane.getColumnConstraints().addAll(columnConstraints, columnConstraints);
                playerInformationGridPane.add(playerInformationVBox0, 0, 0);
                playerInformationGridPane.add(playerInformationVBox1, 1, 0);
                playerInformationGridPane.add(playerInformationVBox2, 0, 1);
                GridPane.setColumnSpan(playerInformationVBox2, GridPane.REMAINING);
            }
            case 4 -> {
                rowConstraints.setPercentHeight(50.0);
                columnConstraints.setPercentWidth(50.0);
                playerInformationGridPane.getRowConstraints().addAll(rowConstraints, rowConstraints);
                playerInformationGridPane.getColumnConstraints().addAll(columnConstraints, columnConstraints);
                playerInformationGridPane.add(playerInformationVBox0, 0, 0);
                playerInformationGridPane.add(playerInformationVBox1, 1, 0);
                playerInformationGridPane.add(playerInformationVBox2, 0, 1);
                playerInformationGridPane.add(playerInformationVBox3, 1, 1);
            }
        }

//        playerInformationGridPane.setPrefSize(300, 520);
//        playerInformationGridPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
//        playerInformationGridPane.setMaxSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        playerInformationGridPane.setAlignment(Pos.CENTER);
    }

    //endregion

}

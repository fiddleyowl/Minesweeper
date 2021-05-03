package app.Minefield;

import SupportingFiles.Audio.Sound;
import javafx.application.Platform;
import javafx.geometry.HPos;
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

    //endregion

    //region Initializer & Date Generation

     public MultiplayerMinefieldController(int rows, int columns, int mines, int numberOfPlayers, int clicksPerMove, int timeout) throws IOException {
        super(rows, columns, mines);
        this.clicksPerMove = clicksPerMove;
        this.numberOfPlayers = numberOfPlayers;
        initializeRightBorderPane();
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
            case 3:
                playerInformationVBox2.scoreLabel.setText(String(scores[2]));
                playerInformationVBox2.mistakesLabel.setText(String(mistakes[2]));
            case 2:
                playerInformationVBox1.scoreLabel.setText(String(scores[1]));
                playerInformationVBox1.mistakesLabel.setText(String(mistakes[1]));
                playerInformationVBox0.scoreLabel.setText(String(scores[0]));
                playerInformationVBox0.mistakesLabel.setText(String(mistakes[0]));
                break;
        }
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
        mainStage.close();
        print("Stage closed");
    }

    public static class PlayerInformationVBox extends VBox {
        Label iconLabel;
        Label scoreNameLabel = new Label("Score: ");
        Label scoreLabel = new Label("0");
        Label mistakesNameLabel = new Label("Mistakes: ");
        Label mistakesLabel = new Label("0");

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
            this.setSpacing(4);
            this.getChildren().addAll(iconLabel,scoreHBox,mistakesHBox);
            this.setAlignment(Pos.CENTER);
        }

    }

    PlayerInformationVBox playerInformationVBox0 = new PlayerInformationVBox(0);
    PlayerInformationVBox playerInformationVBox1 = new PlayerInformationVBox(1);
    PlayerInformationVBox playerInformationVBox2 = new PlayerInformationVBox(2);
    PlayerInformationVBox playerInformationVBox3 = new PlayerInformationVBox(3);

    public void initializeRightBorderPane() {
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

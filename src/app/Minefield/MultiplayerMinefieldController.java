package app.Minefield;

import SupportingFiles.Audio.Sound;
import SupportingFiles.DataModels.GameModel;
import SupportingFiles.DataEncoder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static Extensions.Misc.BetterArray.*;
import static app.PublicDefinitions.*;
import static Extensions.Misc.Print.*;
import static Extensions.TypeCasting.CastString.*;

import static SupportingFiles.ConfigHelper.*;

public class MultiplayerMinefieldController extends MinefieldController {

    //region Variables Declaration

    public int[] scores;
    public int[] mistakes;

    public int clicksPerMove = 1;
    public int numberOfPlayers = 2;
    public int timeout = 30;

    public int currentPlayerIndex = 0;
    public int stepsNum = 0;
    public int winnerIndex = -1;

    public long playerStartTime;
    public long playerStopTime;

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            if (shouldUseCurrentTimeAsStartTime) {
                startTime = System.currentTimeMillis();
            }
            while (true) {
                stopTime = System.currentTimeMillis();
                Platform.runLater(() -> updateInformativeLabels());
                if (shouldStop) {
                    music.stop();
                    try {
                        Sound.gameOver();
                    } catch (Exception ignored) {}
                    winnerIndex = computeFinalWinners();
                    print("Winner is " + winnerIndex + ".");
                    Platform.runLater(() -> {
                        try {
                            endGame();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    return;
                }
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            }
        }
    });

    PlayerInformationVBox playerInformationVBox0 = new PlayerInformationVBox(0);
    PlayerInformationVBox playerInformationVBox1 = new PlayerInformationVBox(1);
    PlayerInformationVBox playerInformationVBox2 = new PlayerInformationVBox(2);
    PlayerInformationVBox playerInformationVBox3 = new PlayerInformationVBox(3);

    PlayerInformationVBox[] playerInformationVBoxes = new PlayerInformationVBox[]{playerInformationVBox0,playerInformationVBox1,playerInformationVBox2,playerInformationVBox3};

    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
        playerInformationVBoxes[currentPlayerIndex].setStyle("-fx-border-radius: 10;-fx-border-color: transparent;-fx-border-width: 5;");
    }), new KeyFrame(Duration.millis(1000), event -> {
        playerInformationVBoxes[currentPlayerIndex].setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 5;");
    }));

    Thread playerThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (shouldStop) {
                    return;
                }

                if (playerStopTime - System.currentTimeMillis() <= 6100 && playerStopTime - System.currentTimeMillis() >= 5900) {
                    // Time is nearly up.
                    timeline.setCycleCount(6);
                    timeline.play();
                }
                if (System.currentTimeMillis() >= playerStopTime) {
                    // Time is up.
                    Sound.glassAlert();
                    switchPlayer();
                }
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            }
        }
    });

    //endregion

    //region Initializer & Data Generation

    public MultiplayerMinefieldController(int rows, int columns, int mines, int numberOfPlayers, int clicksPerMove, int timeout) throws IOException {
        super(rows, columns, mines);
        this.clicksPerMove = clicksPerMove;
        this.timeout = timeout;
        this.numberOfPlayers = numberOfPlayers;
        initializeRightBorderPane();
        scores = new int[numberOfPlayers];
        mistakes = new int[numberOfPlayers];
        updateVBoxUI();
        thread.start();
        playerStartTime = System.currentTimeMillis();
        playerStopTime = playerStartTime + 1000L*timeout;
        playerThread.start();
    }

    public MultiplayerMinefieldController(GameModel gameModel, String savePath) throws IOException {
        super(gameModel,savePath);
        applyGameModel(gameModel);
        isSaved = true;
//        savePath =
    }

    public MultiplayerMinefieldController(int rows, int columns, int mines, int numberOfPlayers, int clicksPerMove, int timeout, MinefieldType[][] minefield) throws IOException {
        super(rows,columns,mines,minefield);
        this.numberOfPlayers = numberOfPlayers;
        this.clicksPerMove = clicksPerMove;
        this.timeout = timeout;
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                this.minefield[row][column] = minefield[row][column];
            }
        }
        initializeRightBorderPane();
        scores = new int[numberOfPlayers];
        mistakes = new int[numberOfPlayers];
        updateVBoxUI();
        thread.start();
        playerStartTime = System.currentTimeMillis();
        playerStopTime = playerStartTime + 1000L*timeout;
        playerThread.start();
        isFirstClick = false;
    }

    @Override
    public void playSameBoard() throws IOException {
        mainStage.setFullScreen(false);
        closeStage();
        MultiplayerMinefieldController multiplayerMinefieldController = new MultiplayerMinefieldController(rows,columns,mines,numberOfPlayers,clicksPerMove,timeout,minefield);
    }

    public boolean shouldUseCurrentTimeAsStartTime = true;

    public void applyGameModel(GameModel gameModel) {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                minefield[row][column] = gameModel.minefield[row][column];
                markGridLabel(row,column,gameModel.manipulatedMinefield[row][column]);
                if (manipulatedMinefield[row][column] != LabelType.NOT_CLICKED) {
                    isFirstClick = false;
                }
                if (manipulatedMinefield[row][column] == LabelType.CORRECT || manipulatedMinefield[row][column] == LabelType.BOMBED) {
                    discoveredMines += 1;
                }
            }
        }
        this.numberOfPlayers = gameModel.numberOfPlayers;
        this.clicksPerMove = gameModel.clicksPerMove;
        this.timeout = gameModel.timeout;
        this.rounds = gameModel.rounds;
        this.currentPlayerIndex = gameModel.activePlayer;
        initializeRightBorderPane();
        scores = new int[numberOfPlayers];
        mistakes = new int[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            scores[i] = gameModel.players[i].score;
            mistakes[i] = gameModel.players[i].mistakes;
        }

        stepsNum = clicksPerMove - gameModel.clicksLeftForActivePlayer;

        updateVBoxUI();

        thread.start();
        startTime = System.currentTimeMillis() - gameModel.timeUsed;
        shouldUseCurrentTimeAsStartTime = false;

        playerStartTime = System.currentTimeMillis() - gameModel.timeLeftForActivePlayer;
        playerStopTime = playerStartTime + 1000L*timeout;
        playerThread.start();
    }

    //endregion

    //region Click Handling

    @Override
    void clickedOnLabel(MouseClickType type, int row, int column) {
         if (shouldStop) {
             return;
         }

        if (manipulatedMinefield[row][column] == LabelType.NOT_CLICKED) {
            switch (type) {
                case PRIMARY:
                    if (minefield[row][column] == MinefieldType.MINE) {
                        if (isFirstClick) {
                            print("First clicked on a mine! Regenerate minefield.");
                            generateMinefieldData(rows, columns, mines);
                            clickedOnLabel(MouseClickType.PRIMARY, row, column);
                            return;
                        } else {
                            Sound.flagWrongly();
                            discoveredMines += 1;
                            markGridLabel(row, column, LabelType.BOMBED);
                            computeScores(row, column);
                        }
                    } else {
                        Sound.uncover();
                        if (isOpenAllSquaresSurroundingZeroEnabled()) {
                            clickRecursively(row, column, row, column);
                        }
                        markGridLabel(row, column, LabelType.CLICKED);
                        computeScores(row, column);
                    }
                    break;

                case SECONDARY:
                    if (minefield[row][column] == MinefieldType.MINE) {
                        Sound.flagCorrectly();
                        discoveredMines += 1;
                        markGridLabel(row, column, LabelType.CORRECT);
                    } else {
                        Sound.flagWrongly();
                        markGridLabel(row, column, LabelType.WRONG);
                    }
                    computeScores(row, column);
                    break;
                default:
                    break;
            }
        }
         updateInformativeLabels();
         isFirstClick = false;
         checkIfShouldStop();
         print("Clicked Type: %s, Row: %d, Column: %d\n", type, row + 1, column + 1);
    }

    //endregion

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

    public int computeFinalWinners() {
        int maxScore = -mines;
        ArrayList<Integer> winnerIndexes = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                winnerIndexes = createArrayList(new int[]{i});
            } else if (scores[i] == maxScore) {
                winnerIndexes.add(i);
            }
        }

        int[] playerNumbers = new int[]{1,2,4,8};

        if (winnerIndexes.size() == 1) {
            return playerNumbers[winnerIndexes.get(0)];
        }

        // More than 1 possible winner.

        int minMistakes = mines;
        ArrayList<Integer> minMistakesIndexes = new ArrayList<>();
        for (Integer index : winnerIndexes) {
            if (mistakes[index] < minMistakes) {
                minMistakes = mistakes[index];
                minMistakesIndexes = createArrayList(new int[]{index});
            } else if (mistakes[index] == minMistakes) {
                minMistakesIndexes.add(index);
            }
        }

        int sum = 0;
        for (Integer index : minMistakesIndexes) {
            sum += playerNumbers[index];
        }
        return sum;
    }

    public void switchPlayer() {
        timeline.stop();
        stepsNum++;
        if (stepsNum >= clicksPerMove) {
            currentPlayerIndex++;
            if (currentPlayerIndex >= numberOfPlayers) {
                currentPlayerIndex = 0;
                rounds += 1;
            }
            stepsNum = 0;
            checkIfShouldStopEveryBout();
            updateVBoxUI();
            playerStartTime = System.currentTimeMillis();
            playerStopTime = playerStartTime + 1000L*timeout;
        }
        print("Current player's index:"+currentPlayerIndex);

    }

    //endregion

    //region UI Updates

    public void checkIfShouldStop() {
        if (mines == discoveredMines) {
//            for (int i = 0; i < scores.length - 1; i++) {
//                if (scores[i+1] > scores[i]) { winnerIndex = i+1;}
//            }
            shouldStop = true;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (manipulatedMinefield[i][j] == LabelType.NOT_CLICKED || manipulatedMinefield[i][j] == LabelType.QUESTIONED) {
                    return;
                }
            }
        }
        shouldStop = true;
    }

    /**
     * Check if the game should stop every time a player end his bout.
     */
    public void checkIfShouldStopEveryBout() {
        int minimumScoreDifference = mines;
        for (int i = 0; i < numberOfPlayers; i++) {
            for (int j = i+1; j < numberOfPlayers; j++) {
                if (Math.abs(scores[i]-scores[j]) < minimumScoreDifference) {
                    minimumScoreDifference = Math.abs(scores[i]-scores[j]);
                }
//                if (Math.abs(scores[j] - scores[j + 1]) > mines - discoveredMines) {
//                    winnerIndex = (scores[i] > scores[i + 1]) ? i : (i + 1);
//                    shouldStop = true;
//                }
            }
        }
        print("minimumScoreDifference: "+minimumScoreDifference);
        if (minimumScoreDifference > mines - discoveredMines) {
            shouldStop = true;
        }
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
            GridPane.setMargin(this,new Insets(2,2,2,2));
        }

    }

    @Override
    public void initializeRightBorderPane() {
        print("initializeRightBorderPane");
//        playerInformationVBox0 = new PlayerInformationVBox(0);
//        playerInformationVBox1 = new PlayerInformationVBox(1);
//        playerInformationVBox2 = new PlayerInformationVBox(2);
//        playerInformationVBox3 = new PlayerInformationVBox(3);

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
        roundLabel.setText(String(rounds));
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

        long playerDuration = playerStopTime - System.currentTimeMillis();
        long playerSecond = (playerDuration / 1000) % 60;
        long playerMinute = (playerDuration / (1000 * 60)) % 60;
        String playerTime = String.format("%02d:%02d", playerMinute, playerSecond);

        try {
            switch (currentPlayerIndex) {
                case 0 -> {
                    playerInformationVBox0.timeLabel.setText(playerTime);
                    playerInformationVBox0.stepsLabel.setText(String(clicksPerMove - stepsNum));
                }
                case 1 -> {
                    playerInformationVBox1.timeLabel.setText(playerTime);
                    playerInformationVBox1.stepsLabel.setText(String(clicksPerMove - stepsNum));
                }
                case 2 -> {
                    playerInformationVBox2.timeLabel.setText(playerTime);
                    playerInformationVBox2.stepsLabel.setText(String(clicksPerMove - stepsNum));
                }
                case 3 -> {
                    playerInformationVBox3.timeLabel.setText(playerTime);
                    playerInformationVBox3.stepsLabel.setText(String(clicksPerMove - stepsNum));
                }
            }
        } catch (Exception ignored) { }

    }

    public void updateVBoxUI() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                playerInformationVBox0.setStyle("-fx-border-radius: 0;-fx-border-color: -system-orange;-fx-border-width: 0;");
                playerInformationVBox0.timeLabel.setText("00:00");
                playerInformationVBox0.stepsLabel.setText("0");
                playerInformationVBox1.setStyle("-fx-border-radius: 0;-fx-border-color: -system-orange;-fx-border-width: 0;");
                playerInformationVBox1.timeLabel.setText("00:00");
                playerInformationVBox1.stepsLabel.setText("0");
                playerInformationVBox2.setStyle("-fx-border-radius: 0;-fx-border-color: -system-orange;-fx-border-width: 0;");
                playerInformationVBox2.timeLabel.setText("00:00");
                playerInformationVBox2.stepsLabel.setText("0");
                playerInformationVBox3.setStyle("-fx-border-radius: 0;-fx-border-color: -system-orange;-fx-border-width: 0;");
                playerInformationVBox3.timeLabel.setText("00:00");
                playerInformationVBox3.stepsLabel.setText("0");
                switch (currentPlayerIndex) {
                    case 0 -> playerInformationVBox0.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 5;");
                    case 1 -> playerInformationVBox1.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 5;");
                    case 2 -> playerInformationVBox2.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 5;");
                    case 3 -> playerInformationVBox3.setStyle("-fx-border-radius: 10;-fx-border-color: -system-orange;-fx-border-width: 5;");
                }
            }
        });

    }

    public void endGame() throws IOException {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (manipulatedMinefield[row][column] == LabelType.NOT_CLICKED && minefield[row][column] == MinefieldType.MINE) {
                    markGridLabel(row,column,LabelType.BOMBED);
                }
            }
        }
        GameOverController gameOverController = new GameOverController(this);
        gameOverController.showStage();
    }

    //endregion

    //region Menu Items

    @Override
    public void restartNewGame() throws IOException {
        mainStage.setFullScreen(false);
        closeStage();
        MultiplayerMinefieldController multiplayerMinefieldController = new MultiplayerMinefieldController(rows, columns, mines,numberOfPlayers,clicksPerMove,timeout);
    }

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

    @Override
    public boolean saveGame() {
        if (!savePath.equals("")) {
            // Already specified path.
            GameModel gameModel = new GameModel(this);
            try (FileWriter fileWriter = new FileWriter(savePath)) {
                //We can write any JSONArray or JSONObject instance to the file
                fileWriter.write(DataEncoder.encodeGame(gameModel));
                fileWriter.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(mainStage);
        if (file != null) {
            GameModel gameModel = new GameModel(this);
            try (FileWriter fileWriter = new FileWriter(file.getAbsolutePath())) {
                //We can write any JSONArray or JSONObject instance to the file
                fileWriter.write(DataEncoder.encodeGame(gameModel));
                fileWriter.flush();
                savePath = file.getAbsolutePath();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean duplicateGame() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(mainStage);
        if (file != null) {
            GameModel gameModel = new GameModel(this);
            try (FileWriter fileWriter = new FileWriter(file.getAbsolutePath())) {
                //We can write any JSONArray or JSONObject instance to the file
                fileWriter.write(DataEncoder.encodeGame(gameModel));
                fileWriter.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //endregion

}

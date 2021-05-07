package app.Minefield;

import SupportingFiles.Audio.Sound;
import SupportingFiles.DataEncoder;
import SupportingFiles.DataModels.GameModel;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static Extensions.Misc.Print.print;
import static Extensions.TypeCasting.CastString.String;
import static app.PublicDefinitions.*;

public class AgainstAIController extends MinefieldController {

    //region Scores Computing & Player Switching


    //endregion

    //region Variables Declaration
    public boolean isWin = false;

    public boolean isSaved = false;

    public int[] scores = new int[2];
    public int[] mistakes = new int[2];
    public int currentPlayer = 0;

    public AIDifficulty aiDifficulty = AIDifficulty.MEDIUM;

    /**
     * Marks the time the game started.
     */
    public long startTime;
    /**
     * Marks the time the game stopped. If the game is not stopped, marks the current time (every 200ms).
     */
    public long stopTime;

    /**
     * <p>Updates <i>stopTime</i> and informative labels every 200ms.</p>
     * <p>If <i>shouldStop</i> becomes true, the loop returns.</p>
     */
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            //todo rewrite this implement to fit the AI against mode
            startTime = System.currentTimeMillis();
            while (true) {
//                print("while");
                stopTime = System.currentTimeMillis();
                Platform.runLater(() -> updateInformativeLabels());
                if (shouldStop) {
                    music.stop();
                    try {
                        if (isWin) {
                            Sound.win();
                        } else {
                            Sound.gameOver();
                        }
                    } catch (Exception ignored) {
                    }
                    return;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
            }
        }
    });

    //endregion

    //region Initializer & Data Generation

    public AgainstAIController(int rows, int columns, int mines, AIDifficulty aiDifficulty) throws IOException {
        super(rows, columns, mines);
        this.aiDifficulty = aiDifficulty;
    }

    //endregion

    //region Click Handling

    @Override
    void clickedOnLabel(MouseClickType type, int row, int column) {
        if (shouldStop) {
            return;
        }
        switch (manipulatedMinefield[row][column]) {
            case CLICKED:
                System.out.println("The square has been clicked");
                return;
            case NOT_CLICKED:
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
                            }
                        } else {
                            Sound.uncover();
                            clickRecursively(row, column, row, column);
                            markGridLabel(row, column, LabelType.CLICKED);
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
                        break;
                    default:
                        break;
                }
                break;
            default:
                System.out.println("Default");
                return;
        }
        updateInformativeLabels();
        if (isFirstClick) {
            thread.start();
        }
        isFirstClick = false;
        checkIfShouldStop();
        System.out.printf("Clicked Type: %s, Row: %d, Column: %d\n", type, row + 1, column + 1);
        autoSweeping(aiDifficulty);
    }

    boolean clickedOnLabel_Robot(MouseClickType type, int row, int column) {
        if (shouldStop) {
            return false;
        }
        boolean isClickedOnUnopenedSquare = false;
        switch (manipulatedMinefield[row][column]) {
            case CLICKED:
                System.out.println("The square has been clicked");
                return false;
            case NOT_CLICKED:
                switch (type) {

                    case PRIMARY:
                        if (minefield[row][column] == MinefieldType.MINE) {
                            Sound.flagWrongly();
                            discoveredMines += 1;
                            markGridLabel(row, column, LabelType.BOMBED);
                        } else {
                            Sound.uncover();
                            clickRecursively(row, column, row, column);
                            markGridLabel(row, column, LabelType.CLICKED);
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
                        break;
                    default:
                        break;
                }
                isClickedOnUnopenedSquare = true;
                break;
            default:
                System.out.println("Default");
                return false;
        }
        updateInformativeLabels();
        checkIfShouldStop();
        System.out.printf("Robot Clicked Type: %s, Row: %d, Column: %d\n", type, row + 1, column + 1);
        return isClickedOnUnopenedSquare;
    }

    //endregion

    //region Auto Sweeping

    void autoSweeping(AIDifficulty aiDifficulty) {
        if (aiDifficulty == AIDifficulty.EASY) {
            autoSweeping_easy();
            return;
        }
        if (aiDifficulty == AIDifficulty.MEDIUM) {
            autoSweeping_medium();
            return;
        }
        if (aiDifficulty == AIDifficulty.HARD) {
            autoSweeping_difficult();
            return;
        }
    }

    void autoSweeping_easy() {
        if (shouldStop) {
            return;
        }
        while (true) {
            Random random = new Random();
            int x = random.nextInt(rows);
            int y = random.nextInt(columns);
            int clickType = random.nextInt(2);
            if (manipulatedMinefield[x][y] == LabelType.NOT_CLICKED && clickedOnLabel_Robot(MouseClickType(clickType), x, y)) {
                return;
            } else {
                System.out.println("Robot clicks unsuccessfully. Click again.");
            }
        }
    }

    void autoSweeping_medium() {
        if (shouldStop) {
            return;
        }
        Random random = new Random();
        boolean probability = random.nextInt(9) > 2;
        if (probability) {
            autoSweeping_difficult();
            return;
        } else {
            boolean clickOrFlag = random.nextInt(2) == 1;
            if (clickOrFlag) {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        if (manipulatedMinefield[i][j] == LabelType.NOT_CLICKED && minefield[i][j] == MinefieldType.MINE) {
                            clickedOnLabel_Robot(MouseClickType.PRIMARY,i,j);
                            return;
                        }
                    }
                }
            }else {
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        if (manipulatedMinefield[i][j] == LabelType.NOT_CLICKED && minefield[i][j] != MinefieldType.MINE) {
                            clickedOnLabel_Robot(MouseClickType.SECONDARY,i,j);
                            return;
                        }
                    }
                }
            }
        }
    }

    void autoSweeping_difficult() {
        if (shouldStop) {
            return;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (manipulatedMinefield[i][j] == LabelType.CLICKED && minefield[i][j] != MinefieldType.EMPTY) {
                    if (countUnopenedMinesAround(i, j) + countFlagsAround(i, j) == minefield[i][j].getCode() && countUnopenedMinesAround(i, j) != 0 && countFlagsAround(i, j) != minefield[i][j].getCode()) {
                        flagACertainSquareAround(i, j);
                        return;
                    }
                } else if (manipulatedMinefield[i][j] == LabelType.CLICKED && countUnopenedMinesAround(i, j) != 0 && countFlagsAround(i, j) == minefield[i][j].getCode() && countFlagsAround(i, j) != countUnopenedMinesAround(i, j)) {
                    clickACertainSquareAround(i, j);
                    return;
                }
            }
        }

        //Click randomly and avoid mines
        while (true) {
            Random random = new Random();
            int x = random.nextInt(rows);
            int y = random.nextInt(columns);
            if (manipulatedMinefield[x][y] == LabelType.NOT_CLICKED && minefield[x][y] != MinefieldType.MINE && clickedOnLabel_Robot(MouseClickType.PRIMARY, x, y)) {
                return;
            } else {
                System.out.println("Robot avoided a mine or clicked unsuccessfully. Click again.");
            }
        }
    }

    int countUnopenedMinesAround(int i, int j) {
        int unopenedNum = 0;
        try { if (manipulatedMinefield[i-1][j-1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[i-1][ j ] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[i-1][j+1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[ i ][j+1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[i+1][j+1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[i+1][ j ] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[i+1][j-1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[ i ][j-1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) { }
        return unopenedNum;
    }

    int countFlagsAround(int i, int j) {
        int flagsNum = 0;
        try { if (manipulatedMinefield[i-1][j-1] == LabelType.CORRECT || manipulatedMinefield[i-1][j-1] == LabelType.BOMBED) { flagsNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[i-1][ j ] == LabelType.CORRECT || manipulatedMinefield[i-1][ j ] == LabelType.BOMBED) { flagsNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[i-1][j+1] == LabelType.CORRECT || manipulatedMinefield[i-1][j+1] == LabelType.BOMBED) { flagsNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[ i ][j+1] == LabelType.CORRECT || manipulatedMinefield[ i ][j+1] == LabelType.BOMBED) { flagsNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[i+1][j+1] == LabelType.CORRECT || manipulatedMinefield[i+1][j+1] == LabelType.BOMBED) { flagsNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[i+1][ j ] == LabelType.CORRECT || manipulatedMinefield[i+1][ j ] == LabelType.BOMBED) { flagsNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[i+1][j-1] == LabelType.CORRECT || manipulatedMinefield[i+1][j-1] == LabelType.BOMBED) { flagsNum++; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[ i ][j-1] == LabelType.CORRECT || manipulatedMinefield[ i ][j-1] == LabelType.BOMBED) { flagsNum++; } } catch (Exception ignored) { }
        return flagsNum;
    }

    /*int countMinesAround(int i, int j) {
        int minesNum = 0;
        try { if (manipulatedMinefield[i][j] == LabelType.BOMBED)}
    }*/

    void flagACertainSquareAround(int i, int j) {
        try { if (clickedOnLabel_Robot(MouseClickType.SECONDARY,i-1,j-1)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.SECONDARY,i-1,j+0)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.SECONDARY,i-1,j+1)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.SECONDARY,i+0,j+1)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.SECONDARY,i+1,j+1)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.SECONDARY,i+1,j+0)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.SECONDARY,i+1,j-1)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.SECONDARY,i+0,j-1)) { return; } } catch (Exception ignored) {}
    }

    void clickACertainSquareAround(int i, int j) {
        try { if (clickedOnLabel_Robot(MouseClickType.PRIMARY,i-1,j-1)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.PRIMARY,i-1,j+0)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.PRIMARY,i-1,j+1)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.PRIMARY,i+0,j+1)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.PRIMARY,i+1,j+1)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.PRIMARY,i+1,j+0)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.PRIMARY,i+1,j-1)) { return; } } catch (Exception ignored) {}
        try { if (clickedOnLabel_Robot(MouseClickType.PRIMARY,i+0,j-1)) { return; } } catch (Exception ignored) {}
    }

    //endregion

    //region UI Updates

    public void checkIfShouldStop() {
        if (mines == discoveredMines) {
            // Mines are all flagged.
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (manipulatedMinefield[i][j] == LabelType.NOT_CLICKED || manipulatedMinefield[i][j] == LabelType.QUESTIONED) {
                        return;
                    }
                }
            }
            shouldStop = true;
        }
    }

    @Override
    public void updateInformativeLabels() {

        long duration = stopTime - startTime;
        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;
        long hour = (duration / (1000 * 60 * 60)) % 24;
        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        timerLabel.setText(time);

        mineLabel.setText(String(mines - discoveredMines));
        try {
            playerInformationVBox1.scoreLabel.setText(String(scores[1]));
            playerInformationVBox1.mistakesLabel.setText(String(mistakes[1]));
            playerInformationVBox0.scoreLabel.setText(String(scores[0]));
            playerInformationVBox0.mistakesLabel.setText(String(mistakes[0]));
        } catch (Exception ignored) { }

    }

    //endregion

    //region Menu Items

    @Override
    public void restartNewGame() throws IOException {
        mainStage.setFullScreen(false);
        closeStage();
        AgainstAIController againstAIController = new AgainstAIController(rows, columns, mines, aiDifficulty);
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
        mainStage.close();
        print("Stage closed");
    }

    @Override
    public boolean openGame() {
        return false;
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

    public static class PlayerInformationVBox extends VBox {
        Label iconLabel;
        Label scoreNameLabel = new Label("Score: ");
        Label scoreLabel = new Label("0");
        Label mistakesNameLabel = new Label("Mistakes: ");
        Label mistakesLabel = new Label("0");

        public PlayerInformationVBox(int playerIndex) {
            switch (playerIndex) {
                case 0 -> iconLabel = new Label("\uDBC0\uDE6A");
                case 1 -> iconLabel = new Label("\uDBC2\uDD7A");
            }
            iconLabel.setFont(new Font("SF Pro Display Regular", 80));
            iconLabel.getStyleClass().add("playerIcon");
            scoreNameLabel.setFont(new Font("SF Mono Regular", 18));
            scoreNameLabel.getStyleClass().add("playerInformationLabel");
            scoreLabel.setFont(new Font("SF Mono Regular", 18));
            scoreLabel.getStyleClass().add("playerInformationLabel");
            HBox scoreHBox = new HBox(scoreNameLabel, scoreLabel);
            scoreHBox.setAlignment(Pos.CENTER);
            mistakesNameLabel.setFont(new Font("SF Mono Regular", 18));
            mistakesNameLabel.getStyleClass().add("playerInformationLabel");
            mistakesLabel.setFont(new Font("SF Mono Regular", 18));
            mistakesLabel.getStyleClass().add("playerInformationLabel");
            HBox mistakesHBox = new HBox(mistakesNameLabel, mistakesLabel);
            mistakesHBox.setAlignment(Pos.CENTER);
            this.setSpacing(4);
            this.getChildren().addAll(iconLabel, scoreHBox, mistakesHBox);
            this.setAlignment(Pos.CENTER);
        }

    }

    PlayerInformationVBox playerInformationVBox0 = new PlayerInformationVBox(0);
    PlayerInformationVBox playerInformationVBox1 = new PlayerInformationVBox(1);

    @Override
    public void initializeRightBorderPane() {
        playerInformationGridPane.setGridLinesVisible(false);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.ALWAYS);
        rowConstraints.setValignment(VPos.CENTER);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        columnConstraints.setHalignment(HPos.CENTER);

        rowConstraints.setPercentHeight(50.0);
        columnConstraints.setPercentWidth(100.0);
        playerInformationGridPane.getRowConstraints().addAll(rowConstraints, rowConstraints);
        playerInformationGridPane.getColumnConstraints().addAll(columnConstraints);
        playerInformationGridPane.add(playerInformationVBox0, 0, 0);
        playerInformationGridPane.add(playerInformationVBox1, 0, 1);

        playerInformationGridPane.setAlignment(Pos.CENTER);
    }

    //endregion

}

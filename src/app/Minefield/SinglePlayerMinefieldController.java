package app.Minefield;

import SupportingFiles.Audio.Sound;
import SupportingFiles.DataModels.GameModel;
import SupportingFiles.DataEncoder;
import SupportingFiles.GameDecoder;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.util.Duration;
import net.kurobako.gesturefx.GesturePane;

import java.awt.*;
import java.io.*;
import java.util.Arrays;

import static Extensions.Misc.Print.*;
import static Extensions.TypeCasting.CastString.*;
import static Extensions.TypeCasting.CastDouble.*;

import static app.PublicDefinitions.*;
import static SupportingFiles.ConfigHelper.*;

public class SinglePlayerMinefieldController extends MinefieldController {

    //region Variables Declaration

    public boolean isWin = false;

    public boolean isSaved = false;

    /**
     * <p>Updates <i>stopTime</i> and informative labels every 200ms.</p>
     * <p>If <i>shouldStop</i> becomes true, the loop returns.</p>
     */
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            if (shouldUseCurrentTimeAsStartTime) {
                startTime = System.currentTimeMillis();
            }
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
                            Sound.gameFailed();
                        }
                    } catch (Exception ignored) { }
                    Platform.runLater(() -> {
                        try {
                            endGame();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
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

    public SinglePlayerMinefieldController(int rows, int columns, int mines) throws IOException {
        super(rows,columns,mines);
        initializeRightBorderPane();
    }

    public SinglePlayerMinefieldController(GameModel gameModel, String savePath) throws IOException {
        super(gameModel,savePath);
        applyGameModel(gameModel);
    }

    public SinglePlayerMinefieldController(int rows, int columns, int mines, MinefieldType[][] minefield) throws IOException {
        super(rows,columns,mines,minefield);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                this.minefield[row][column] = minefield[row][column];
            }
        }
        initializeRightBorderPane();
        isFirstClick = false;
        thread.start();
    }

    @Override
    public void playSameBoard() throws IOException {
        mainStage.setFullScreen(false);
        closeStage();
        SinglePlayerMinefieldController singlePlayerMinefieldController = new SinglePlayerMinefieldController(rows,columns,mines,minefield);
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
                if (manipulatedMinefield[row][column] == LabelType.FLAGGED) {
                    discoveredMines += 1;
                }
            }
        }
        rounds = gameModel.rounds;
        initializeRightBorderPane();
        if (!isFirstClick) {
            thread.start();
            startTime = System.currentTimeMillis() - gameModel.timeUsed;
            shouldUseCurrentTimeAsStartTime = false;
        }
    }

    //endregion

    //region Clicking Handling

    /**
     * The main function that handles user clicking.
     *
     * @param type   Mouse click type. Could be primary, secondary or tertiary.
     * @param row    Row that was clicked.
     * @param column Column that was clicked.
     */
    @Override
    public void clickedOnLabel(MouseClickType type, int row, int column) {
        if (shouldStop) {
            return;
        }

        switch (manipulatedMinefield[row][column]) {
            case CLICKED:
                // -1 for clicked, only tertiary button is allowed.
                if (type == MouseClickType.TERTIARY) {
                    quickClick(row, column);
                }
                break;
            case NOT_CLICKED:
                // 0 for not clicked, tertiary button is not allowed.
                switch (type) {
                    case PRIMARY:
                        // 0 for primary button.
                        Sound.uncover();
                        rounds += 1;
                        if (minefield[row][column] == MinefieldType.MINE) {
                            // Is a mine!
                            if (isFirstClick) {
                                // First clicked on a mine, regenerate minefield without any prompt.
                                print("First clicked on a mine! Regenerate minefield.");
                                generateMinefieldData(rows, columns, mines);
                                clickedOnLabel(MouseClickType.PRIMARY, row, column);
                                return;
                            } else {
                                discoveredMines += 1;
                                markGridLabel(row, column, LabelType.BOMBED);
                            }
                        } else {
                            clickRecursively(row, column, row, column);
                            markGridLabel(row, column, LabelType.CLICKED);
                        }
                        break;
                    case SECONDARY:
                        // 1 for secondary button.
                        Sound.flag();
                        rounds += 1;
                        discoveredMines += 1;
                        markGridLabel(row, column, LabelType.FLAGGED);
                        break;
                    default:
                        break;
                }
                break;
            case FLAGGED:
                // 1 for flagged, only secondary button is allowed.
                if (type == MouseClickType.SECONDARY) {
                    if (isQuestionMarksEnabled()) {
                        markGridLabel(row, column, LabelType.QUESTIONED);
                    } else {
                        markGridLabel(row, column, LabelType.NOT_CLICKED);
                    }
                    Sound.flag();
                    rounds += 1;
                    discoveredMines -= 1;
                }
                break;
            case QUESTIONED:
                // 2 for questioned, only secondary button is allowed.
                if (type == MouseClickType.SECONDARY) {
                    Sound.flag();
                    rounds += 1;
                    markGridLabel(row, column, LabelType.NOT_CLICKED);
                }
                break;
            default:
                break;
        }



        updateInformativeLabels();
        if (isFirstClick) {
            thread.start();
        }
        isFirstClick = false;
        checkIfShouldStop(row, column);
        print("Clicked Type: %s, Row: %d, Column: %d\n", type, row + 1, column + 1);
    }

    class FillLabel extends javafx.scene.control.Label {
        public FillLabel(String text, String color) {
            this.setText(text);
            this.setFont(new Font("SF Pro Display Regular",128));
            this.setStyle("-fx-text-fill: "+color);
        }
    }
    @Override
    public void initializeRightBorderPane() {
        minefieldTopVBox.setPrefHeight(200);
//        BorderPane.setMargin(minefieldTopVBox,new Insets(10,0,0,0));

        FillLabel fillLabel2 = new FillLabel("\uDBC0\uDCF2","-mine-green");
        FillLabel fillLabel1 = new FillLabel("\uDBC0\uDCEE","-system-orange");

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.ALWAYS);
        rowConstraints.setValignment(VPos.CENTER);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHgrow(Priority.ALWAYS);
        columnConstraints.setHalignment(HPos.CENTER);
        rowConstraints.setPercentHeight(50.0);
        columnConstraints.setPercentWidth(100.0);
        playerInformationGridPane.getRowConstraints().addAll(rowConstraints,rowConstraints);
        playerInformationGridPane.getColumnConstraints().addAll(columnConstraints);
        playerInformationGridPane.add(fillLabel1, 0, 0);
        playerInformationGridPane.add(fillLabel2, 0, 1);
    }

    /**
     * <p>If the number of adjacent flags is at least the number shown on the label, tertiary click automatically clicks all NOT_CLICKED labels around that label.</p>
     * <p>This method could only apply to CLICKED labels. Function returns if tertiary clicked on other labels </p>
     *
     * @param row    Row that was clicked.
     * @param column Column that was clicked.
     */
    public void quickClick(int row, int column) {
        if (!isChordEnabled()) {
            return;
        }

        if (manipulatedMinefield[row][column] != LabelType.CLICKED) {
            // If quickClick is not applied on clicked label, return without clicking.
            return;
        }

        int flaggedAround = 0;
        try { if (manipulatedMinefield[row - 1][column - 1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row - 1][column] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row - 1][column + 1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row][column - 1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row][column + 1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row + 1][column - 1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row + 1][column] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row + 1][column + 1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        if (flaggedAround < minefield[row][column].getCode()) {
            // If the number of flags around is less than the number shown on the label, return without clicking.
            return;
        }

        Sound.quickClick();
        rounds += 1;

        // Proceed with clicking.
        int[] rowData = {row - 1, row - 1, row - 1, row, row, row + 1, row + 1, row + 1};
        int[] columnData = {column - 1, column, column + 1, column - 1, column + 1, column - 1, column, column + 1};
        for (int i = 0; i < 8; i++) {
            if (rowData[i] >= 0 && rowData[i] < rows && columnData[i] >= 0 && columnData[i] < columns) {
                if (manipulatedMinefield[rowData[i]][columnData[i]] == LabelType.NOT_CLICKED) {
                    // Only if it's not clicked
                    if (minefield[rowData[i]][columnData[i]] == MinefieldType.MINE) {
                        // Oops, it's a mine.
                        discoveredMines += 1;
                        markGridLabel(rowData[i], columnData[i], LabelType.BOMBED);
                    } else {
                        // Not a mine,
                        clickRecursively(rowData[i], columnData[i], rowData[i], columnData[i]);
                        markGridLabel(rowData[i], columnData[i], LabelType.CLICKED);
                    }
                    checkIfShouldStop(rowData[i], columnData[i]);
                }
            }
        }
    }

    //endregion

    //region UI Updates

    /**
     * Determines if the game should stop. If determined, the external variable shouldStop will be true.
     *
     * @param row    Last clicked row. Used to determine if clicked on a mine.
     * @param column Last clicked column. Used to determine if clicked on a mine.
     */
    public void checkIfShouldStop(int row, int column) {
        // 1. If clicked on mine, stop immediately.
        if (manipulatedMinefield[row][column] == LabelType.BOMBED) {
            shouldStop = true;
            double clickedX = gesturePane.getHeight()/2.0;
            double clickedY = gesturePane.getWidth()/2.0;
            Point2D point = new Point2D(clickedX,clickedY);
            gesturePane.animate(Duration.millis(200)).centreOn(point);
            gesturePane.animate(Duration.millis(200)).zoomTo(gesturePane.getMinScale(),point);
            print(Arrays.deepToString(manipulatedMinefield));
            return;
        }

        // 2. All mines are discovered. This requires correct flag numbers with no un-clicked labels.
        if (mines == discoveredMines) {
            // Mines are all flagged.
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (manipulatedMinefield[i][j] == LabelType.NOT_CLICKED || manipulatedMinefield[i][j] == LabelType.QUESTIONED) {
                        return;
                    }
                }
            }
            isWin = true;
            shouldStop = true;
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
        roundLabel.setText(String(rounds));
    }

    public void endGame() throws IOException {
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (manipulatedMinefield[row][column] == LabelType.FLAGGED) {
                    if (minefield[row][column] == MinefieldType.MINE) {
                        markGridLabel(row,column,LabelType.CORRECT);
                    } else {
                        markGridLabel(row,column,LabelType.WRONG);
                    }
                }
                if ((manipulatedMinefield[row][column] == LabelType.NOT_CLICKED || manipulatedMinefield[row][column] == LabelType.QUESTIONED) && minefield[row][column] == MinefieldType.MINE) {
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
        SinglePlayerMinefieldController singlePlayerMinefieldController = new SinglePlayerMinefieldController(rows, columns, mines);
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
    public boolean saveGame() {
        if (shouldStop) {
            return false;
        }
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
        if (shouldStop) {
            return false;
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
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    //endregion

}

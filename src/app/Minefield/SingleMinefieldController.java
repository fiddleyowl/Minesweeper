package app.Minefield;

import SupportingFiles.Sound;
import app.ChooseModeController;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.*;
import net.kurobako.gesturefx.GesturePane;

import java.io.*;
import java.util.Arrays;
import java.util.Random;

import static Extensions.Misc.Print.*;
import static Extensions.TypeCasting.CastString.*;

import static app.PublicDefinitions.*;

public class SingleMinefieldController extends MinefieldController {

    //region Variables Declaration

    boolean isFirstClick = true;
    int discoveredMines = 0;
    boolean shouldStop = false;
    boolean isWin = false;

    boolean isSaved = false;

    /**
     * Marks the time the game started.
     */
    long startTime;
    /**
     * Marks the time the game stopped. If the game is not stopped, marks the current time (every 200ms).
     */
    long stopTime;

    /**
     * <p>Updates <i>stopTime</i> and informative labels every 200ms.</p>
     * <p>If <i>shouldStop</i> becomes true, the loop returns.</p>
     */
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
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
                    } catch (Exception ignored) { }
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

    public SingleMinefieldController(int rows, int columns, int mines) throws IOException {
        super(rows,columns,mines);
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
                    Sound.flag();
                    markGridLabel(row, column, LabelType.QUESTIONED);
                    discoveredMines -= 1;
                }
                break;
            case QUESTIONED:
                // 2 for questioned, only secondary button is allowed.
                if (type == MouseClickType.SECONDARY) {
                    Sound.flag();
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
        System.out.printf("Clicked Type: %s, Row: %d, Column: %d\n", type, row + 1, column + 1);
    }

    /**
     * A recursive function that automatically clicks all labels around a label with no mines nearby.
     */
    public void clickRecursively(int previousRow, int previousColumn, int currentRow, int currentColumn) {
        if (currentRow >= 0 && currentRow < rows && currentColumn >= 0 && currentColumn < columns) {
            if (minefield[previousRow][previousColumn] == MinefieldType.EMPTY && manipulatedMinefield[currentRow][currentColumn] == LabelType.NOT_CLICKED) {
                // Previous label is empty -> safe to click this label.
                markGridLabel(currentRow, currentColumn, LabelType.CLICKED);
                clickRecursively(currentRow, currentColumn, currentRow - 1, currentColumn - 1);
                clickRecursively(currentRow, currentColumn, currentRow - 1, currentColumn);
                clickRecursively(currentRow, currentColumn, currentRow - 1, currentColumn + 1);
                clickRecursively(currentRow, currentColumn, currentRow, currentColumn - 1);
                clickRecursively(currentRow, currentColumn, currentRow, currentColumn + 1);
                clickRecursively(currentRow, currentColumn, currentRow + 1, currentColumn - 1);
                clickRecursively(currentRow, currentColumn, currentRow + 1, currentColumn);
                clickRecursively(currentRow, currentColumn, currentRow + 1, currentColumn + 1);
            }

        }

    }

    /**
     * <p>If the number of adjacent flags is at least the number shown on the label, tertiary click automatically clicks all NOT_CLICKED labels around that label.</p>
     * <p>This method could only apply to CLICKED labels. Function returns if tertiary clicked on other labels </p>
     *
     * @param row    Row that was clicked.
     * @param column Column that was clicked.
     */
    public void quickClick(int row, int column) {
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
    }

    /**
     * A UI method that marks the specified grid label as the given type.
     *
     * @param row    Row of the label.
     * @param column Column of the label.
     * @param type   Type to be marked as.
     */
    @Override
    public void markGridLabel(int row, int column, LabelType type) {
        ObservableList<Node> childrens = minefieldGridPane.getChildren();
        for (Node children : childrens) {
            if (GridPane.getColumnIndex(children) == column && GridPane.getRowIndex(children) == row) {
                minefieldGridPane.getChildren().remove(children);
                Label label = new Label();
                label.setFont(new Font("SF Pro Display Regular", size));
                switch (type) {
                    case NOT_CLICKED -> {
                        label.getStyleClass().add("minefieldLabel");
                        label.setText("\uDBC0\uDC93");
                    }
                    case BOMBED -> {
                        label.getStyleClass().add("minefieldLabelBombed");
                        label.setText("\uDBC2\uDDFA");
                    }
                    case CLICKED -> {
                        label.getStyleClass().add("minefieldLabelPressed");
                        label.setText(labelText[minefield[row][column].getCode()]);
                    }
                    case FLAGGED -> {
                        label.getStyleClass().add("minefieldLabelFlagged");
//                        label.setText("\uDBC0\uDCEF");
                        label.setText("\uDBC0\uDCEE");
//                        label.setText("\uDBC0\uDECC");
                    }
                    case QUESTIONED -> {
                        label.getStyleClass().add("minefieldLabelQuestioned");
                        label.setText("\uDBC0\uDCEC");
                    }
                }

                label.setCache(true);
                label.setCacheShape(true);
                label.setCacheHint(CacheHint.SPEED);
                label.setOnMousePressed(mouseEvent -> {
                    mouseFirstX = mouseEvent.getScreenX();
                    mouseFirstY = mouseEvent.getScreenY();
                });
                label.setOnMouseReleased(mouseEvent -> {
                    mouseSecondX = mouseEvent.getScreenX();
                    mouseSecondY = mouseEvent.getScreenY();
                    double dragDistance = Math.sqrt(Math.pow(mouseFirstX - mouseSecondX, 2) + Math.pow(mouseFirstY - mouseSecondY, 2));
                    print("Drag Distance: " + dragDistance);
                    if (dragDistance > 10.0) {
                        return;
                    }
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        // Primary button clicked.
                        if (mouseEvent.getClickCount() == 1) {
                            // Single click.
                            clickedOnLabel(MouseClickType.PRIMARY, row, column);
                        } else {
                            // Double or more clicks.
                            clickedOnLabel(MouseClickType.TERTIARY, row, column);
                        }
                    } else if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                        // Secondary button clicked.
                        clickedOnLabel(MouseClickType.SECONDARY, row, column);
                    } else {
                        // Other button clicked.
                        clickedOnLabel(MouseClickType.TERTIARY, row, column);
                    }
                });

                minefieldGridPane.add(label, column, row);
                manipulatedMinefield[row][column] = type;
                return;
            }
        }

    }

    //endregion

    //region Menu Items
    @FXML
    public void newGame() throws IOException {
        double x = mainStage.getX() + (mainStage.getWidth() - CHOOSE_MODE_CONTROLLER_WIDTH) / 2;
        double y = mainStage.getY() + (mainStage.getHeight() - CHOOSE_MODE_CONTROLLER_HEIGHT - 30.0) / 2;
        ChooseModeController chooseModeController = new ChooseModeController(x, y);
        chooseModeController.showStage();
    }

    @FXML
    public void restartNewGame() throws IOException {
        mainStage.setFullScreen(false);
        mainStage.close();
        SingleMinefieldController singleMinefieldController = new SingleMinefieldController(rows, columns, mines);
    }

    @FXML
    public void openGame() {

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

    public void saveGame() {

    }

    public void duplicateGame() {

    }
    //endregion

}
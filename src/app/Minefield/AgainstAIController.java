package app.Minefield;

import SupportingFiles.Audio.Sound;
import javafx.application.Platform;

import java.io.IOException;

import static app.PublicDefinitions.*;

public class AgainstAIController extends MinefieldController {

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
                            Sound.gameFailed();
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

    public AgainstAIController(int rows, int columns, int mines) throws IOException {
        super(rows, columns, mines);
    }

    //endregion

    //region Click Handling

    @Override
    void clickedOnLabel(MouseClickType type, int row, int column) {
        if (shouldStop) {
            return;
        }
    }

    //endregion

    //region Auto Sweeping

    void autoSweeping() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (manipulatedMinefield[i][j] == LabelType.CLICKED && minefield[i][j] != MinefieldType.EMPTY) {
                    if (countUnopenedMinesAround(i,j) == minefield[i][j].getCode() && countFlagsAround(i,j) != minefield[i][j].getCode()) {
                        flagsAllAround(i,j);
                    }
                }else if (countFlagsAround(i,j) == minefield[i][j].getCode() && countFlagsAround(i,j) != countUnopenedMinesAround(i,j)) {

                }
            }
        }
    }

    int countUnopenedMinesAround(int i, int j) {
        int unopenedNum = 0;
        try { if (manipulatedMinefield[i-1][j-1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i-1][ j ] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i-1][j+1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[ i ][j+1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][j+1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][ j ] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][j-1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[ i ][j-1] == LabelType.NOT_CLICKED) { unopenedNum++; } } catch (Exception ignored) {}
        return unopenedNum;
    }

    int countFlagsAround(int i, int j) {
        int flagsNum = 0;
        try { if (manipulatedMinefield[i-1][j-1] == LabelType.FLAGGED) { flagsNum++; }} catch (Exception ignored) {}
        try { if (manipulatedMinefield[i-1][ j ] == LabelType.FLAGGED) { flagsNum++; }} catch (Exception ignored) {}
        try { if (manipulatedMinefield[i-1][j+1] == LabelType.FLAGGED) { flagsNum++; }} catch (Exception ignored) {}
        try { if (manipulatedMinefield[ i ][j+1] == LabelType.FLAGGED) { flagsNum++; }} catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][j+1] == LabelType.FLAGGED) { flagsNum++; }} catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][ j ] == LabelType.FLAGGED) { flagsNum++; }} catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][j-1] == LabelType.FLAGGED) { flagsNum++; }} catch (Exception ignored) {}
        try { if (manipulatedMinefield[ i ][j-1] == LabelType.FLAGGED) { flagsNum++; }} catch (Exception ignored) {}
        return flagsNum;
    }

    /*int countMinesAround(int i, int j) {
        int minesNum = 0;
        try { if (manipulatedMinefield[i][j] == LabelType.BOMBED)}
    }*/

    void flagsAllAround(int i, int j) {
        try { clickedOnLabel(MouseClickType.SECONDARY,i-1,j-1); } catch (Exception ignored) {}
        try { clickedOnLabel(MouseClickType.SECONDARY,i-1,j+0 ); } catch (Exception ignored) {}
        try { clickedOnLabel(MouseClickType.SECONDARY,i-1,j+1); } catch (Exception ignored) {}
        try { clickedOnLabel(MouseClickType.SECONDARY,i+0,j+1); } catch (Exception ignored) {}
        try { clickedOnLabel(MouseClickType.SECONDARY,i+1,j+1); } catch (Exception ignored) {}
        try { clickedOnLabel(MouseClickType.SECONDARY,i+1,j+0); } catch (Exception ignored) {}
        try { clickedOnLabel(MouseClickType.SECONDARY,i+1,j-1); } catch (Exception ignored) {}
        try { clickedOnLabel(MouseClickType.SECONDARY,i+0,j-1); } catch (Exception ignored) {}
    }

    //endregion

    //region UI Updates

    @Override
    void updateInformativeLabels() {

    }

    //endregion

    //region Menu Items

    void initializeRightBorderPane() {

    }

    @Override
    void closeStage() {

    }

    //endregion
}

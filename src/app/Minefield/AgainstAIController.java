package app.Minefield;

import SupportingFiles.Audio.Sound;
import javafx.application.Platform;

import java.io.IOException;
import java.util.Random;

import static Extensions.Misc.Print.print;
import static app.PublicDefinitions.*;

public class AgainstAIController extends MinefieldController {

    //region Scores Computing & Player Switching



    //endregion

    //region Variables Declaration

    boolean isFirstClick = true;
    int discoveredMines = 0;
    boolean shouldStop = false;
    boolean isWin = false;

    boolean isSaved = false;

    int[] scores = new int[2];
    int[] mistakes = new  int[2];
    int currentPlayer = 0;

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
                    try { if (isWin) { Sound.win(); } else { Sound.gameOver(); } } catch (Exception ignored) {}
                    return;
                }else {
                    try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
                    autoSweeping();
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
                            }
                        }else {
                            Sound.uncover();
                            clickRecursively(row, column, row, column);
                            markGridLabel(row, column, LabelType.CLICKED);
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

    //region Auto Sweeping

    void autoSweeping() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (manipulatedMinefield[i][j] == LabelType.CLICKED && minefield[i][j] != MinefieldType.EMPTY) {
                    if (countUnopenedMinesAround(i,j) == minefield[i][j].getCode() && countFlagsAround(i,j) != minefield[i][j].getCode()) {
                        try { Thread.sleep(1000); }catch (Exception ignored) {}
                        flagsAllAround(i,j);
                        return;
                    }
                }else if (manipulatedMinefield[i][j] == LabelType.CLICKED && countFlagsAround(i,j) == minefield[i][j].getCode() && countFlagsAround(i,j) != countUnopenedMinesAround(i,j)) {
                    try { Thread.sleep(1000); } catch (Exception ignored) {}
                    clickedOnLabel(MouseClickType.TERTIARY,i,j);
                    return;
                }
            }
        }

        //Click randomly and avoid mines
        int x,y;
        Random random = new Random();
        while (true) {
            x = random.nextInt(rows);
            y = random.nextInt(columns);
            if (manipulatedMinefield[x][y] == LabelType.NOT_CLICKED && minefield[x][y] != MinefieldType.MINE) {
                try { Thread.sleep(1000); } catch (Exception ignored) {}
                clickedOnLabel(MouseClickType.PRIMARY,x,y);
                return;
            }else {
                continue;
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

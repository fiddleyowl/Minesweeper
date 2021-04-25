package app;

import java.io.IOException;
import java.util.Arrays;

import SupportingFiles.Sound;
import app.PublicDefinitions.*;

import static Extensions.Misc.Print.print;

public class MultiUserMinefieldController extends MinefieldController {

    //region Variable declaration

    private int currentPlayerCode;
    private int playersNum;
    private int[] scoreboard;
    private int[] playerMistakeNum;
    private int winnerPlayerCode;

    /**
     * <p>A two-dimensional array that stores which player click certain grid.</p>
     * <p>Default:all -1</p>
     */
    private int[][] mineClickedPlayer;

    //endregion

    //region Initializer & Data generation

    public MultiUserMinefieldController(int rows, int columns, int mines, int playersNum) throws IOException {
        super(rows, columns, mines,"MultiUserMinefield.fxml");
        this.playersNum = playersNum;
        this.currentPlayerCode = 0;
        scoreboard = new int[playersNum];
        playerMistakeNum = new int[playersNum];
        for (int i = 0; i < playersNum; i++) {
            scoreboard[i] = 0;
            playerMistakeNum[i] = 0;
        }
        mineClickedPlayer = new int[rows][columns];
        winnerPlayerCode = -1;
    }

    //endregion

    //region Click handling

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
                        //Every time flag a grid, add 1 point to the certain user's score, and check if the player make a mistake.
                        computeScore(currentPlayerCode);
                        computeMistake(rows,columns,currentPlayerCode);
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

    //endregion

    //region UI updates

    @Override
    public void checkIfShouldStop(int row, int column) {
        //  If clicked on mine, stop immediately.
//        if (manipulatedMinefield[row][column] == LabelType.BOMBED) {
 //           shouldStop = true;
        //   print(Arrays.deepToString(manipulatedMinefield));
  //          return;
       // }

        // Only when all mines are discovered. This requires correct flag numbers with no un-clicked labels.
        if (mines == discoveredMines) {
            // Mines are all flagged.
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (manipulatedMinefield[i][j] == LabelType.NOT_CLICKED || manipulatedMinefield[i][j] == LabelType.QUESTIONED) {
                        return;
                    }
                }
            }
            endGame(); //Compute which player will win.
            shouldStop = true;
        }

    }

    //endregion

    //region New method

    public void endCurrentRun() {
        if (currentPlayerCode == playersNum - 1) {
            currentPlayerCode = 0;
        } else {
            currentPlayerCode++;
        }
        if (Math.abs(scoreboard[0] - scoreboard[1]) > mines - discoveredMines) {
            winnerPlayerCode = currentPlayerCode;
        }
    }

    public void endGame(){
        if (scoreboard[0] == scoreboard[1]){
            winnerPlayerCode = (playerMistakeNum[0] != playerMistakeNum[1] && (playerMistakeNum[0] < playerMistakeNum[1])) ? 0 : 1;
        }else {
            winnerPlayerCode = (scoreboard[0] < scoreboard[1]) ? 1 : 0;
        }
    }

    //When a grid in manipulatedMinefield is clicked to FLAGGED, this method will be executed.
    public void computeScore(int currentPlayerCode) {
        scoreboard[currentPlayerCode]++;
    }

    public void computeMistake(int rows, int columns, int currentPlayerCode) {
        if (manipulatedMinefield[rows][columns] == LabelType.BOMBED) {
            playerMistakeNum[currentPlayerCode]++;
        }else if (manipulatedMinefield[rows][columns] == LabelType.FLAGGED && minefield[rows][columns] != MinefieldType.MINE){
            playerMistakeNum[currentPlayerCode]++;
        }
    }

    //endregion

}

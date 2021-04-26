package app;

import java.io.IOException;

import app.PublicDefinitions.*;

public class MultiUserMinefieldController extends MinefieldController {

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

    public MultiUserMinefieldController(int rows, int columns, int mines, int playersNum) throws IOException {
        super(rows, columns, mines);
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

}

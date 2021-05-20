package app.Minefield;

import SupportingFiles.Audio.Sound;
import SupportingFiles.DataEncoder;
import SupportingFiles.DataModels.GameModel;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import static Extensions.Misc.Print.print;
import static Extensions.TypeCasting.CastString.String;
import static app.PublicDefinitions.*;
import static SupportingFiles.ConfigHelper.*;

public class AgainstAIController extends MinefieldController {

    //region Variables Declaration

    public AutoSweeper ai = new AutoSweeper(this);

    public boolean isWin = false;

    public boolean isSaved = false;

    public int[] scores = new int[2];
    public int[] mistakes = new int[2];

    /**
     * 1 for a mine, -1 for not, 0 for uncertain
     */
    public int[][] isTheCellAMine = new int[rows][columns];

    public int currentPlayerIndex = 0;
    public int winnerIndex = -1;

    public AIDifficulty aiDifficulty = AIDifficulty.MEDIUM;

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
                    winnerIndex = computeFinalWinnerIndex();
                    music.stop();
                    try {
                        Sound.gameOver();
                    } catch (Exception ignored) {}
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

    public AgainstAIController(int rows, int columns, int mines, AIDifficulty aiDifficulty) throws IOException {
        super(rows, columns, mines);
        this.aiDifficulty = aiDifficulty;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                isTheCellAMine[i][j] = 0;
            }
        }
        mainStage.setTitle("Minesweeper - Computer Level " + aiDifficulty.getName());
        initializeRightBorderPane();
    }

    public AgainstAIController(GameModel gameModel, String savePath) throws IOException {
        super(gameModel,savePath);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                isTheCellAMine[i][j] = 0;
            }
        }
        applyGameModel(gameModel);
        mainStage.setTitle("Minesweeper - Computer Level " + aiDifficulty.getName() + " - " + Paths.get(savePath).getFileName());
    }

    public AgainstAIController(int rows, int columns, int mines, AIDifficulty aiDifficulty, MinefieldType[][] minefield) throws IOException {
        super(rows,columns,mines,minefield);
        this.aiDifficulty = aiDifficulty;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                isTheCellAMine[i][j] = 0;
            }
        }
        mainStage.setTitle("Minesweeper - Computer Level " + aiDifficulty.getName());
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                this.minefield[row][column] = minefield[row][column];
            }
        }
        initializeRightBorderPane();
        scores = new int[2];
        mistakes = new int[2];
        thread.start();
        isFirstClick = false;
    }

    @Override
    public void playSameBoard() throws IOException {
        closeStage();
        AgainstAIController againstAIController = new AgainstAIController(rows,columns,mines,aiDifficulty,minefield);
    }

    boolean shouldUseCurrentTimeAsStartTime = true;

    public void applyGameModel(GameModel gameModel) {
        this.aiDifficulty = gameModel.aiDifficulty;
        this.rounds = gameModel.rounds;
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
        scores = new int[2];
        mistakes = new int[2];
        for (int i = 0; i < 2; i++) {
            scores[i] = gameModel.players[i].score;
            mistakes[i] = gameModel.players[i].mistakes;
        }
        if (!isFirstClick) {
            thread.start();
            startTime = System.currentTimeMillis() - gameModel.timeUsed;
            shouldUseCurrentTimeAsStartTime = false;
        }
        initializeRightBorderPane();
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
                print("The square has been clicked");
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
                                computeScores(MouseClickType.PRIMARY, row, column, true);
                            }
                        } else {
                            Sound.uncover();
                            clickRecursively(row, column, row, column);
                            markGridLabel(row, column, LabelType.CLICKED);
                            switchPlayer();
                        }
                        break;

                    case SECONDARY:
                        if (minefield[row][column] == MinefieldType.MINE) {
                            Sound.flagCorrectly();
                            discoveredMines += 1;
                            markGridLabel(row, column, LabelType.CORRECT);
                            computeScores(MouseClickType.SECONDARY, row ,column, false);
                        } else {
                            Sound.flagWrongly();
//                            clickRecursively(row, column, row, column);
                            markGridLabel(row, column, LabelType.CLICKED);
                            markSquareAsWrong(row,column);
                            computeScores(MouseClickType.SECONDARY, row, column, true);
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                print("Default");
                return;
        }
        updateInformativeLabels();
        if (isFirstClick) {
            thread.start();
        }
        isFirstClick = false;
        checkIfShouldStop();
        print("Clicked Type: %s, Row: %d, Column: %d\n", type, row + 1, column + 1);
        autoSweeping(aiDifficulty);
    }

    boolean clickedOnLabel_Robot(MouseClickType type, int row, int column) {
        if (shouldStop) {
            return false;
        }
        boolean isClickedOnUnopenedSquare = false;
        switch (manipulatedMinefield[row][column]) {
            case CLICKED:
                print("The square has been clicked");
                return false;
            case NOT_CLICKED:
                switch (type) {

                    case PRIMARY:
                        if (minefield[row][column] == MinefieldType.MINE) {
                            Sound.flagWrongly();
                            discoveredMines += 1;
                            markGridLabel(row, column, LabelType.BOMBED);
                            computeScores(MouseClickType.PRIMARY, row, column, true);
                        } else {
                            Sound.uncover();
                            clickRecursively(row, column, row, column);
                            markGridLabel(row, column, LabelType.CLICKED);
                            switchPlayer();
                        }
                        break;

                    case SECONDARY:
                        if (minefield[row][column] == MinefieldType.MINE) {
                            Sound.flagCorrectly();
                            discoveredMines += 1;
                            markGridLabel(row, column, LabelType.CORRECT);
                            computeScores(MouseClickType.SECONDARY, row ,column, false);
                        } else {
                            Sound.flagWrongly();
//                            clickRecursively(row, column, row, column);
                            markGridLabel(row, column, LabelType.CLICKED);
                            markSquareAsWrong(row, column);
                            computeScores(MouseClickType.SECONDARY, row ,column, true);
                        }
                        break;
                    default:
                        break;
                }
                isClickedOnUnopenedSquare = true;
                break;
            default:
                print("Default");
                return false;
        }
        highlightSquare(row,column);
        updateInformativeLabels();
        checkIfShouldStop();
        print("Robot Clicked Type: %s, Row: %d, Column: %d\n", type, row + 1, column + 1);
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
        if (aiDifficulty == AIDifficulty.IMPOSSIBLE) {
            autoSweeping_impossible();
            return;
        }
    }

    void autoSweeping_easy() {
        if (shouldStop) {
            return;
        }

//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                computeIfSurroundingCellsAreSafe(i, j);
//            }
//        }
//
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                if (ai.flagWithSomeDeduction(i, j)) {
//                    print("Deduction.");
//                    return; }
//            }
//        }

        while (true) {
            Random random = new Random();
            int x = random.nextInt(rows);
            int y = random.nextInt(columns);
            int clickType = random.nextInt(2);
            if (manipulatedMinefield[x][y] == LabelType.NOT_CLICKED && clickedOnLabel_Robot(MouseClickType(clickType), x, y)) {
                print("Robot has just clicked randomly.");
                return;
            } else {
                print("Robot clicks unsuccessfully. Click again.");
            }
        }
    }

    void autoSweeping_medium() {
        if (shouldStop) {
            return;
        }

        if(ai.sweepAllBasedOnDefinition()) { return; }

        //Click randomly and avoid mines
        while (true) {
            Random random = new Random();
            int x = random.nextInt(rows);
            int y = random.nextInt(columns);
            int clickType = random.nextInt(2);
            if (manipulatedMinefield[x][y] == LabelType.NOT_CLICKED && clickedOnLabel_Robot(MouseClickType(clickType), x, y)) {
                print("Click randomly.");
                return;
            } else {
                print("Robot avoided a mine or clicked unsuccessfully. Click again.");
            }
        }
    }

    void autoSweeping_difficult() {
        if (shouldStop) {
            return;
        }

        if (ai.flagWithSomeDeduction()) { return; }

        if(ai.sweepAllBasedOnDefinition()) { return; }

        for (int i = 0; i < rows; i++) {
            for (int j = 0 ; j < columns; j++) {
                if (manipulatedMinefield[i][j] == LabelType.NOT_CLICKED) {
                    if (isABoundryCell(i, j)) {
                        print("The cell is a boundry cell.");
                        switch (minefield[i][j]) {
                            case MINE -> clickedOnLabel_Robot(MouseClickType.SECONDARY, i, j);
                            default -> clickedOnLabel_Robot(MouseClickType.PRIMARY, i, j);
                        }
                        return;
                    }
                }
            }
        }

        //Click randomly and avoid mines
        while (true) {
            Random random = new Random();
            int x = random.nextInt(rows);
            int y = random.nextInt(columns);
            if (manipulatedMinefield[x][y] == LabelType.NOT_CLICKED && isTheCellAMine[x][y] != -1 && clickedOnLabel_Robot(MouseClickType.PRIMARY, x, y)) {
                print("Robot has just clicked randomly.");
                return;
            } else {
                print("Robot avoided a mine or clicked unsuccessfully. Click again.");
            }
        }
    }

    void autoSweeping_impossible() {
        if (shouldStop) { return; }
        Random random = new Random();
        while (true) {
            int x = random.nextInt(rows);
            int y = random.nextInt(columns);
            if (manipulatedMinefield[x][y] == LabelType.NOT_CLICKED && minefield[x][y] == MinefieldType.MINE && clickedOnLabel_Robot(MouseClickType.SECONDARY, x, y)) { return; }
        }
    }

    void computeIfSurroundingCellsAreSafe() {
        for (int i = 0; i <rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (manipulatedMinefield[i][j] != LabelType.CLICKED) { continue; }
                if (countFlagsAround(i, j) == minefield[i][j].getCode()) {
                    try { if (manipulatedMinefield[i-1][j-1] == LabelType.NOT_CLICKED) { isTheCellAMine[i-1][j-1] = -1; } } catch (Exception ignored) { }
                    try { if (manipulatedMinefield[i-1][ j ] == LabelType.NOT_CLICKED) { isTheCellAMine[i-1][ j ] = -1; } } catch (Exception ignored) { }
                    try { if (manipulatedMinefield[i-1][j+1] == LabelType.NOT_CLICKED) { isTheCellAMine[i-1][j+1] = -1; } } catch (Exception ignored) { }
                    try { if (manipulatedMinefield[ i ][j+1] == LabelType.NOT_CLICKED) { isTheCellAMine[ i ][j+1] = -1; } } catch (Exception ignored) { }
                    try { if (manipulatedMinefield[i+1][j+1] == LabelType.NOT_CLICKED) { isTheCellAMine[i+1][j+1] = -1; } } catch (Exception ignored) { }
                    try { if (manipulatedMinefield[i+1][ j ] == LabelType.NOT_CLICKED) { isTheCellAMine[i+1][ j ] = -1; } } catch (Exception ignored) { }
                    try { if (manipulatedMinefield[i+1][j-1] == LabelType.NOT_CLICKED) { isTheCellAMine[i+1][j-1] = -1; } } catch (Exception ignored) { }
                    try { if (manipulatedMinefield[ i ][j-1] == LabelType.NOT_CLICKED) { isTheCellAMine[ i ][j-1] = -1; } } catch (Exception ignored) { }
                }
            }
        }
    }

    ArrayList<Point> getUnopenedAndSafeCellAround(int i, int j) {
        computeIfSurroundingCellsAreSafe();
        ArrayList<Point> res = new ArrayList<>();
        try { if (manipulatedMinefield[i-1][j-1] == LabelType.NOT_CLICKED && isTheCellAMine[i-1][j-1] == -1) { res.add(new Point(i-1, j-1)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i-1][ j ] == LabelType.NOT_CLICKED && isTheCellAMine[i-1][ j ] == -1) { res.add(new Point(i-1, j+0)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i-1][j+1] == LabelType.NOT_CLICKED && isTheCellAMine[i-1][j+1] == -1) { res.add(new Point(i-1, j+1)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[ i ][j+1] == LabelType.NOT_CLICKED && isTheCellAMine[ i ][j+1] == -1) { res.add(new Point(i+0, j+1)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][j+1] == LabelType.NOT_CLICKED && isTheCellAMine[i+1][j+1] == -1) { res.add(new Point(i+1, j+1)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][ j ] == LabelType.NOT_CLICKED && isTheCellAMine[i+1][ j ] == -1) { res.add(new Point(i+1, j+0)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][j-1] == LabelType.NOT_CLICKED && isTheCellAMine[i+1][j-1] == -1) { res.add(new Point(i+1, j-1)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[ i ][j-1] == LabelType.NOT_CLICKED && isTheCellAMine[ i ][j-1] == -1) { res.add(new Point(i+0, j-1)); } } catch (Exception ignored) {}
        return res;
    }

    ArrayList<Point> getUnopenedCellsAround(int i, int j) {
        ArrayList<Point> res = new ArrayList<>();
        try { if (manipulatedMinefield[i-1][j-1] == LabelType.NOT_CLICKED) { res.add(new Point(i-1, j-1)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i-1][ j ] == LabelType.NOT_CLICKED) { res.add(new Point(i-1, j+0)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i-1][j+1] == LabelType.NOT_CLICKED) { res.add(new Point(i-1, j+1)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[ i ][j+1] == LabelType.NOT_CLICKED) { res.add(new Point(i+0, j+1)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][j+1] == LabelType.NOT_CLICKED) { res.add(new Point(i+1, j+1)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][ j ] == LabelType.NOT_CLICKED) { res.add(new Point(i+1, j+0)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[i+1][j-1] == LabelType.NOT_CLICKED) { res.add(new Point(i+1, j-1)); } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[ i ][j-1] == LabelType.NOT_CLICKED) { res.add(new Point(i+0, j-1)); } } catch (Exception ignored) {}
        return res;
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

    boolean isPointInRange(int x, int y) {
        return x >= 0 && x < rows && y >= 0 && y < columns;
    }

    boolean isABoundryCell(int x, int y) {
        if (!isPointInRange(x, y)) { return false; }
        int num = 0;
        try { if (manipulatedMinefield[x-1][y-1] == LabelType.CLICKED) { num++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[x-1][ y ] == LabelType.CLICKED) { num++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[x-1][y+1] == LabelType.CLICKED) { num++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[ x ][y+1] == LabelType.CLICKED) { num++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[x+1][y+1] == LabelType.CLICKED) { num++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[x+1][ y ] == LabelType.CLICKED) { num++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[x+1][y-1] == LabelType.CLICKED) { num++; } } catch (Exception ignored) {}
        try { if (manipulatedMinefield[ x ][y-1] == LabelType.CLICKED) { num++; } } catch (Exception ignored) {}
        if (num >= 1) { return true; }
        return false;
    }

//    public int[][] getBoard() {
//        int[][] res = new int[rows][columns];
//        for (int i = 0; i < rows; i++) {
//            for (int j = 0; j < columns; j++) {
//                switch (manipulatedMinefield[i][j]) {
//                    case NOT_CLICKED -> res[i][j] = MineSweeper.UNCHECKED;
//                    case CLICKED -> res[i][j] = minefield[i][j].getCode();
//                    case BOMBED, CORRECT -> res[i][j] = MineSweeper.FLAG;
//                }
//            }
//        }
//        return res;
//    }

    //endregion

    //region Scores Computing & Player Switching

    public void computeScores(MouseClickType type, int row, int column, boolean isWrong) {
        switch (type) {
            case PRIMARY -> {
                if (manipulatedMinefield[row][column] == LabelType.BOMBED) {
                    scores[currentPlayerIndex]--;
                }
            }
            case SECONDARY -> {
                if (!isWrong) { scores[currentPlayerIndex]++; }
                else { mistakes[currentPlayerIndex]++; }
            }
        }
//        print("Scores:");
//        print(scores);
//        print("Mistakes:");
//        print(mistakes);
        switchPlayer();
    }

    public void switchPlayer() {
//        timeline.stop();
        if (currentPlayerIndex == 0) {
            currentPlayerIndex = 1;
        } else {
            rounds += 1;
            currentPlayerIndex = 0;
        }
    }

    public int computeFinalWinnerIndex() {
        if (scores[0] != scores[1]) {
            return (scores[0] > scores[1]) ? 0 : 1;
        } else {
            if (mistakes[0] != mistakes[1]) {
                return (mistakes[0] < mistakes[1]) ? 0 : 1;
            } else {
                return -1;
            }
        }
    }

    //endregion

    //region UI Updates

    public void checkIfShouldStop() {
        if (mines == discoveredMines) {
            // Mines are all flagged.
            shouldStop = true;
        }
        if (Math.abs(scores[0] - scores[1]) > mines - discoveredMines) { shouldStop = true; }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (manipulatedMinefield[i][j] == LabelType.NOT_CLICKED || manipulatedMinefield[i][j] == LabelType.QUESTIONED) {
                    return;
                }
            }
        }
        shouldStop = true;
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
        roundLabel.setText(String(rounds));
        try {
            playerInformationVBox1.scoreLabel.setText(String(scores[1]));
            playerInformationVBox1.mistakesLabel.setText(String(mistakes[1]));
            playerInformationVBox0.scoreLabel.setText(String(scores[0]));
            playerInformationVBox0.mistakesLabel.setText(String(mistakes[0]));
        } catch (Exception ignored) { }

    }

    public void highlightSquare(int row, int column) {
        if (!isHighlightComputersMoveEnabled()) {
            return;
        }
        ObservableList<Node> childrens = minefieldGridPane.getChildren();
        for (Node children : childrens) {
            children.getStyleClass().remove("minefieldLabelHighlighted");
            if (GridPane.getColumnIndex(children) == column && GridPane.getRowIndex(children) == row) {
                Label label = (Label) children;
                children.getStyleClass().add("minefieldLabelHighlighted");
            }
        }
    }

    public void markSquareAsWrong(int row, int column) {
        if (!isMarkIncorrectSquaresEnabled()) {
            return;
        }
        ObservableList<Node> childrens = minefieldGridPane.getChildren();
        for (Node children : childrens) {
            if (GridPane.getColumnIndex(children) == column && GridPane.getRowIndex(children) == row) {
                Label label = (Label) children;
                children.getStyleClass().add("minefieldLabelWrong");
            }
        }
    }

    public void endGame() throws IOException {
        double clickedX = gesturePane.getHeight()/2.0;
        double clickedY = gesturePane.getWidth()/2.0;
        Point2D point = new Point2D(clickedX,clickedY);
        gesturePane.animate(Duration.millis(200)).centreOn(point);
        gesturePane.animate(Duration.millis(200)).zoomTo(gesturePane.getMinScale(),point);
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
        closeStage();
        AgainstAIController againstAIController = new AgainstAIController(rows, columns, mines, aiDifficulty);
    }

    @Override
    void closeStage() {
        mainStage.setFullScreen(false);
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
                mainStage.setTitle("Minesweeper - Computer Level " + aiDifficulty.getName() + " - " + Paths.get(savePath).getFileName());
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

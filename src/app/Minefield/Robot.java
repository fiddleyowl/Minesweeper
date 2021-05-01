package app.Minefield;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import static app.PublicDefinitions.*;

public class Robot {

    private AgainstAIController againstAIController;

    //region Normal Robot

    public Robot(AgainstAIController againstAIController){
        this.againstAIController = againstAIController;
    }

    Random random = new Random();

    public void clickRandomly() {
        int i = random.nextInt(againstAIController.minefield.length);
        int j = random.nextInt(againstAIController.minefield[0].length);
        if (againstAIController.manipulatedMinefield[i][j] != LabelType.NOT_CLICKED) {
            clickRandomly();
        } else {
            int temp = random.nextInt(4);
            //Flag the spot with a percentage of 25%.
            if (temp == 0) {
                againstAIController.clickedOnLabel(MouseClickType.SECONDARY, i, j);
            } else {
                againstAIController.clickedOnLabel(MouseClickType.PRIMARY, i, j);
            }
        }
    }

    public void clickLikeAProfessor() {
        int i = random.nextInt(againstAIController.minefield.length);
        int j = random.nextInt(againstAIController.minefield[0].length);
        if (againstAIController.manipulatedMinefield[i][j] != LabelType.NOT_CLICKED) {
            clickLikeAProfessor();
        } else {
            if (againstAIController.minefield[i][j] == MinefieldType.MINE) {
                againstAIController.clickedOnLabel(MouseClickType.SECONDARY, i, j);
            } else {
                againstAIController.clickedOnLabel(MouseClickType.PRIMARY, i, j);
            }
        }
    }

    public void clickLikeAAmateur() {
        boolean probability = random.nextInt(3) == 0;
        int i = random.nextInt(againstAIController.minefield.length);
        int j = random.nextInt(againstAIController.minefield[0].length);
        if (againstAIController.manipulatedMinefield[i][j] != LabelType.NOT_CLICKED) {
            clickLikeAAmateur();
        } else {
            if (againstAIController.minefield[i][j] == MinefieldType.MINE) {
                if (probability) {
                    againstAIController.clickedOnLabel(MouseClickType.PRIMARY, i, j);
                } else {
                    againstAIController.clickedOnLabel(MouseClickType.SECONDARY, i, j);
                }
            } else {
                againstAIController.clickedOnLabel(MouseClickType.PRIMARY, i, j);
            }
        }
    }

    //endregion

    //region MSolver

    class MSolver {

        protected int[][] onScreen = null;
        protected boolean[][] flags = null;
        protected int numMines = 0;
        protected int TOT_MINES = againstAIController.mines;
        protected int rows = againstAIController.rows;
        protected int columns = againstAIController.columns;

        public int detect(int i, int j){
            LabelType[][] temp_l = againstAIController.manipulatedMinefield;
            MinefieldType[][] temp_m = againstAIController.minefield;
            if (temp_l[i][j] == LabelType.NOT_CLICKED || temp_l[i][j] == LabelType.FLAGGED || temp_l[i][j] == LabelType.QUESTIONED) { return -1; }
            if (temp_l[i][j] == LabelType.BOMBED){ return -3; }
            if (temp_m[i][j] == MinefieldType.EMPTY){ return 0; }
            if (temp_m[i][j] == MinefieldType.ONE)  { return 1; }
            if (temp_m[i][j] == MinefieldType.TWO)  { return 2; }
            if (temp_m[i][j] == MinefieldType.THREE){ return 3; }
            if (temp_m[i][j] == MinefieldType.FOUR) { return 4; }
            if (temp_m[i][j] == MinefieldType.FIVE) { return 5; }
            if (temp_m[i][j] == MinefieldType.SIX)  { return 6; }
            if (temp_m[i][j] == MinefieldType.SEVEN){ return 7; }
            if (temp_m[i][j] == MinefieldType.EIGHT){ return 8; }
            return -10;
        }

        //todo Maybe this method need a little change.
        public int onScreen(int i, int j){
            return onScreen[i][j];
        }

        public int updateOnScreen(){
            int numMines_t = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int d = detect(i, j);
                    if (d == -3 || flags[i][j]){
                        onScreen[i][j] = -1;
                        flags[i][j] = true;
                    }
                    if (d == -1){
                        flags[i][j] = false;
                    }

                    if (flags[i][j]){
                        numMines_t++;
                    }
                }
            }
            numMines = numMines_t;
            return 0;
        }

        public boolean checkConsistency(){
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    int freeSquares = countFreeSquaresAround(onScreen, i, j);
                    int numFlags = countFlagsAround(flags, i , j);

                    if (onScreen(i, j) == 0 && freeSquares > 0) {
                        return false;
                    }
                    if ((onScreen(i, j) - numFlags) > 0 && freeSquares == 0) {
                        return false;
                    }

                }
            }
            return true;
        }

        //todo:Make the firstSquare() to click on a certain spot on the borad(not just the center)
        public void firstSquare() throws Throwable {
            Thread.sleep(20);
            updateOnScreen();
            boolean isUntouched = true;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (onScreen(i,j) != -1) {
                        isUntouched = false;
                    }
                }
            }
            if (!isUntouched) {
                return;
            }

            againstAIController.clickedOnLabel(MouseClickType.PRIMARY,rows/2,columns/2);
            Thread.sleep(200);
        }

        public int countFlagsAround(boolean[][] array, int i, int j){
            int mines = 0;
            boolean oU = false, oD = false, oL = false, oR = false;
            if (i == 0) oU = true;
            if (j == 0) oL = true;
            if (i == rows - 1) oD = true;
            if (j == columns - 1) oR = true;

            if (!oU && array[i-1][ j ]) mines++;
            if (!oL && array[ i ][j-1]) mines++;
            if (!oD && array[i+1][ j ]) mines++;
            if (!oR && array[ i ][j+1]) mines++;
            if (!oU && !oL && array[i - 1][j - 1]) mines++;
            if (!oU && !oR && array[i - 1][j + 1]) mines++;
            if (!oD && !oL && array[i + 1][j - 1]) mines++;
            if (!oD && !oR && array[i + 1][j + 1]) mines++;

            return mines;
        }

        public int countFreeSquaresAround(int[][] board, int i, int j) {
            int freeSquares = 0;

            if (onScreen(i - 1, j) == -1) freeSquares++;
            if (onScreen(i + 1, j) == -1) freeSquares++;
            if (onScreen(i, j - 1) == -1) freeSquares++;
            if (onScreen(i, j + 1) == -1) freeSquares++;
            if (onScreen(i - 1, j - 1) == -1) freeSquares++;
            if (onScreen(i - 1, j + 1) == -1) freeSquares++;
            if (onScreen(i + 1, j - 1) == -1) freeSquares++;
            if (onScreen(i + 1, j + 1) == -1) freeSquares++;

            return freeSquares;
        }

        public boolean isBoundry(int[][] board, int i, int j) {
            if (board[i][j] != -1) return false;

            boolean oU = false;
            boolean oD = false;
            boolean oL = false;
            boolean oR = false;
            if (i == 0) oU = true;
            if (j == 0) oL = true;
            if (i == rows - 1) oD = true;
            if (j == columns - 1) oR = true;
            boolean isBoundry = false;

            if (!oU && board[i - 1][j] >= 0) isBoundry = true;
            if (!oL && board[i][j - 1] >= 0) isBoundry = true;
            if (!oD && board[i + 1][j] >= 0) isBoundry = true;
            if (!oR && board[i][j + 1] >= 0) isBoundry = true;
            if (!oU && !oL && board[i - 1][j - 1] >= 0) isBoundry = true;
            if (!oU && !oR && board[i - 1][j + 1] >= 0) isBoundry = true;
            if (!oD && !oL && board[i + 1][j - 1] >= 0) isBoundry = true;
            if (!oD && !oR && board[i + 1][j + 1] >= 0) isBoundry = true;

            return isBoundry;
        }

        public void attemptFlagMine() throws Throwable {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {

                    if (onScreen(i, j) >= 1) {
                        int curNum = onScreen(i, j);

                        // Flag necessary squares
                        if (curNum == countFreeSquaresAround(onScreen, i, j)) {
                            for (int ii = 0; ii < rows; ii++) {
                                for (int jj = 0; jj < columns; jj++) {
                                    if (Math.abs(ii - i) <= 1 && Math.abs(jj - j) <= 1) {
                                        if (onScreen(ii, jj) == -1 && !flags[ii][jj]) {
                                            flags[ii][jj] = true;
                                            againstAIController.clickedOnLabel(MouseClickType.SECONDARY, ii, jj);
                                        }
                                    }
                                }
                            }
                        }


                    }
                }
            }
        }

        public void attemptMove() throws Throwable {

            boolean success = false;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {

                    if (onScreen(i, j) >= 1) {

                        // Count how many mines around it
                        int curNum = onScreen[i][j];
                        int mines = countFlagsAround(flags, i, j);
                        int freeSquares = countFreeSquaresAround(onScreen, i, j);


                        // Click on the deduced non-mine squares
                        if (curNum == mines && freeSquares > mines) {
                            success = true;

                            // Use the chord or the classical algorithm
                            if (freeSquares - mines > 1) {
                                againstAIController.clickedOnLabel(MouseClickType.TERTIARY, i, j);
                                onScreen[i][j] = 0; // hack to make it not overclick a square
                                continue;
                            }

                            // Old algorithm: don't chord
                            for (int ii = 0; ii < rows; ii++) {
                                for (int jj = 0; jj < columns; jj++) {
                                    if (Math.abs(ii - i) <= 1 && Math.abs(jj - j) <= 1) {
                                        if (onScreen(ii, jj) == -1 && !flags[ii][jj]) {
                                            againstAIController.clickedOnLabel(MouseClickType.PRIMARY, ii, jj);
                                            onScreen[ii][jj] = 0;
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

            if (success) return;

            // Bring in the big guns
            tankSolver();

        }

        public void guessRandomly() throws Throwable {
            System.out.println("Attempting to guess randomly");
            while (true) {
                int k = random.nextInt(rows * columns);
                int i = k / columns;
                int j = k % columns;

                if (onScreen(i, j) == -1 && !flags[i][j]) {
                    againstAIController.clickedOnLabel(MouseClickType.PRIMARY, i, j);
                    return;
                }
            }
        }

        public void tankSolver() throws Throwable {
            Thread.sleep(50);
            if (!checkConsistency()) { return; }

            //Timing
            long tankTime = System.currentTimeMillis();

            ArrayList<Pair> borderTiles = new ArrayList<Pair>();
            ArrayList<Pair> allEmptyTiles = new ArrayList<Pair>();

            // Endgame case: if there are few enough tiles, don't bother with border tiles.
            borderOptimization = false;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (onScreen(i, j) == -1 && !flags[i][j])
                        allEmptyTiles.add(new Pair(i, j));
                }
            }

            // Determine all border tiles
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (isBoundry(onScreen, i, j) && !flags[i][j])
                        borderTiles.add(new Pair(i, j));
                }
            }

            // Count how many squares outside the knowable range
            int numOutSquares = allEmptyTiles.size() - borderTiles.size();
            if (numOutSquares > BF_LIMIT) {
                borderOptimization = true;
            } else { borderTiles = allEmptyTiles; }

            // Something went wrong
            if (borderTiles.size() == 0) { return; }

            // Run the segregation routine before recursing one by one
            // Don't bother if it's endgame as doing so might make it miss some cases
            ArrayList<ArrayList<Pair>> segregated;
            if (!borderOptimization) {
                segregated = new ArrayList<>();
                segregated.add(borderTiles);
            } else { segregated = tankSegregate(borderTiles); }

            int totalMultCases = 1;
            boolean success = false;
            double prob_best = 0; // Store information about the best probability
            int prob_besttile = -1;
            int prob_best_s = -1;
            for (int s = 0; s < segregated.size(); s++) {

                // Copy everything into temporary constructs
                tank_solutions = new ArrayList<boolean[]>();
                tank_board = onScreen.clone();
                knownMine = flags.clone();

                knownEmpty = new boolean[rows][columns];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < columns; j++) {
                        if (tank_board[i][j] >= 0){ knownEmpty[i][j] = true; }
                        else { knownEmpty[i][j] = false; }
                    }
                }
                // Compute solutions -- here's the time consuming step
                tankRecurse(segregated.get(s), 0);

                // Something screwed up
                if (tank_solutions.size() == 0) { return; }


                // Check for solved squares
                for (int i = 0; i < segregated.get(s).size(); i++) {
                    boolean allMine = true;
                    boolean allEmpty = true;
                    for (boolean[] sln : tank_solutions) {
                        if (!sln[i]) allMine = false;
                        if (sln[i]) allEmpty = false;
                    }


                    Pair<Integer, Integer> q = segregated.get(s).get(i);
                    int qi = q.getFirst();
                    int qj = q.getSecond();

                    // Muahaha
                    if (allMine) {
                        flags[qi][qj] = true;
                        againstAIController.clickedOnLabel(MouseClickType.SECONDARY, qi, qj);
                    }
                    if (allEmpty) {
                        success = true;
                        againstAIController.clickedOnLabel(MouseClickType.PRIMARY, qi, qj);
                    }
                }

                totalMultCases *= tank_solutions.size();


                // Calculate probabilities, in case we need it
                if (success) continue;
                int maxEmpty = -10000;
                int iEmpty = -1;
                for (int i = 0; i < segregated.get(s).size(); i++) {
                    int nEmpty = 0;
                    for (boolean[] sln : tank_solutions) {
                        if (!sln[i]) nEmpty++;
                    }
                    if (nEmpty > maxEmpty) {
                        maxEmpty = nEmpty;
                        iEmpty = i;
                    }
                }
                double probability = (double) maxEmpty / (double) tank_solutions.size();

                if (probability > prob_best) {
                    prob_best = probability;
                    prob_besttile = iEmpty;
                    prob_best_s = s;
                }

            }

            // But wait! If there's any hope, bruteforce harder (by a factor of 32x)!
            if (BF_LIMIT == 8 && numOutSquares > 8 && numOutSquares <= 13) {
                System.out.println("Extending bruteforce horizon...");
                BF_LIMIT = 13;
                tankSolver();
                BF_LIMIT = 8;
                return;
            }

            tankTime = System.currentTimeMillis() - tankTime;
            if (success) {
                System.out.printf(
                        "TANK Solver successfully invoked at step %d (%dms, %d cases)%s\n",
                        numMines, tankTime, totalMultCases, (borderOptimization ? "" : "*"));
                return;
            }

            // Take the guess, since we can't deduce anything useful
            System.out.printf(
                    "TANK Solver guessing with probability %1.2f at step %d (%dms, %d cases)%s\n",
                    prob_best, numMines, tankTime, totalMultCases,
                    (borderOptimization ? "" : "*"));
            Pair<Integer, Integer> q = segregated.get(prob_best_s).get(prob_besttile);
            int qi = q.getFirst();
            int qj = q.getSecond();
            againstAIController.clickedOnLabel(MouseClickType.PRIMARY, qi, qj);

        }

        public ArrayList<ArrayList<Pair>> tankSegregate(ArrayList<Pair> borderTiles) {

            ArrayList<ArrayList<Pair>> allRegions = new ArrayList<ArrayList<Pair>>();
            ArrayList<Pair> covered = new ArrayList<Pair>();

            while (true) {

                LinkedList<Pair> queue = new LinkedList<Pair>();
                ArrayList<Pair> finishedRegion = new ArrayList<Pair>();

                // Find a suitable starting point
                for (Pair firstT : borderTiles) {
                    if (!covered.contains(firstT)) {
                        queue.add(firstT);
                        break;
                    }
                }

                if (queue.isEmpty())
                    break;

                while (!queue.isEmpty()) {

                    Pair<Integer, Integer> curTile = queue.poll();
                    int ci = curTile.getFirst();
                    int cj = curTile.getSecond();

                    finishedRegion.add(curTile);
                    covered.add(curTile);

                    // Find all connecting tiles
                    for (Pair<Integer, Integer> tile : borderTiles) {
                        int ti = tile.getFirst();
                        int tj = tile.getSecond();

                        boolean isConnected = false;

                        if (finishedRegion.contains(tile))
                            continue;

                        if (Math.abs(ci - ti) > 2 || Math.abs(cj - tj) > 2)
                            isConnected = false;

                        else {
                            // Perform a search on all the tiles
                            tilesearch:
                            for (int i = 0; i < rows; i++) {
                                for (int j = 0; j < columns; j++) {
                                    if (onScreen(i, j) > 0) {
                                        if (Math.abs(ci - i) <= 1 && Math.abs(cj - j) <= 1 &&
                                                Math.abs(ti - i) <= 1 && Math.abs(tj - j) <= 1) {
                                            isConnected = true;
                                            break tilesearch;
                                        }
                                    }
                                }
                            }
                        }

                        if (!isConnected) continue;

                        if (!queue.contains(tile))
                            queue.add(tile);

                    }
                }

                allRegions.add(finishedRegion);

            }

            return allRegions;
        }

        public void tankRecurse(ArrayList<Pair> borderTiles, int k) {

            // Return if at this point, it's already inconsistent
            int flagCount = 0;
            for (int i = 0; i < rows; i++)
                for (int j = 0; j < columns; j++) {

                    // Count flags for endgame cases
                    if (knownMine[i][j])
                        flagCount++;

                    int num = tank_board[i][j];
                    if (num < 0) continue;

                    // Total bordering squares
                    int surround = 0;
                    if ((i == 0 && j == 0) || (i == rows - 1 && j == columns - 1))
                        surround = 3;
                    else if (i == 0 || j == 0 || i == rows - 1 || j == columns - 1)
                        surround = 5;
                    else surround = 8;

                    int numFlags = countFlagsAround(knownMine, i, j);
                    int numFree = countFlagsAround(knownEmpty, i, j);

                    // Scenario 1: too many mines
                    if (numFlags > num) return;

                    // Scenario 2: too many empty
                    if (surround - numFree < num) return;
                }

            // We have too many flags
            if (flagCount > TOT_MINES)
                return;


            // Solution found!
            if (k == borderTiles.size()) {

                // We don't have the exact mine count, so no
                if (!borderOptimization && flagCount < TOT_MINES)
                    return;

                boolean[] solution = new boolean[borderTiles.size()];
                for (int i = 0; i < borderTiles.size(); i++) {
                    Pair<Integer, Integer> s = borderTiles.get(i);
                    int si = s.getFirst();
                    int sj = s.getSecond();
                    solution[i] = knownMine[si][sj];
                }
                tank_solutions.add(solution);
                return;
            }

            Pair<Integer, Integer> q = borderTiles.get(k);
            int qi = q.getFirst();
            int qj = q.getSecond();

            // Recurse two positions: mine and no mine
            knownMine[qi][qj] = true;
            tankRecurse(borderTiles, k + 1);
            knownMine[qi][qj] = false;

            knownEmpty[qi][qj] = true;
            tankRecurse(borderTiles, k + 1);
            knownEmpty[qi][qj] = false;
        }


        protected int[][] tank_board = null;
        protected boolean[][] knownMine = null;
        protected boolean[][] knownEmpty = null;
        protected ArrayList<boolean[]> tank_solutions;

        protected boolean borderOptimization;
        protected int BF_LIMIT = 8;

        public void Main() throws Throwable {
            Thread.sleep(2000);
            onScreen = new int[rows][columns];
            flags = new boolean[rows][columns];
            for (int i = 0; i < rows; i++) for (int j = 0; j < columns; j++) flags[i][j] = false;


            firstSquare();
            for (int c = 0; c < 1000000; c++) {
                int status = updateOnScreen();
                if (!checkConsistency()) {
                    status = updateOnScreen();
                    if (status == -10) exit();
                    continue;
                }
                // Exit on death
                if (status == -10) exit();
                attemptFlagMine();
                attemptMove();
            }
        }

        public void exit() {
            System.exit(0);
        }

        public void dumpPosition() {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < columns; j++) {

                    int d = onScreen(i, j);
                    if (flags[i][j])
                        System.out.print(".");
                    else if (d >= 1)
                        System.out.print(d);
                    else if (d == 0)
                        System.out.print(" ");
                    else System.out.print("#");

                }
                System.out.println();
            }
            System.out.println();
        }

    }

    //endregion

    //region Pair

    class Pair<A, B> {
        private A first;
        private B second;

        public Pair(A first, B second) {
            super();
            this.first = first;
            this.second = second;
        }

        public int hashCode() {
            int hashFirst = first != null ? first.hashCode() : 0;
            int hashSecond = second != null ? second.hashCode() : 0;

            return (hashFirst + hashSecond) * hashSecond + hashFirst;
        }

        public boolean equals(Object other) {
            if (other instanceof Pair) {
                Pair otherPair = (Pair) other;
                return
                        ((this.first == otherPair.first ||
                                (this.first != null && otherPair.first != null &&
                                        this.first.equals(otherPair.first))) &&
                                (this.second == otherPair.second ||
                                        (this.second != null && otherPair.second != null &&
                                                this.second.equals(otherPair.second))));
            }

            return false;
        }

        public String toString() {
            return "(" + first + ", " + second + ")";
        }

        public A getFirst() {
            return first;
        }

        public void setFirst(A first) {
            this.first = first;
        }

        public B getSecond() {
            return second;
        }

        public void setSecond(B second) {
            this.second = second;
        }
    }

    //endregion
}

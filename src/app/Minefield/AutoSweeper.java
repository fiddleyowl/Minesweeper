package app.Minefield;

import app.PublicDefinitions.*;

import java.awt.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;

import static Extensions.Misc.Print.print;

public class AutoSweeper {

    //region Variable Declaration

//    /**
//     * The 3 types of judgement given bu AI.
//     */
//    final int UNKNOWN = 0, MINE = 1, NOT_MINE = -1;
//
//    /**
//     * The marks of graph of connected components
//     */
//    final int CC_VISITED = -233, CC_UNKNOWN =0;
//
//    /**
//     * 下一步可确定的平均格子算法最大支持的计算量 (待测格子数小于等于该数字则投入该算法运行)
//     * 该策略只是一种估算, 估算的格子越多偏差也可能越大. 所以该数字不是越大越好, 12 ~ 18 之间或许比较合理.
//     */
//    final int MAX_NEXT_SITUATION_NUM = 15;
//
//    /**
//     *  时间复杂度爆炸的胜率算法最大支持的计算量 (待测格子数小于等于该数字则投入该算法运行)
//     *  该策略精确计算点击每个格子的胜率, 所以该数字越大胜率越高. 但该数字指数级影响 AI 的总耗时.
//     */
//    final int MAX_WIN_RATE_NUM = 12;
//
//    /**
//     *  二维数组的 [n][m] 表示当 n 个未知格子有 m 个雷时, 有多少种可能的情况
//     *  用 long 可能会溢出, 但同时后面会用到浮点除法, 所以又不能用 BigInteger, 于是选用了 BigDecimal
//     */
    final ArrayList<ArrayList<BigDecimal>> numOfCasesForGivenCellsAndMines;

    //endregion

    AgainstAIController mode;

    public AutoSweeper(AgainstAIController mode) {
        this.mode = mode;
        numOfCasesForGivenCellsAndMines = new ArrayList<>(18 * 32);
        ArrayList<BigDecimal> zero = new ArrayList<>(1);
        zero.add(new BigDecimal(1));
        numOfCasesForGivenCellsAndMines.add(zero);
    }

    //region Inner Class

    /**
     * Since the there are so many return values needed when using Probability Solver, design a inner class to wrap all these return values.
     */
    public static class ProbResult {
        public List<List<Point>> ccList;
        public int[][] ccGraph;
        public List<Map<Integer, int[]>> ccPermList;
        public double[][] probGraph;

        public ProbResult() {}
        public ProbResult(List<List<Point>> ccList, int[][] ccGraph, List<Map<Integer, int[]>> ccPermList, double[][] probGraph) {
            this.ccList = ccList;
            this.ccGraph = ccGraph;
            this.ccPermList = ccPermList;
            this.probGraph = probGraph;
        }
    }

    class Pair<K, V> {

        private final K key;
        private final V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }
        public V getValue() {
            return value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
        }

        @Override
        public int hashCode() {
            return key.hashCode() * 13 + (value == null ? 0 : value.hashCode());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o instanceof Pair) {
                Pair<?, ?> pair = (Pair<?, ?>) o;
                if (!Objects.equals(key, pair.key)) return false;
                return Objects.equals(value, pair.value);
            }
            return false;
        }

    }

    //endregion

    /**
     * Use Subtraction Formula to check whether the unknown cells around the 2 known
     * cells can be determined
     * @param x1 the x coordinate of first target cell
     * @param y1 the y coordinate of first target cell
     * @param x2 the x coordinate of second target cell
     * @param y2 the y coordinate of second target cell
     * @return return Pair that stores 2 value: key is [the list of cells that can be clicked], value is [the list of cells that can be flagged]
     */
    public Pair<List<Point>, List<Point>> checkTwoUncoveredCell(int x1, int y1, int x2, int y2) {
        if (mode.manipulatedMinefield[x1][y1] != LabelType.CLICKED || mode.manipulatedMinefield[x2][y2] != LabelType.CLICKED) { return null; }
        int num1 = mode.minefield[x1][y1].getCode(), num2 = mode.minefield[x2][y2].getCode(); //number of unopened mines around (x1,y1) and (x2,y2)
        int diffX = x2 - x1, diffY = y2 - y1;
        if (Math.abs(diffX) + Math.abs(diffY) != 1) { return null; }
        List<Point> around1 = new ArrayList<>(3);
        List<Point> around2 = new ArrayList<>(3);
        for (int i = -1; i < 2; i++) {

            int xx1 = x1 - diffX + diffY * i, yy1 = y1 - diffY + diffX * i;
            if (mode.isPointInRange(xx1, yy1)) {
                if (mode.manipulatedMinefield[xx1][yy1] == LabelType.CORRECT || mode.manipulatedMinefield[xx1][yy1] == LabelType.BOMBED) { num1--; }
                else if (mode.manipulatedMinefield[xx1][yy1] == LabelType.NOT_CLICKED) { around1.add(new Point(xx1, yy1)); }
            }

            int xx2 = x2 + diffX + diffY * i, yy2 = y2 + diffY + diffX *i ;
            if (mode.isPointInRange(xx2, yy2)) {
                if (mode.manipulatedMinefield[xx2][yy2] == LabelType.CORRECT || mode.manipulatedMinefield[xx2][yy2] == LabelType.BOMBED) { num2--; }
                else if (mode.manipulatedMinefield[xx2][yy2] == LabelType.NOT_CLICKED) { around2.add(new Point(xx2, yy2)); }
            }

        }
        Pair<List<Point>, List<Point>> res = null;
        if (num2 - num1 == around2.size()) { res = new Pair<>(around1, around2); }
        else if (num1 - num2 == around1.size()) { res = new Pair<>(around2, around1); }
        return res;
    }

    /**
     * The basic algorithm that is used during mine sweeping.
     * One Cell Check and Subtraction Formula.
     * @return true if click or flag once, and false for no actions being done.
     */
    public boolean sweepAllBasedOnDefinition() {

        for (int x = 0; x < mode.rows; x++) {
            for ( int y = 0; y < mode.columns; y++) {

                //Check based on only one cell and its around.
                if (mode.manipulatedMinefield[x][y] == LabelType.CLICKED && mode.minefield[x][y] != MinefieldType.EMPTY) {
                    if (mode.countUnopenedMinesAround(x, y) + mode.countFlagsAround(x, y) == mode.minefield[x][y].getCode() &&
                        mode.countUnopenedMinesAround(x, y) != 0 && mode.countFlagsAround(x, y) != mode.minefield[x][y].getCode())
                    {
                        mode.flagACertainSquareAround(x, y);
                        print("Robot flag certain around");
                        return true;
                    }
                } else if (mode.manipulatedMinefield[x][y] == LabelType.CLICKED &&
                           mode.countUnopenedMinesAround(x, y) != 0 && mode.countFlagsAround(x, y) == mode.minefield[x][y].getCode() &&
                           mode.countFlagsAround(x, y) != mode.countUnopenedMinesAround(x, y))
                {
                    mode.clickACertainSquareAround(x, y);
                    print("Robot click certain around.");
                    return true;
                }

            }
        }

        for (int x = 0; x < mode.rows; x++) {
            for (int y = 0; y < mode.columns; y++) {
                //Check based on two adjacent cells by using Subtraction Formula.
                for (int i = 0; i < 2; i++) {
                    int x2 = x + i, y2 = y + 1 - i;
                    if (!mode.isPointInRange(x2, y2)) { continue; }
                    Pair<List<Point>, List<Point>> _pair = checkTwoUncoveredCell(x, y, x2, y2);
                    if (_pair != null) {
                        if (_pair.value.size() > 0) {
                            print("Use Subtraction Formula to flag.");
                            mode.clickedOnLabel_Robot(MouseClickType.SECONDARY, _pair.value.get(0).x, _pair.value.get(0).y);
                            return true;
                        }
                        if (_pair.key.size() > 0) {
                            print("Use Subtraction Formula to click.");
                            mode.clickedOnLabel_Robot(MouseClickType.PRIMARY, _pair.key.get(0).x, _pair.key.get(0).y);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
}

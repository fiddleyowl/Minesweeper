package app.Minefield;

import app.PublicDefinitions.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static Extensions.Misc.Print.print;

public class AutoSweeper {

    AgainstAIController mode;

    public AutoSweeper(AgainstAIController mode) {
        this.mode = mode;
    }

    //region class Pair

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
        if (mode.manipulatedMinefield[x1][y1] != LabelType.NOT_CLICKED || mode.manipulatedMinefield[x2][y2] != LabelType.NOT_CLICKED) { return null; }
        int num1 = mode.minefield[x1][y1].getCode(), num2 = mode.minefield[x2][y2].getCode();
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
        if (num2 - num1 - around2.size() == 0) { res = new Pair<>(around1, around2); }
        else if (num1 - num2 - around1.size() == 0) { res = new Pair<>(around2, around1); }
        return res;
    }

    public boolean sweepAllBasedOnDefinition() {
        for (int x = 0; x < mode.rows; x++) {
            for ( int y = 0; y < mode.columns; y++) {

                //Check based on only one cell and its around.
                if (mode.manipulatedMinefield[x][y] == LabelType.CLICKED && mode.minefield[x][y] != MinefieldType.EMPTY) {
                    if (mode.countUnopenedMinesAround(x, y) + mode.countFlagsAround(x, y) == mode.minefield[x][y].getCode() &&
                        mode.countUnopenedMinesAround(x, y) != 0 && mode.countFlagsAround(x, y) != mode.minefield[x][y].getCode())
                    {
                        mode.flagACertainSquareAround(x, y);
                        return true;
                    }
                } else if (mode.manipulatedMinefield[x][y] == LabelType.CLICKED &&
                           mode.countUnopenedMinesAround(x, y) != 0 && mode.countFlagsAround(x, y) == mode.minefield[x][y].getCode() &&
                           mode.countFlagsAround(x, y) != mode.countUnopenedMinesAround(x, y))
                {
                    mode.clickACertainSquareAround(x, y);
                    return true;
                }

                //Check based on two adjacent cells by using Subtraction Formula.
                for (int i = 0; i < 2; i++) {
                    int x2 = x + i, y2 = y + 1 - i;
                    if (!mode.isPointInRange(x2, y2)) { continue; }
                    Pair<List<Point>, List<Point>> _pair = checkTwoUncoveredCell(x, y, x2, y2);
                    if (_pair != null) {
                        if (_pair.value.size() > 0) {
                            mode.clickedOnLabel_Robot(MouseClickType.SECONDARY, _pair.value.get(0).x, _pair.value.get(0).y);
                            print("Use Subtraction Formula to flag successfully.");
                            return true;
                        }
                        if (_pair.key.size() > 0) {
                            mode.clickedOnLabel_Robot(MouseClickType.PRIMARY, _pair.key.get(0).x, _pair.key.get(0).y);
                            print("Use Subtraction Formula to click successfully.");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }


}

package app.Minefield;

public class Archiver {

    static int[][] byteMinefieldArray;
    static int[][] byteLabelFieldArray;

    public static void save(MultiplayerMinefieldController mode) {
        int rows = mode.rows;
        int columns = mode.columns;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                byteMinefieldArray[i][j] = mode.minefield[i][j].getCode();
            }
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                byteLabelFieldArray[i][j] = mode.manipulatedMinefield[i][j].getCode();
            }
        }
    }
}

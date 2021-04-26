package app;

import SupportingFiles.Sound;
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

public class MinefieldController {

    //region Variables Declaration

    private final Stage mainStage;

    int rows;
    int columns;
    int mines;

    int size = 48;
    /**
     * <p>A two-dimensional array that stores the position of mines and the number shown on labels.</p>
     * <p>-1 for mine, 0 to 8 for number 0 to 8 respectively.</p>
     */
    MinefieldType[][] minefield;

    /**
     * <p>A two-dimensional array that stores the current status of minefield.</p>
     * <p>0 for not clicked, -1 for clicked, -2 for bombed, 1 for flagged, 2 is questioned.</p>
     */
    LabelType[][] manipulatedMinefield;

    boolean isFirstClick = true;
    double mouseFirstX = 0.0;
    double mouseSecondX = 0.0;
    double mouseFirstY = 0.0;
    double mouseSecondY = 0.0;
    int discoveredMines = 0;
    boolean shouldStop = false;
    private boolean isWin = false;

    boolean isSaved = false;

    /**
     * Marks the time the game started.
     */
    long startTime;
    /**
     * Marks the time the game stopped. If the game is not stopped, marks the current time (every 200ms).
     */
    long stopTime;

    String[] labelText = {"\uDBC0\uDC92", "\uDBC0\uDCCA", "\uDBC0\uDCCC", "\uDBC0\uDCCE", "\uDBC0\uDCD0", "\uDBC0\uDCD2", "\uDBC0\uDCD4", "\uDBC0\uDCD6", "\uDBC0\uDCD8"};
    // SF Symbols text.

    /**
     * <p>Updates <i>stopTime</i> and informative labels every 200ms.</p>
     * <p>If <i>shouldStop</i> becomes true, the loop returns.</p>
     */
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            while (true) {
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
                    } catch (Exception ignored) {}
                    return;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
            }
        }
    });

    @FXML
    private AnchorPane leftAnchorPane;

    private GridPane minefieldGridPane;

    @FXML
    private GridPane playerInformationGridPane;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label timerLabel;

    @FXML
    private Label mineLabel;


    //endregion

    //region Initializer & Data Generation

    public MinefieldController(int rows, int columns, int mines,String fxmlName) throws IOException {
        this.rows = rows;
        this.columns = columns;
        this.mines = mines;

        manipulatedMinefield = new LabelType[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                manipulatedMinefield[i][j] = LabelType.NOT_CLICKED;
            }
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlName));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setTitle("Minesweeper");
        mainStage.setOnCloseRequest(event -> closeStage());

        setupInterfaceStyle(root);

        menuBar.useSystemMenuBarProperty().set(true);

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        music.play();

        showStage();

    }

    @FXML
    public void showStage() {
        initializeMinefield(rows, columns, mines);
        mainStage.show();
    }

    /**
     * Generates minefield data.
     *
     * @param rows    Rows of the minefield.
     * @param columns Columns of the minefield.
     * @param mines   Mines on the minefield.
     */
    public void generateMinefieldData(int rows, int columns, int mines) {
        // Call only when needed.

        minefield = new MinefieldType[rows][columns];
        Random random = new Random();
        for (int i = 1; i <= mines; i++) {
            int ranRow = random.nextInt(rows);
            int ranColumn = random.nextInt(columns);
            while (minefield[ranRow][ranColumn] == MinefieldType.MINE) {
                ranRow = random.nextInt(rows);
                ranColumn = random.nextInt(columns);
            }
            minefield[ranRow][ranColumn] = MinefieldType.MINE;
        }

        //Compute the elements in mineField(The number of mines in surrounding location).
        for (int currentRow = 0; currentRow < rows; currentRow++) {
            for (int currentColumn = 0; currentColumn < columns; currentColumn++) {
                if (minefield[currentRow][currentColumn] != MinefieldType.MINE) {
                    int count = 0;
                    try { if (minefield[currentRow - 1][currentColumn - 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow - 1][currentColumn] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow - 1][currentColumn + 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow][currentColumn - 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow][currentColumn + 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow + 1][currentColumn - 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow + 1][currentColumn] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow + 1][currentColumn + 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    minefield[currentRow][currentColumn] = MinefieldType(count);
                }
            }
        }
        // Generate minefield. Uncomment the next line to print the whole minefield.
        print(Arrays.deepToString(minefield));

        //Check if there exists a 9x9 region that is filled with mines.
        outerFor:
        for (int i = 1; i < rows - 1; i++) {
            for (int j = 1; j < columns - 1; j++) {
                if (minefield[i][j] == MinefieldType.MINE) {
                    int surroundingMinesNum = 0; //The number of the surrounding mines.
                    try { if (minefield[i - 1][j - 1] == MinefieldType.MINE) { surroundingMinesNum++; } } catch (Exception ignore) { }
                    try { if (minefield[i - 1][j] == MinefieldType.MINE) { surroundingMinesNum++; } } catch (Exception ignore) { }
                    try { if (minefield[i - 1][j + 1] == MinefieldType.MINE) { surroundingMinesNum++; } } catch (Exception ignore) { }
                    try { if (minefield[i][j + 1] == MinefieldType.MINE) { surroundingMinesNum++; } } catch (Exception ignore) { }
                    try { if (minefield[i + 1][j + 1] == MinefieldType.MINE) { surroundingMinesNum++; } } catch (Exception ignore) { }
                    try { if (minefield[i + 1][j] == MinefieldType.MINE) { surroundingMinesNum++; } } catch (Exception ignore) { }
                    try { if (minefield[i + 1][j - 1] == MinefieldType.MINE) { surroundingMinesNum++; } } catch (Exception ignore) { }
                    try { if (minefield[i][j - 1] == MinefieldType.MINE) { surroundingMinesNum++; } } catch (Exception ignore) { }
                    if (surroundingMinesNum == 8) {
                        generateMinefieldData(rows, columns, mines);
                        break outerFor;
                    } //Regenerate minefield.
                }
            }
        }
    }


    /**
     * A UI method that creates and initializes the minefield pane.
     */
    @FXML
    public void initializeMinefield(int rows, int columns, int mines) {
        minefieldGridPane = new GridPane();
        minefieldGridPane.getStyleClass().add("minefieldGridPane");
        minefieldGridPane.setGridLinesVisible(false);

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        // Place the gridpane in center.
        vBox.getChildren().add(minefieldGridPane);
        hBox.getChildren().add(vBox);
        GesturePane gesturePane = new GesturePane(hBox);
        leftAnchorPane.getChildren().add(gesturePane);
        AnchorPane.setLeftAnchor(gesturePane, 0.0);
        AnchorPane.setRightAnchor(gesturePane, 0.0);
        AnchorPane.setTopAnchor(gesturePane, 0.0);
        AnchorPane.setBottomAnchor(gesturePane, 0.0);
        gesturePane.reset();

        generateMinefieldData(rows, columns, mines);
        initializeGridPaneLabels(rows, columns);
        // Add initial labels to gridpane.
        updateInformativeLabels();

        minefieldGridPane.setPrefSize(size * 1.2 * columns, size * 1.2 * rows);
        minefieldGridPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        minefieldGridPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        minefieldGridPane.setAlignment(Pos.CENTER);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(100.0 / rows);
        minefieldGridPane.getRowConstraints().add(rowConstraints);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100.0 / columns);
        minefieldGridPane.getColumnConstraints().add(columnConstraints);


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
     * A UI method that adds grid labels with NOT_CLICKED type to gridpane.
     *
     * @param rows    Number of rows in the pane.
     * @param columns Number of columns in the pane.
     */
    public void initializeGridPaneLabels(int rows, int columns) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Label emptyLabel = new Label();
                minefieldGridPane.add(emptyLabel, j, i);
                markGridLabel(i, j, LabelType.NOT_CLICKED);
            }
        }
    }

    /**
     * A UI method that marks the specified grid label as the given type.
     *
     * @param row    Row of the label.
     * @param column Column of the label.
     * @param type   Type to be marked as.
     */
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
        MinefieldController minefieldController = new MinefieldController(rows, columns, mines,"MinefieldController.fxml");
    }

    @FXML
    public void openGame() {

    }

    @FXML
    private void closeStage() {
        print("closeStage");
        music.stop();
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

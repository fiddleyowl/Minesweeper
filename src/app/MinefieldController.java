package app;

import javafx.application.Platform;
import javafx.collections.ObservableList;
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
    MinefieldType[][] minefield;
    LabelType[][] manipulatedMinefield;
    // 0 for not clicked, -1 for clicked, 1 for flagged, 2 is questioned.

    boolean isFirstClick = true;
    double mouseFirstX = 0.0;
    double mouseSecondX = 0.0;
    double mouseFirstY = 0.0;
    double mouseSecondY = 0.0;
    int discoveredMines = 0;
    boolean shouldStop = false;

    boolean isSaved = false;

    long startTime;
    long stopTime;

    String[] labelText = {"\uDBC0\uDC92","\uDBC0\uDCCA","\uDBC0\uDCCC","\uDBC0\uDCCE","\uDBC0\uDCD0","\uDBC0\uDCD2","\uDBC0\uDCD4","\uDBC0\uDCD6","\uDBC0\uDCD8"};
    // SF Symbols text.

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            while (true) {
                stopTime = System.currentTimeMillis();
                Platform.runLater(() -> updateInformationLabels());
                if (shouldStop) { return; }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) { }
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

    public MinefieldController(int rows, int columns, int mines) throws IOException {
        this.rows = rows;
        this.columns = columns;
        this.mines = mines;

        manipulatedMinefield = new LabelType[rows][columns];
        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                manipulatedMinefield[i][j] = LabelType.NOT_CLICKED;
            }
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MinefieldController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setTitle("Minesweeper");

        setupDarkMode(root);

        menuBar.useSystemMenuBarProperty().set(true);

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        showStage();
    }

    @FXML
    public void showStage() {
        initializeMinefield(rows,columns,mines);
        mainStage.show();
    }

    public void generateMinefieldData(int rows, int columns, int mines) {
        // Call only when needed.
        minefield = new MinefieldType[rows][columns];
        Random random = new Random();
        for (int i = 1; i <= mines; i++) {
            int ranRow = random.nextInt(rows);
            int ranColumn = random.nextInt(columns);
            while (minefield[ranRow][ranColumn] == MinefieldType.MINE) {
                ranRow = random.nextInt(rows);
                random.nextInt(columns);
            }
            minefield[ranRow][ranColumn] = MinefieldType.MINE;
        }

        for (int currentRow = 0; currentRow < rows; currentRow++) {
            for (int currentColumn = 0; currentColumn < columns; currentColumn++) {
                if (minefield[currentRow][currentColumn] != MinefieldType.MINE) {
                    int count = 0;
                    try { if (minefield[currentRow-1][currentColumn-1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow-1][currentColumn] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow-1][currentColumn+1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow][currentColumn-1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow][currentColumn+1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow+1][currentColumn-1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow+1][currentColumn] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (minefield[currentRow+1][currentColumn+1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    minefield[currentRow][currentColumn] = MinefieldType(count);
                }
            }
        }

        // Generate minefield. Uncomment the next line to output the whole minefield.
        print(Arrays.deepToString(minefield));
    }

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
        AnchorPane.setLeftAnchor(gesturePane,0.0);
        AnchorPane.setRightAnchor(gesturePane,0.0);
        AnchorPane.setTopAnchor(gesturePane,0.0);
        AnchorPane.setBottomAnchor(gesturePane,0.0);
        gesturePane.reset();

        generateMinefieldData(rows,columns,mines);
        initializeGridPaneLabels(rows,columns);
        // Add initial labels to gridpane.
        updateInformationLabels();

        minefieldGridPane.setPrefSize(size*1.2*columns,size*1.2*rows);
        minefieldGridPane.setMinSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
        minefieldGridPane.setMaxSize(Region.USE_PREF_SIZE,Region.USE_PREF_SIZE);
        minefieldGridPane.setAlignment(Pos.CENTER);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(100.0/rows);
        minefieldGridPane.getRowConstraints().add(rowConstraints);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100.0/columns);
        minefieldGridPane.getColumnConstraints().add(columnConstraints);
    }

    //endregion

    //region Clicking Handling

    public void clickedOnLabel(MouseClickType type, int row, int column) {
        if (shouldStop) {
            return;
        }

        switch (manipulatedMinefield[row][column]) {
            case CLICKED:
                // -1 for clicked, check for quick click.
                if (type == MouseClickType.TERTIARY) {
                    quickClick(row,column);
                }
                break;
            case NOT_CLICKED:
                // 0 for not clicked.
                switch (type) {
                    case PRIMARY:
                        // 0 for primary button.
                        if (minefield[row][column] == MinefieldType.MINE) {
                            // Is a mine!
                            if (isFirstClick) {
                                // First move is mine, regenerate minefield without any prompt, pretend the user did not click successfully.
                                print("First click is a mine! Regenerate minefield.");
                                generateMinefieldData(rows,columns,mines);
                                clickedOnLabel(MouseClickType.PRIMARY,row,column);
                                return;
                            } else {
                                discoveredMines += 1;
                                markGridLabel(row,column,LabelType.BOMBED);
                            }
                        } else {
                            clickRecursively(row,column,row,column);
                            markGridLabel(row,column,LabelType.CLICKED);
                        }
                        break;
                    case SECONDARY:
                        // 1 for secondary button.
                        discoveredMines += 1;
                        markGridLabel(row,column,LabelType.FLAGGED);
                        break;
                    case TERTIARY:
                        // 2 for middle button or double click.
                        quickClick(row,column);
                        break;
                    default: break;
                }
                break;
            case FLAGGED:
                // 1 for flagged.
                if (type == MouseClickType.SECONDARY) {
                    markGridLabel(row,column,LabelType.QUESTIONED);
                    discoveredMines -= 1;
                }
                break;
            case QUESTIONED:
                // 2 for questioned.
                if (type == MouseClickType.SECONDARY) {
                    markGridLabel(row,column,LabelType.NOT_CLICKED);
                }
                break;
            default: break;
        }

        updateInformationLabels();
        if (isFirstClick) {
            thread.start();
        }
        isFirstClick = false;
        checkIfShouldStop(row,column);
        System.out.printf("Clicked Type: %s, Row: %d, Column: %d\n",type,row+1,column+1);
    }

    public void clickRecursively(int previousRow, int previousColumn, int currentRow, int currentColumn) {
        if (currentRow >= 0 && currentRow < rows && currentColumn >= 0 && currentColumn < columns) {
            if (minefield[previousRow][previousColumn] == MinefieldType.EMPTY && manipulatedMinefield[currentRow][currentColumn] == LabelType.NOT_CLICKED) {
                // Previous label is empty -> safe to click this label.
                markGridLabel(currentRow,currentColumn,LabelType.CLICKED);
                clickRecursively(currentRow,currentColumn,currentRow - 1, currentColumn - 1);
                clickRecursively(currentRow,currentColumn,currentRow - 1, currentColumn);
                clickRecursively(currentRow,currentColumn,currentRow - 1, currentColumn + 1);
                clickRecursively(currentRow,currentColumn,currentRow, currentColumn - 1);
                clickRecursively(currentRow,currentColumn,currentRow, currentColumn + 1);
                clickRecursively(currentRow,currentColumn,currentRow + 1, currentColumn - 1);
                clickRecursively(currentRow,currentColumn,currentRow + 1, currentColumn);
                clickRecursively(currentRow,currentColumn,currentRow + 1, currentColumn + 1);

            }

        }

    }

    public void quickClick(int row, int column) {
        if (manipulatedMinefield[row][column] != LabelType.CLICKED) {
            // If quickClick is not applied on clicked label, return without clicking.
            return;
        }

        int flaggedAround = 0;
        try { if (manipulatedMinefield[row-1][column-1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row-1][column] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row-1][column+1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row][column-1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row][column+1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row+1][column-1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row+1][column] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        try { if (manipulatedMinefield[row+1][column+1] == LabelType.FLAGGED) { flaggedAround += 1; } } catch (Exception ignored) { }
        if (flaggedAround != minefield[row][column].getCode()) {
            // If the number of flags around is not equal to the number shown on the label, return without clicking.
            return;
        }

        // Proceed with clicking.
        int[] rowData = {row-1,row-1,row-1,row,row,row+1,row+1,row+1};
        int[] columnData = {column-1,column,column+1,column-1,column+1,column-1,column,column+1};
        for (int i = 0; i < 8; i++) {
            if (rowData[i] >= 0 && rowData[i] < rows && columnData[i] >= 0 && columnData[i] < columns) {
                if (manipulatedMinefield[rowData[i]][columnData[i]] == LabelType.NOT_CLICKED) {
                    // Only if it's not clicked
                    if (minefield[rowData[i]][columnData[i]] == MinefieldType.MINE) {
                        // Oops, it's a mine.
                        discoveredMines += 1;
                        markGridLabel(rowData[i],columnData[i],LabelType.BOMBED);
                    } else {
                        // Not a mine,
                        clickRecursively(rowData[i],columnData[i],rowData[i],columnData[i]);
                        markGridLabel(rowData[i],columnData[i],LabelType.CLICKED);
                    }
                    checkIfShouldStop(rowData[i],columnData[i]);
                }
            }
        }
    }

    //endregion

    //region UI Updates

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
            shouldStop = true;
        }

    }

    public void updateInformationLabels() {
        long duration = stopTime - startTime;
        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;
        long hour = (duration / (1000 * 60 * 60)) % 24;
        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        timerLabel.setText(time);
        mineLabel.setText(String(mines - discoveredMines));
    }

    public void initializeGridPaneLabels(int rows, int columns) {
        for(int i = 0; i < rows; i++) {
            for(int j = 0; j < columns; j++) {
                Label emptyLabel = new Label();
                minefieldGridPane.add(emptyLabel,j,i);
                markGridLabel(i,j,LabelType.NOT_CLICKED);
            }
        }
    }

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
                    double dragDistance = Math.sqrt(Math.pow(mouseFirstX - mouseSecondX,2) + Math.pow(mouseFirstY - mouseSecondY,2));
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
        double x = mainStage.getX() + (mainStage.getWidth() - CHOOSE_MODE_CONTROLLER_WIDTH)/2;
        double y = mainStage.getY() + (mainStage.getHeight() - CHOOSE_MODE_CONTROLLER_HEIGHT)/2;
        ChooseModeController chooseModeController = new ChooseModeController(x,y);
        chooseModeController.showStage();
    }

    @FXML
    public void restartNewGame() throws IOException {
        mainStage.setFullScreen(false);
        MinefieldController minefieldController = new MinefieldController(rows,columns,mines);
        mainStage.close();
    }

    @FXML
    public void openGame() {

    }

    @FXML
    private void closeStage() { mainStage.close(); }

    public void saveGame() {

    }

    public void duplicateGame() {

    }
    //endregion

}

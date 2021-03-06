package app.Minefield;

import SupportingFiles.DataModels.GameModel;
import SupportingFiles.GameDecoder;
import app.ChooseModeController;
import app.PreferencesController;
import app.WelcomeController;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

import java.io.*;
import java.util.Random;

import net.kurobako.gesturefx.GesturePane;

import static SupportingFiles.ConfigHelper.*;
import static app.PublicDefinitions.*;
import static Extensions.Misc.Print.*;

abstract class MinefieldController {

    //region Variables Declaration
    Stage mainStage;

    public int rows;
    public int columns;
    public int mines;

    public int size = 48;

    public long startTime;
    public long stopTime;

    public double mouseFirstX = 0.0;
    public double mouseSecondX = 0.0;
    public double mouseFirstY = 0.0;
    public double mouseSecondY = 0.0;

    public boolean isFirstClick = true;
    public int discoveredMines = 0;
    public boolean shouldStop = false;

    public boolean isSaved = false;
    public String savePath = "";

    public int rounds = 1;

    /**
     * <p>A two-dimensional array that stores the position of mines and the number shown on labels.</p>
     * <p>-1 for mine, 0 to 8 for number 0 to 8 respectively.</p>
     */
    public MinefieldType[][] minefield;

    /**
     * <p>A two-dimensional array that stores the current status of minefield.</p>
     * <p>0 for not clicked, -1 for clicked, -2 for bombed, 1 for flagged, 2 is questioned.</p>
     */
    public LabelType[][] manipulatedMinefield;

    public String[] labelText = {"\uDBC0\uDC92", "\uDBC0\uDCCA", "\uDBC0\uDCCC", "\uDBC0\uDCCE", "\uDBC0\uDCD0", "\uDBC0\uDCD2", "\uDBC0\uDCD4", "\uDBC0\uDCD6", "\uDBC0\uDCD8"};
    // SF Symbols text.

    @FXML
    AnchorPane leftAnchorPane;

    @FXML
    BorderPane rightBorderPane;

    @FXML
    VBox minefieldTopVBox;

    @FXML
    GridPane playerInformationGridPane;

    GridPane minefieldGridPane;

    GesturePane gesturePane;

    @FXML
    MenuBar menuBar;

    @FXML
    Label timerLabel;

    @FXML
    Label mineLabel;

    @FXML
    Label roundLabel;

    @FXML
    CheckBox musicCheckBox;

    @FXML
    CheckBox soundEffectsCheckBox;

    //endregion

    //region Initializer & Data Generation

    public MinefieldController(int rows, int columns, int mines) throws IOException {
        this.rows = rows;
        this.columns = columns;
        this.mines = mines;

        manipulatedMinefield = new LabelType[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                manipulatedMinefield[i][j] = LabelType.NOT_CLICKED;
            }
        }

        loadStage();
    }

    public MinefieldController(GameModel gameModel, String savePath) throws IOException {
        this.rows = gameModel.minefield.length;
        this.columns = gameModel.minefield[0].length;
        for (MinefieldType[] i: gameModel.minefield) {
            for (MinefieldType j: i) {
                if (j == MinefieldType.MINE) {
                    this.mines += 1;
                }
            }
        }

        manipulatedMinefield = new LabelType[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                manipulatedMinefield[i][j] = LabelType.NOT_CLICKED;
            }
        }

        isSaved = true;
        this.savePath = savePath;

        loadStage();
    }

    public MinefieldController(int rows, int columns, int mines, MinefieldType[][] minefield) throws IOException {
        this.rows = rows;
        this.columns = columns;
        this.mines = mines;

        manipulatedMinefield = new LabelType[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                manipulatedMinefield[i][j] = LabelType.NOT_CLICKED;
            }
        }

        loadStage();
    }

    abstract void playSameBoard() throws IOException;

    public void loadStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MinefieldController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setTitle("Minesweeper");
        mainStage.setOnCloseRequest(event -> {
            closeStage();
        });

        mainStage.setMinWidth(600.0);
        mainStage.setMinHeight(800.0);

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
        menuBar.toFront();
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
        try {
            for (int i = 0; i < rows; i++) {
                System.out.print("[ ");
                for (int j = 0; j < columns; j++) {
                    System.out.print(minefield[i][j].getCode()+", ");
                }
                System.out.print("]\n");
            }
        }catch (Exception ignored){}

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
        print("generateMinefieldData");
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
        gesturePane = new GesturePane(hBox);
        leftAnchorPane.getChildren().add(gesturePane);
        AnchorPane.setLeftAnchor(gesturePane, 0.0);
        AnchorPane.setRightAnchor(gesturePane, 0.0);
        if (isMacOS()) {
            AnchorPane.setTopAnchor(gesturePane, 0.0);
        } else {
            AnchorPane.setTopAnchor(gesturePane, 30.0);
        }
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

        musicCheckBox.setSelected(isMusicEnabled());
        soundEffectsCheckBox.setSelected(isSoundEffectsEnabled());
        print("initializeMinefield");
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

    public abstract void initializeRightBorderPane();

    //endregion

    //region Clicking Handling
    abstract void clickedOnLabel(MouseClickType type, int row, int column);

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

    //endregion

    //region UI Updates

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
                    case WRONG -> {
                        label.getStyleClass().add("minefieldLabelWrong");
//                        label.setText("\uDBC0\uDCE1");
                        label.setText("\uDBC0\uDCF0");
                    }
                    case CORRECT -> {
                        label.getStyleClass().add("minefieldLabelCorrect");
//                        label.setText("\uDBC0\uDCF3");
                        label.setText("\uDBC0\uDCF2");
                    }
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

    @FXML
    abstract void updateInformativeLabels();

    //endregion

    @FXML
    public void toggleMusic() {
        if (musicCheckBox.isSelected()) {
            setMusicEnabled(true);
            music.play();
        } else {
            setMusicEnabled(false);
            music.stop();
        }
    }

    @FXML
    public void toggleSoundEffects() {
        if (soundEffectsCheckBox.isSelected()) {
            setSoundEffectsEnabled(true);
        } else {
            setSoundEffectsEnabled(false);
        }
    }

    @FXML
    public void newGame() throws IOException {
        double x = mainStage.getX() + (mainStage.getWidth() - CHOOSE_MODE_CONTROLLER_WIDTH) / 2;
        double y = mainStage.getY() + (mainStage.getHeight() - CHOOSE_MODE_CONTROLLER_HEIGHT - 30.0) / 2;
        ChooseModeController chooseModeController = new ChooseModeController(x, y);
        chooseModeController.showStage();
    }

    @FXML
    public abstract void restartNewGame() throws IOException;

    @FXML
    abstract void closeStage();

    @FXML
    public void quitToHome() throws IOException {
        double x = (screenWidth - CHOOSE_MODE_CONTROLLER_WIDTH) / 2;
        double y = (screenHeight - CHOOSE_MODE_CONTROLLER_HEIGHT) / 2;
        ChooseModeController chooseModeController = new ChooseModeController(x, y);
        closeStage();
        chooseModeController.showStage();
    }

    @FXML
    public void openGame() {
        GameDecoder.openGame(mainStage);
    }

    @FXML
    public abstract boolean saveGame();

    @FXML
    public abstract boolean duplicateGame();

    @FXML
    public void showPreferences() throws IOException {
        PreferencesController preferencesController = new PreferencesController();
        preferencesController.showStage();
    }

    @FXML
    public void showMinefield() throws IOException {
        ShowMinefieldController showMinefieldController = new ShowMinefieldController(minefield);
        showMinefieldController.showStage();
    }

    @FXML
    public void showWelcomeScreen() throws IOException {
        WelcomeController welcomeController = new WelcomeController();
        welcomeController.showStage();
    }

    @FXML
    public void showAbout() throws IOException {

    }

}

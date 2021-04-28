package app.Minefield;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import net.kurobako.gesturefx.GesturePane;

import static app.PublicDefinitions.*;
import static Extensions.Misc.Print.*;

abstract class MinefieldController {

    //region Variables Declaration
    Stage mainStage;

    int rows;
    int columns;
    int mines;

    int size = 48;

    double mouseFirstX = 0.0;
    double mouseSecondX = 0.0;
    double mouseFirstY = 0.0;
    double mouseSecondY = 0.0;

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

    String[] labelText = {"\uDBC0\uDC92", "\uDBC0\uDCCA", "\uDBC0\uDCCC", "\uDBC0\uDCCE", "\uDBC0\uDCD0", "\uDBC0\uDCD2", "\uDBC0\uDCD4", "\uDBC0\uDCD6", "\uDBC0\uDCD8"};
    // SF Symbols text.

    @FXML
    AnchorPane leftAnchorPane;

    GridPane minefieldGridPane;

    @FXML
    GridPane playerInformationGridPane;

    @FXML
    MenuBar menuBar;

    @FXML
    Label timerLabel;

    @FXML
    Label mineLabel;

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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MinefieldController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setTitle("Minesweeper");
        mainStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                closeStage();
//                System.exit(0);
            }
        });

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

    //endregion

    //region Clicking Handling
    abstract void clickedOnLabel(MouseClickType type, int row, int column);

    //endregion

    abstract void markGridLabel(int row, int column, LabelType type);

    @FXML
    abstract void updateInformativeLabels();


    @FXML
    abstract void closeStage();

}

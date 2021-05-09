package app.Minefield;

import javafx.collections.ObservableList;
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
import javafx.stage.Stage;
import net.kurobako.gesturefx.GesturePane;

import java.io.IOException;

import static Extensions.Misc.Print.print;
import static SupportingFiles.ConfigHelper.isMusicEnabled;
import static SupportingFiles.ConfigHelper.isSoundEffectsEnabled;
import static app.PublicDefinitions.*;

public class ShowMinefieldController {

    private final Stage mainStage;

    MinefieldType[][] minefield;

    GridPane minefieldGridPane;

    @FXML
    AnchorPane leftAnchorPane;

    @FXML
    MenuBar menuBar;

    int size = 48;

    public double mouseFirstX = 0.0;
    public double mouseSecondX = 0.0;
    public double mouseFirstY = 0.0;
    public double mouseSecondY = 0.0;

    public String[] labelText = {"\uDBC0\uDC92", "\uDBC0\uDCCA", "\uDBC0\uDCCC", "\uDBC0\uDCCE", "\uDBC0\uDCD0", "\uDBC0\uDCD2", "\uDBC0\uDCD4", "\uDBC0\uDCD6", "\uDBC0\uDCD8"};
    // SF Symbols text.

    public ShowMinefieldController(MinefieldType[][] minefield) throws IOException {
        this.minefield = new MinefieldType[minefield.length][minefield[0].length];
        for (int i = 0; i < minefield.length; i++) {
            for (int j = 0; j < minefield[0].length; j++) {
                this.minefield[i][j] = minefield[i][j];
            }
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ShowMinefieldController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setMinWidth(CHOOSE_MODE_CONTROLLER_WIDTH);
        mainStage.setMinHeight(CHOOSE_MODE_CONTROLLER_HEIGHT);
        mainStage.setResizable(false);
        mainStage.setTitle("Show Minefield");

        setupInterfaceStyle(root);

        menuBar.useSystemMenuBarProperty().set(true);

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);
    }

    public void showStage() {
        mainStage.show();
        menuBar.toFront();
        showMinefield();
    }

    @FXML
    private void closeStage() {
        mainStage.close();
    }

    public void showMinefield() {
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
        if (isMacOS()) {
            AnchorPane.setTopAnchor(gesturePane, 0.0);
        } else {
            AnchorPane.setTopAnchor(gesturePane, 30.0);
        }
        AnchorPane.setBottomAnchor(gesturePane, 0.0);
        gesturePane.reset();

        initializeGridPaneLabels(minefield.length,minefield[0].length);

        minefieldGridPane.setPrefSize(size * 1.2 * minefield[0].length, size * 1.2 * minefield.length);
        minefieldGridPane.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        minefieldGridPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        minefieldGridPane.setAlignment(Pos.CENTER);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setPercentHeight(100.0 / minefield.length);
        minefieldGridPane.getRowConstraints().add(rowConstraints);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100.0 / minefield[0].length);
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
                if (minefield[i][j] == MinefieldType.MINE) {
                    markGridLabel(i, j, LabelType.BOMBED);
                } else {
                    markGridLabel(i, j, LabelType.CLICKED);
                }
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
                    case BOMBED -> {
                        label.getStyleClass().add("minefieldLabelBombed");
                        label.setText("\uDBC2\uDDFA");
                    }
                    case CLICKED -> {
                        label.getStyleClass().add("minefieldLabelPressed");
                        label.setText(labelText[minefield[row][column].getCode()]);
                    }
                }

                label.setCache(true);
                label.setCacheShape(true);
                label.setCacheHint(CacheHint.SPEED);

                minefieldGridPane.add(label, column, row);
                return;
            }
        }

    }

}

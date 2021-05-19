package app;

import SupportingFiles.GameDecoder;
import animatefx.animation.*;
import app.Minefield.AgainstAIController;
import app.Minefield.MultiplayerMinefieldController;
import app.Minefield.SinglePlayerMinefieldController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import javax.annotation.CheckForNull;
import java.io.IOException;

import static Extensions.TypeCasting.CastInt.*;
import static app.PublicDefinitions.*;

public class ChooseSizeController {

    private final Stage mainStage;

    int numberOfPlayers;
    int clicksPerMove;
    int timeout;
    boolean isComputerMode = false;
    int aiDifficulty = 1;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Label informationLabel;

    /**
     * Creates ChooseSizeController.
     * @param numberOfPlayers 1 means classic single player mode, 2-4 mean multiplayer mode.
     * @param x Window position x.
     * @param y Window position y.
     */
    public ChooseSizeController(int numberOfPlayers, int clicksPerMove, int timeout, double x, double y) throws IOException {
        this.numberOfPlayers = numberOfPlayers;
        this.clicksPerMove = clicksPerMove;
        this.timeout = timeout;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseSizeController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setMinWidth(CHOOSE_SIZE_CONTROLLER_WIDTH);
        mainStage.setMinHeight(CHOOSE_SIZE_CONTROLLER_HEIGHT);
        mainStage.setX(x);
        mainStage.setY(y);
        mainStage.setResizable(false);
        switch (numberOfPlayers) {
            case 1 -> mainStage.setTitle("Minesweeper - Single Player");
            case 2 -> mainStage.setTitle("Minesweeper - Multiplayer - 2 Players");
            case 3 -> mainStage.setTitle("Minesweeper - Multiplayer - 3 Players");
            case 4 -> mainStage.setTitle("Minesweeper - Multiplayer - 4 Players");
        }

        setupInterfaceStyle(root);

        menuBar.useSystemMenuBarProperty().set(true);

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);
    }

    /**
     * Creates ChooseSizeController.
     * @param isComputerMode If present, it is human vs computer mode.
     * @param aiDifficulty Computer Strength. 1 is easiest, 3 is hardest.
     * @param x Window position x.
     * @param y Window position y.
     */
    public ChooseSizeController(boolean isComputerMode, int aiDifficulty, int clicksPerMove, double x, double y) throws IOException {
        this.isComputerMode = isComputerMode;
        this.clicksPerMove = clicksPerMove;
        this.aiDifficulty = aiDifficulty;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseSizeController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setMinWidth(CHOOSE_SIZE_CONTROLLER_WIDTH);
        mainStage.setMinHeight(CHOOSE_SIZE_CONTROLLER_HEIGHT);
        mainStage.setX(x);
        mainStage.setY(y);
        mainStage.setResizable(false);
        switch (aiDifficulty) {
            case 1 -> mainStage.setTitle("Minesweeper - Human vs Computer - Easy");
            case 2 -> mainStage.setTitle("Minesweeper - Human vs Computer - Medium");
            case 3 -> mainStage.setTitle("Minesweeper - Human vs Computer - Hard");
            case 4 -> mainStage.setTitle("Minesweeper - Human vs Computer - Impossible");
        }

        setupInterfaceStyle(root);

        menuBar.useSystemMenuBarProperty().set(true);

        if (!isMacOS()) {
            informationLabel.setTranslateY(informationLabel.getLayoutY() + 15.0);
        }

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);
    }

    @FXML
    private void closeStage() {
        mainStage.close();
    }

    @FXML
    private void backToChooseMode() throws IOException {
        double x = mainStage.getX() + (mainStage.getWidth() - CHOOSE_MODE_CONTROLLER_WIDTH)/2;
        double y = mainStage.getY() + (mainStage.getHeight() - CHOOSE_MODE_CONTROLLER_HEIGHT - 30.0)/2;
        ChooseModeController chooseModeController = new ChooseModeController(x,y);
        chooseModeController.showStage();
        mainStage.close();
    }

    @FXML
    public void showStage() {
        mainStage.show();
    }

    @FXML
    public void showEasyMinefield() throws IOException {
        showMineField(9,9,10);
    }

    @FXML
    public void showMediumMinefield() throws IOException {
        showMineField(16,16,40);
    }

    @FXML
    public void showExpertMinefield() throws IOException {
        showMineField(16,30,99);
    }

    @FXML
    public void showCustomMinefield() {
        Dialog<CustomSizeDialogResults> dialog = new Dialog<>();
        dialog.setTitle("Enter Custom Board Parameters");
        dialog.setHeaderText("Enter custom board parameters...");

        Label playersIcon = new Label("\uDBC2\uDF49");
        playersIcon.setFont(new Font("SF Pro Display Regular", 52));
        dialog.setGraphic(playersIcon);

        DialogPane dialogPane = dialog.getDialogPane();
        setupInterfaceStyle(dialogPane);

        dialog.setX(mainStage.getX() + (mainStage.getWidth() - PLAYER_COUNT_DIALOG_WIDTH)/2);
        dialog.setY(mainStage.getY() + (mainStage.getHeight() - PLAYER_COUNT_DIALOG_HEIGHT - 30.0)/2);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);

        Label rowLabel = new Label("Rows:");
        rowLabel.getStyleClass().add("dialogLabel");
        TextField rowTextField = new TextField("");
        rowTextField.setPromptText("9");
        gridPane.addRow(0,rowLabel,rowTextField);
        GridPane.setMargin(rowTextField,new Insets(0,10,0,10));

        Label columnLabel = new Label("Columns:");
        columnLabel.getStyleClass().add("dialogLabel");
        TextField columnTextField = new TextField("");
        columnTextField.setPromptText("9");
        gridPane.addRow(1,columnLabel,columnTextField);
        GridPane.setMargin(columnTextField,new Insets(0,10,0,10));

        Label mineLabel = new Label("Mines:");
        mineLabel.getStyleClass().add("dialogLabel");
        TextField mineTextField = new TextField("");
        mineTextField.setPromptText("10");
        gridPane.addRow(2,mineLabel,mineTextField);
        GridPane.setMargin(mineTextField,new Insets(0,10,0,10));

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.ALWAYS);
        rowConstraints.setValignment(VPos.CENTER);
        rowConstraints.setPercentHeight(-1);

        ColumnConstraints columnConstraints1 = new ColumnConstraints();
        columnConstraints1.setHgrow(Priority.ALWAYS);
        columnConstraints1.setHalignment(HPos.RIGHT);
        columnConstraints1.setPercentWidth(20);

        ColumnConstraints columnConstraints2 = new ColumnConstraints();
        columnConstraints2.setHgrow(Priority.ALWAYS);
        columnConstraints2.setHalignment(HPos.LEFT);
        columnConstraints2.setPercentWidth(80);

        gridPane.getRowConstraints().addAll(rowConstraints,rowConstraints,rowConstraints);
        gridPane.getColumnConstraints().addAll(columnConstraints1,columnConstraints2);

        dialogPane.setContent(gridPane);

        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(dialogPane.getButtonTypes().get(0));
        cancelButton.getStyleClass().add("cancelButton");

        Button doneButton = (Button) dialogPane.lookupButton(dialogPane.getButtonTypes().get(1));
        doneButton.addEventFilter(ActionEvent.ACTION, ae -> {
            if (rowTextField.getText().equals("")) {
                rowTextField.setText("9");
            }

            if (columnTextField.getText().equals("")) {
                columnTextField.setText("9");
            }

            if (mineTextField.getText().equals("")) {
                mineTextField.setText("10");
            }

            int rows = 0;
            try { rows = Int(rowTextField.getText()); } catch (NumberFormatException ignored) { }

            int columns = 0;
            try { columns = Int(columnTextField.getText()); } catch (NumberFormatException ignored) { }

            int mines = 0;
            try { mines = Int(mineTextField.getText()); } catch (NumberFormatException ignored) { }

            if (!(rows >= 9 && rows <= 24)) {
                Shake shake = new Shake(rowTextField);
                shake.setSpeed(2.0);
                shake.play();
                ae.consume();
            }

            if (!(columns >= 9 && columns <= 30)) {
                Shake shake = new Shake(columnTextField);
                shake.setSpeed(2.0);
                shake.play();
                ae.consume();
            }

            if (!(mines >= 10 && mines <= (rows * columns) / 2)) {
                Shake shake = new Shake(mineTextField);
                shake.setSpeed(2.0);
                shake.play();
                ae.consume();
            }

        });

        doneButton.setText("Done");
        doneButton.getStyleClass().add("doneButton");
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                // Result is entered.
                try {
                    showMineField(Int(rowTextField.getText()), Int(columnTextField.getText()), Int(mineTextField.getText()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        });
        dialog.show();
    }

    private static class CustomSizeDialogResults {
        int rows;
        int columns;
        int mines;

        public CustomSizeDialogResults(int rows, int columns, int mines) {
            this.rows = rows;
            this.columns = columns;
            this.mines = mines;
        }
    }

    @FXML
    public void showMineField(int rows, int columns, int mines) throws IOException {
        // Game always starts at the center of the screen.
        mainStage.hide();
        if (isComputerMode) {
            AgainstAIController againstAIController = new AgainstAIController(rows,columns,mines,AIDifficulty(aiDifficulty));
            return;
        }
        if (numberOfPlayers == 1) {
            SinglePlayerMinefieldController singlePlayerMinefieldController = new SinglePlayerMinefieldController(rows,columns,mines);
        } else {
            MultiplayerMinefieldController multiplayerMinefieldController = new MultiplayerMinefieldController(rows,columns,mines,numberOfPlayers,clicksPerMove,timeout);
        }
    }

    @FXML
    public void openGame() {
        if (GameDecoder.openGame(mainStage)) {
            closeStage();
        }
    }

    @FXML
    public void showPreferences() throws IOException {
        PreferencesController preferencesController = new PreferencesController();
        preferencesController.showStage();
    }

    @FXML
    public void showWelcomeScreen() throws IOException {
        PublicDefinitions.showWelcomeScreen();
    }

    @FXML
    public void showAbout() throws IOException {

    }

}

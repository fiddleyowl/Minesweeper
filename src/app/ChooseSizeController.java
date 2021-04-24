package app;

import animatefx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.regex.Pattern;

import static Extensions.Misc.Print.print;
import static Extensions.TypeCasting.CastInt.Int;
import static app.PublicDefinitions.*;

public class ChooseSizeController {

    private final Stage mainStage;

    int numberOfPlayers;

    @FXML
    private MenuBar menuBar;

    public ChooseSizeController(int numberOfPlayers, double x, double y) throws IOException {
        this.numberOfPlayers = numberOfPlayers;
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
            case 1:
                mainStage.setTitle("Minesweeper - Single Player");
                break;
            case 2:
                mainStage.setTitle("Minesweeper - Multiplayer - 2 Players");
                break;
            case 3:
                mainStage.setTitle("Minesweeper - Multiplayer - 3 Players");
                break;
            case 4:
                mainStage.setTitle("Minesweeper - Multiplayer - 4 Players");
                break;
            default: break;
        }

        setupInterfaceStyle(root);

        menuBar.useSystemMenuBarProperty().set(true);

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);
    }

    @FXML
    private void closeStage() {
        mainStage.close();
    }

    @FXML
    private void backToChooseMode() throws IOException {
        double x = mainStage.getX();
        double y = mainStage.getY();
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
        dialog.setTitle("Dialog Test");
        dialog.setHeaderText("Enter custom board parameters...");

        Label playersIcon = new Label("\uDBC2\uDF49");
        playersIcon.setFont(new Font("SF Pro Display Regular", 52));
        dialog.setGraphic(playersIcon);

        DialogPane dialogPane = dialog.getDialogPane();
        setupInterfaceStyle(dialogPane);
//        dialogPane.setPrefWidth(PLAYER_COUNT_DIALOG_WIDTH);
//        dialogPane.setPrefHeight(PLAYER_COUNT_DIALOG_HEIGHT);

        dialog.setX(mainStage.getX() + (mainStage.getWidth() - PLAYER_COUNT_DIALOG_WIDTH)/2);
        dialog.setY(mainStage.getY() + (mainStage.getHeight() - PLAYER_COUNT_DIALOG_HEIGHT)/2);


        Label rowLabel = new Label("Rows:");
        rowLabel.getStyleClass().add("dialogLabel");
        TextField rowTextField = new TextField("");
        rowTextField.setPromptText("9");
        HBox rowHBox = new HBox(4,rowLabel,rowTextField);
        rowHBox.setAlignment(Pos.CENTER_LEFT);

        Label columnLabel = new Label("Columns:");
        columnLabel.getStyleClass().add("dialogLabel");
        TextField columnTextField = new TextField("");
        columnTextField.setPromptText("9");
        HBox columnHBox = new HBox(4,columnLabel,columnTextField);

        Label mineLabel = new Label("Mines:");
        mineLabel.getStyleClass().add("dialogLabel");
        TextField mineTextField = new TextField("");
        mineTextField.setPromptText("10");
        HBox mineHBox = new HBox(4,mineLabel,mineTextField);

        dialogPane.setContent(new VBox(4,rowHBox,columnHBox,mineHBox));

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
                    MinefieldController minefieldController = new MinefieldController(Int(rowTextField.getText()), Int(columnTextField.getText()), Int(mineTextField.getText()));
                    mainStage.hide();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new CustomSizeDialogResults(Int(rowTextField.getText()), Int(columnTextField.getText()), Int(mineTextField.getText()));
            } else {
                return null;
            }
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
//        double x = mainStage.getX() + (mainStage.getWidth() - CHOOSE_SIZE_CONTROLLER_WIDTH)/2;
//        double y = mainStage.getY() + (mainStage.getHeight() - CHOOSE_SIZE_CONTROLLER_HEIGHT)/2;
        // Game always starts at the center of the screen.
        mainStage.hide();
        MinefieldController minefieldController = new MinefieldController(rows,columns,mines);
//        minefieldController.showStage();
    }

}

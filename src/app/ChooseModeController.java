package app;

import SupportingFiles.GameDecoder;
import animatefx.animation.Shake;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static Extensions.Misc.Print.print;
import static Extensions.TypeCasting.CastInt.*;
import static app.PublicDefinitions.*;

public class ChooseModeController {

    private final Stage mainStage;

    @FXML
    private VBox singlePlayerVBox;
    @FXML
    private VBox multiPlayerVBox;

    @FXML
    private Label informationLabel;

    @FXML
    private MenuBar menuBar;

    public ChooseModeController(double x, double y) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseModeController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setMinWidth(CHOOSE_MODE_CONTROLLER_WIDTH);
        mainStage.setMinHeight(CHOOSE_MODE_CONTROLLER_HEIGHT);
        mainStage.setX(x);
        mainStage.setY(y);
        mainStage.setResizable(false);
        mainStage.setTitle("Minesweeper");

        setupInterfaceStyle(root);

        menuBar.useSystemMenuBarProperty().set(true);

        if (!isMacOS()) {
            informationLabel.setTranslateY(informationLabel.getLayoutY() + 15.0);
        }

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);
    }

    public void showStage() {
        mainStage.show();
    }

    @FXML
    private void closeStage() {
        mainStage.close();
    }

    @FXML
    private void enterSinglePlayerMode() throws IOException {
        double x = mainStage.getX() + (mainStage.getWidth() - CHOOSE_SIZE_CONTROLLER_WIDTH)/2;
        double y = mainStage.getY() + (mainStage.getHeight() - CHOOSE_SIZE_CONTROLLER_HEIGHT - 30.0)/2;
        mainStage.hide();
        ChooseSizeController chooseSizeController = new ChooseSizeController(1,1,0,x,y);
        chooseSizeController.showStage();
    }

    @FXML
    private void enterMultiPlayerMode() throws IOException {
        Dialog<MultiplayerDialogResults> dialog = new Dialog<>();
        DialogPane dialogPane = dialog.getDialogPane();
        setupInterfaceStyle(dialogPane);
//        dialogPane.getStyleClass().add("dialogPane");

        dialog.setResizable(false);

        dialog.setX(mainStage.getX() + (mainStage.getWidth() - PLAYER_COUNT_DIALOG_WIDTH)/2);
        dialog.setY(mainStage.getY() + (mainStage.getHeight() - PLAYER_COUNT_DIALOG_HEIGHT - 30.0)/2);

        dialog.setTitle("Enter Game Parameters");
        Label playersIcon = new Label("\uDBC2\uDFE9");
        playersIcon.setFont(new Font("SF Pro Display Regular", 52));
        dialog.setGraphic(playersIcon);
        dialog.setHeaderText("Select multiplayer game parameters.");

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);

        Label numberLabel = new Label("Number of Players:");
        numberLabel.getStyleClass().add("dialogLabel");
        ComboBox<String> numberChoice = new ComboBox<>();
        numberChoice.getItems().add("2");
        numberChoice.getItems().add("3");
        numberChoice.getItems().add("4");
        numberChoice.setValue("2");
        numberChoice.setMaxWidth(Double.MAX_VALUE);

        gridPane.addRow(0,numberLabel,numberChoice);
        GridPane.setMargin(numberChoice,new Insets(0,10,0,10));

        Label clicksLabel = new Label("Clicks Per Move:");
        clicksLabel.getStyleClass().add("dialogLabel");
        ComboBox<String> clicksChoice = new ComboBox<>();
        clicksChoice.getItems().add("1");
        clicksChoice.getItems().add("2");
        clicksChoice.getItems().add("3");
        clicksChoice.getItems().add("4");
        clicksChoice.getItems().add("5");
        clicksChoice.setValue("1");
        clicksChoice.setMaxWidth(Double.MAX_VALUE);

        gridPane.addRow(1,clicksLabel,clicksChoice);
        GridPane.setMargin(clicksChoice,new Insets(0,10,0,10));

        Label timeoutLabel = new Label("Timeout:");
        timeoutLabel.getStyleClass().add("dialogLabel");
        TextField timeoutField = new TextField("");
        timeoutField.setPromptText("30");

        gridPane.addRow(2,timeoutLabel,timeoutField);
        GridPane.setMargin(timeoutField,new Insets(0,10,0,10));

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.ALWAYS);
        rowConstraints.setValignment(VPos.CENTER);
        rowConstraints.setPercentHeight(-1);

        ColumnConstraints columnConstraints1 = new ColumnConstraints();
        columnConstraints1.setHgrow(Priority.ALWAYS);
        columnConstraints1.setHalignment(HPos.RIGHT);
        columnConstraints1.setPercentWidth(36);

        ColumnConstraints columnConstraints2 = new ColumnConstraints();
        columnConstraints2.setHgrow(Priority.ALWAYS);
        columnConstraints2.setHalignment(HPos.LEFT);
        columnConstraints2.setPercentWidth(64);

        gridPane.getRowConstraints().addAll(rowConstraints,rowConstraints,rowConstraints);
        gridPane.getColumnConstraints().addAll(columnConstraints1,columnConstraints2);

        dialogPane.setContent(gridPane);

        dialogPane.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        Button cancelButton = (Button) dialogPane.lookupButton(dialogPane.getButtonTypes().get(0));
        cancelButton.getStyleClass().add("cancelButton");

        Button doneButton = (Button) dialogPane.lookupButton(dialogPane.getButtonTypes().get(1));
        doneButton.setText("Done");
        doneButton.getStyleClass().add("doneButton");
        doneButton.addEventFilter(ActionEvent.ACTION, ae -> {
            if (timeoutField.getText().equals("")) {
                timeoutField.setText("30");
            }
            int tempTimeout = 0;
            try { tempTimeout = Int(timeoutField.getText()); } catch (NumberFormatException ignored) { }
            if (tempTimeout < 30 || tempTimeout > 3599) {
                Shake shake = new Shake(timeoutField);
                shake.setSpeed(2.0);
                shake.play();
                ae.consume();
            }
        });

        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                // OK Pressed or Enter pressed.
                double x = mainStage.getX() + (mainStage.getWidth() - CHOOSE_SIZE_CONTROLLER_WIDTH) / 2;
                double y = mainStage.getY() + (mainStage.getHeight() - CHOOSE_SIZE_CONTROLLER_HEIGHT - 30.0) / 2;
                mainStage.hide();
                ChooseSizeController chooseSizeController = null;
                try {
                    chooseSizeController = new ChooseSizeController(Int(numberChoice.getValue()), Int(clicksChoice.getValue()), Int(timeoutField.getText()), x, y);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                chooseSizeController.showStage();
            }
            return null;
        });

        dialog.show();
    }

    private static class MultiplayerDialogResults {
        int numberOfPlayers;
        int clicksPerMove;

        public MultiplayerDialogResults(int numberOfPlayers, int clicksPerMove) {
            this.numberOfPlayers = numberOfPlayers;
            this.clicksPerMove = clicksPerMove;
        }
    }

    @FXML
    private void enterComputerMode() throws IOException {
        List<String> choices = new ArrayList<>();
        choices.add("Easy");
        choices.add("Medium");
        choices.add("Hard");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Easy", choices);
        DialogPane dialogPane = dialog.getDialogPane();
        setupInterfaceStyle(dialogPane);

        dialogPane.setPrefWidth(COMPUTER_LEVEL_DIALOG_WIDTH);
        dialogPane.setPrefHeight(COMPUTER_LEVEL_DIALOG_HEIGHT);

        dialog.setX(mainStage.getX() + (mainStage.getWidth() - COMPUTER_LEVEL_DIALOG_WIDTH)/2);
        dialog.setY(mainStage.getY() + (mainStage.getHeight() - COMPUTER_LEVEL_DIALOG_HEIGHT - 30.0)/2);

        Button cancelButton = (Button) dialogPane.lookupButton(dialogPane.getButtonTypes().get(1));
        cancelButton.getStyleClass().add("cancelButton");

        Button doneButton = (Button) dialogPane.lookupButton(dialogPane.getButtonTypes().get(0));
        doneButton.setText("Done");
        doneButton.getStyleClass().add("doneButton");

        dialog.setTitle("Enter Game Parameters");
        Label playersIcon = new Label("\uDBC2\uDD4F");
        playersIcon.setFont(new Font("SF Pro Display Regular", 52));
        dialog.setGraphic(playersIcon);
        dialog.setHeaderText("Select computer level.");
        dialog.setContentText("Computer Level:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            double x = mainStage.getX() + (mainStage.getWidth() - CHOOSE_SIZE_CONTROLLER_WIDTH)/2;
            double y = mainStage.getY() + (mainStage.getHeight() - CHOOSE_SIZE_CONTROLLER_HEIGHT - 30.0)/2;
            mainStage.hide();
            ChooseSizeController chooseSizeController = switch (result.get()) {
                case "Easy" -> new ChooseSizeController(true,1, 1, x, y);
                case "Medium" -> new ChooseSizeController(true,2, 1, x, y);
                case "Hard" -> new ChooseSizeController(true,3, 1, x, y);
                default -> throw new IllegalStateException("Unexpected value: " + result.get());
            };
            chooseSizeController.showStage();
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

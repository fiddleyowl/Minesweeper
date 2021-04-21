package app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static Extensions.Misc.Print.print;
import static Extensions.TypeCasting.CastInt32.*;
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

        setupDarkMode(root);

        menuBar.useSystemMenuBarProperty().set(true);

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
        double x = mainStage.getX();
        double y = mainStage.getY();
        mainStage.hide();
        ChooseSizeController chooseSizeController = new ChooseSizeController(1,x,y);
        chooseSizeController.showStage();
    }

    @FXML
    private void enterMultiPlayerMode() throws IOException {

        List<String> choices = new ArrayList<>();
        choices.add("2");
        choices.add("3");
        choices.add("4");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("2", choices);
        DialogPane dialogPane = dialog.getDialogPane();
        setupDarkMode(dialogPane);

        dialogPane.setPrefWidth(PLAYER_COUNT_DIALOG_WIDTH);
        dialogPane.setPrefHeight(PLAYER_COUNT_DIALOG_HEIGHT);

        dialog.setX(mainStage.getX() + (mainStage.getWidth() - PLAYER_COUNT_DIALOG_WIDTH)/2);
        dialog.setY(mainStage.getY() + (mainStage.getHeight() - PLAYER_COUNT_DIALOG_HEIGHT)/2);

        Button cancelButton = (Button) dialogPane.lookupButton(dialogPane.getButtonTypes().get(1));
        cancelButton.getStyleClass().add("cancelButton");

        Button doneButton = (Button) dialogPane.lookupButton(dialogPane.getButtonTypes().get(0));
        doneButton.setText("Done");
        doneButton.getStyleClass().add("doneButton");

//        ComboBox comboBox = (ComboBox) dialogPane.lookup(".combo-box");
//        comboBox.getStyleClass().add("comboBox");

        dialog.setTitle("How Many Players?");
        Label playersIcon = new Label("\uDBC2\uDFE9");
        playersIcon.setFont(new Font("SF Pro Display Regular", 52));
        dialog.setGraphic(playersIcon);
        dialog.setHeaderText("Select number of players.");
        dialog.setContentText("Number of players:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
//            System.out.println("Your choice: " + result.get());
            double x = mainStage.getX() + (mainStage.getWidth() - MINEFIELD_CONTROLLER_WIDTH)/2;
            double y = mainStage.getY() + (mainStage.getHeight() - MINEFIELD_CONTROLLER_HEIGHT)/2;
            mainStage.hide();
            ChooseSizeController chooseSizeController = new ChooseSizeController(Int32(result.get()),x,y);
            chooseSizeController.showStage();
        }

    }

}

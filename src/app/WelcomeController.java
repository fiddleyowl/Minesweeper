package app;

import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.event.ActionEvent;

import java.io.*;

import static Extensions.Misc.Print.*;
import static app.PublicDefinitions.*;

public class WelcomeController {

    private final Stage welcomeStage;

    @FXML
    private Button continueButton;

    public WelcomeController() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("WelcomeController.fxml"));
        loader.setController(this);
        Parent root = loader.load();

        setupInterfaceStyle(root);

        welcomeStage = new Stage();
        welcomeStage.setTitle("Minesweeper");
        welcomeStage.setMinWidth(WELCOME_CONTROLLER_WIDTH);
        welcomeStage.setMinHeight(WELCOME_CONTROLLER_HEIGHT);
        welcomeStage.setResizable(false);

        Scene welcomeScene = new Scene(root, WELCOME_CONTROLLER_WIDTH, WELCOME_CONTROLLER_HEIGHT);
        welcomeStage.setScene(welcomeScene);
    }

    public void showStage() { welcomeStage.showAndWait(); }

    @FXML
    private void continueToMain(ActionEvent event) throws IOException {
        print("Continue Button Clicked.");

        Stage thisStage = (Stage) continueButton.getScene().getWindow();
        thisStage.hide();

        File doNotShowWelcomeFile = new File(appDirectory + pathSeparator + ".do_not_show_welcome");

        if (doNotShowWelcomeFile.exists()) {
            // Opened the window from menu.
        } else {
            // First launch.
            boolean createNewFileBoolean = doNotShowWelcomeFile.createNewFile();
            if (createNewFileBoolean) {
                System.out.printf("Successfully created new file at %s", doNotShowWelcomeFile.getAbsolutePath());
            } else {
                print("Failed to create new file.");
            }


            double x = thisStage.getX() + (thisStage.getWidth() - CHOOSE_MODE_CONTROLLER_WIDTH)/2;
            double y = thisStage.getY() + (thisStage.getHeight() - CHOOSE_MODE_CONTROLLER_HEIGHT - 30.0)/2;
            ChooseModeController chooseModeController = new ChooseModeController(x,y);
            chooseModeController.showStage();
        }
    }
}

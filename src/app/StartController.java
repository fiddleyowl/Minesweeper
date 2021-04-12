package app;

import com.jthemedetecor.OsThemeDetector;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static Extensions.Misc.Print.*;
import static app.PublicDefinitions.*;

public class StartController extends Application {

    @FXML
    private Label initializeLabel;

    public static void main(String[] args) {
        print("Start Controller Starting...");
        System.setProperty("apple.awt.application.appearance","dark");
        java.awt.Toolkit.getDefaultToolkit();
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("StartController.fxml"));
        Parent root = loader.load();
        Scene startScene = new Scene(root,200,200);

        setupDarkMode(root);

        /*
        if (isMacOS()) {
            primaryStage.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> ov, Boolean onHidden, Boolean onShown) {
                    root.getStylesheets().remove("/resources/Style/Darcula.css");
                    root.getStylesheets().remove("/resources/Style/Light.css");
                    if(isMacOSDark()) {
                        root.getStylesheets().add("/resources/Style/Darcula.css");
                    } else {
                        root.getStylesheets().add("/resources/Style/Light.css");
                    }
                }
            });
        }
*/

        primaryStage.setScene(startScene);
        primaryStage.show();
        print("Start Controller Started.");

        // Create a directory in user home to store further configuration.
        boolean directoryCreated = new File(homeDirectory, ".Minesweeper").mkdirs();
        print("Creating directory.");
        boolean directoryExists = Files.isDirectory(Paths.get(appDirectory));
        if (directoryExists) {
            System.out.printf("Directory %s already exists.\n",appDirectory);
        } else {

        }

        primaryStage.hide();

        final String showWelcomePath = appDirectory + File.separator + ".do_not_show_welcome";
        boolean fileExists = Files.exists(Paths.get(showWelcomePath));
        if(fileExists) {
            print("Directly show main screen.");
            double x = (screenWidth - CHOOSE_MODE_CONTROLLER_WIDTH) / 2;
            double y = (screenHeight - CHOOSE_MODE_CONTROLLER_HEIGHT) / 2;
            ChooseModeController chooseModeController = new ChooseModeController(x,y);
            chooseModeController.showStage();
        } else {
            print("Show welcome screen.");
            WelcomeController welcomeController = new WelcomeController();
            welcomeController.showStage();
        }
    }

}

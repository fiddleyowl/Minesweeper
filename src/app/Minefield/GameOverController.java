package app.Minefield;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static app.PublicDefinitions.*;

public class GameOverController {

    private final Stage mainStage;

    public GameOverController(SinglePlayerMinefieldController singlePlayerMinefieldController) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseModeController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setMinWidth(GAME_OVER_CONTROLLER_WIDTH);
        mainStage.setMinHeight(GAME_OVER_CONTROLLER_HEIGHT);
        mainStage.setResizable(false);
        mainStage.setTitle("Minesweeper");

        setupInterfaceStyle(root);

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);
    }

    public GameOverController(MultiplayerMinefieldController multiplayerMinefieldController) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseModeController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setMinWidth(GAME_OVER_CONTROLLER_WIDTH);
        mainStage.setMinHeight(GAME_OVER_CONTROLLER_HEIGHT);
        mainStage.setResizable(false);
        mainStage.setTitle("Minesweeper");

        setupInterfaceStyle(root);

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);
    }

    public GameOverController(AgainstAIController againstAIController) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChooseModeController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setMinWidth(GAME_OVER_CONTROLLER_WIDTH);
        mainStage.setMinHeight(GAME_OVER_CONTROLLER_HEIGHT);
        mainStage.setResizable(false);
        mainStage.setTitle("Minesweeper");

        setupInterfaceStyle(root);

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
}

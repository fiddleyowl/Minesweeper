package app.Minefield;

import app.ChooseModeController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

import static Extensions.Misc.Print.print;
import static app.PublicDefinitions.*;

public class GameOverController {

    private Stage mainStage;

    @FXML
    Label descriptionTextLabel;

    @FXML
    Label gameOverIconLabel;

    @FXML
    Button playSameBoardButton;

    @FXML
    Button restartButton;

    @FXML
    Button homeButton;

    MinefieldController minefieldController;

    public GameOverController(SinglePlayerMinefieldController singlePlayerMinefieldController) throws IOException {
        minefieldController = singlePlayerMinefieldController;
        loadStage();

        if (singlePlayerMinefieldController.isWin) {
            gameOverIconLabel.setText("\uDBC0\uDFB8\uDBC0\uDECA");
            long duration = singlePlayerMinefieldController.stopTime - singlePlayerMinefieldController.startTime;
            long second = (duration / 1000) % 60;
            long minute = (duration / (1000 * 60)) % 60;
            long hour = (duration / (1000 * 60 * 60)) % 24;
            String time = String.format("%02d:%02d:%02d", hour, minute, second);
            descriptionTextLabel.setText("Congratulations! You won the game within " + time + ".");
        } else {
            gameOverIconLabel.setText("\uDBC2\uDDFA");
//            gameOverIconLabel.getStyleClass().add("gameOverIconLabelLose");
            gameOverIconLabel.setStyle("-fx-text-fill: -mine-red;");
        }
    }

    public GameOverController(MultiplayerMinefieldController multiplayerMinefieldController) throws IOException {
        minefieldController = multiplayerMinefieldController;
        loadStage();

        long duration = multiplayerMinefieldController.stopTime - multiplayerMinefieldController.startTime;
        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;
        long hour = (duration / (1000 * 60 * 60)) % 24;
        String time = String.format("%02d:%02d:%02d", hour, minute, second);

        switch (multiplayerMinefieldController.winnerIndex) {
            case -1:
                descriptionTextLabel.setText("All players tied the game!");
                break;
            case 0:
                gameOverIconLabel.setText("\uDBC0\uDC05\uDBC1\uDFEE");
                descriptionTextLabel.setText("A won the game within " + time + "!");
                break;
            case 1:
                gameOverIconLabel.setText("\uDBC0\uDC07\uDBC1\uDFEE");
                descriptionTextLabel.setText("B won the game within " + time + "!");
                break;
            case 2:
                gameOverIconLabel.setText("\uDBC0\uDC09\uDBC1\uDFEE");
                descriptionTextLabel.setText("C won the game within " + time + "!");
                break;
            case 3:
                gameOverIconLabel.setText("\uDBC0\uDC0B\uDBC1\uDFEE");
                descriptionTextLabel.setText("D won the game within " + time + "!");
                break;
        }
    }

    public GameOverController(AgainstAIController againstAIController) throws IOException {
        loadStage();
    }

    public void loadStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameOverController.fxml"));
        loader.setController(this);

        Parent root = loader.load();
        mainStage = new Stage();
        mainStage.setMinWidth(GAME_OVER_CONTROLLER_WIDTH);
        mainStage.setMinHeight(GAME_OVER_CONTROLLER_HEIGHT);
        mainStage.setResizable(false);
        mainStage.setAlwaysOnTop(true);
        mainStage.setTitle("Game Over!");

        setupInterfaceStyle(root);

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        homeButton.requestFocus();
        homeButton.setDefaultButton(true);
    }

    public void showStage() {
        mainStage.show();
    }

    @FXML
    private void closeStage() {
        mainStage.close();
    }

    @FXML
    private void playSameBoard() throws IOException {
        print("playSameBoard");
//        minefieldController
        minefieldController.playSameBoard();
        closeStage();
    }

    @FXML
    private void restartNewGame() throws IOException {
        print("restartNewGame");
        minefieldController.restartNewGame();
        closeStage();
    }

    @FXML
    private void home() throws IOException {
        print("home");
        double x = (screenWidth - CHOOSE_MODE_CONTROLLER_WIDTH) / 2;
        double y = (screenHeight - CHOOSE_MODE_CONTROLLER_HEIGHT) / 2;
        ChooseModeController chooseModeController = new ChooseModeController(x,y);
        chooseModeController.showStage();
        minefieldController.mainStage.setFullScreen(false);
        minefieldController.closeStage();
        closeStage();
    }
}

package app.Minefield;

import app.ChooseModeController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
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
            gameOverIconLabel.setText("\uDBC0\uDCF2");
            gameOverIconLabel.setStyle("-fx-text-fill: -system-green;");
            long duration = singlePlayerMinefieldController.stopTime - singlePlayerMinefieldController.startTime;
            long second = (duration / 1000) % 60;
            long minute = (duration / (1000 * 60)) % 60;
            long hour = (duration / (1000 * 60 * 60)) % 24;
            String time = String.format("%02d:%02d:%02d", hour, minute, second);
            descriptionTextLabel.setText("Congratulations! You won the game within " + time + ".");
            descriptionTextLabel.setFont(new Font("SF Pro Display Regular",20));
        } else {
            gameOverIconLabel.setText("\uDBC2\uDDFA");
//            gameOverIconLabel.getStyleClass().add("gameOverIconLabelLose");
            gameOverIconLabel.setStyle("-fx-text-fill: -mine-red;");
            descriptionTextLabel.setText("Better Luck Next Time!");
            descriptionTextLabel.setFont(new Font("SF Pro Display Regular",30));
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

        String gameOverIconLabelText = "";
        String descriptionTextLabelText = "";
        int textSmall = 20;
        int textMedium = 24;
        int textLarge = 30;
        int iconMedium = 52;
        int iconLarge = 64;
        int gameOverIconLabelSize = iconLarge;
        int descriptionTextLabelSize = textMedium;
        String A = "\uDBC0\uDC05";
        String B = "\uDBC0\uDC07";
        String C = "\uDBC0\uDC09";
        String D = "\uDBC0\uDC0B";
        String clap = "\uDBC1\uDFEE";

        if (multiplayerMinefieldController.numberOfPlayers == 2) {
            switch (multiplayerMinefieldController.winnerIndex) {
                // May be 1,2,3.
                case 1 -> {
                    // A won the game.
                    gameOverIconLabelText = A + clap;
                    descriptionTextLabelText = "A won the game within " + time + "!";
                }
                case 2 -> {
                    // B won the game.
                    gameOverIconLabelText = B + clap;
                    descriptionTextLabelText = "B won the game within " + time + "!";
                }
                case 3 -> {
                    // A and B are tied.
                    gameOverIconLabelText = "\uDBC2\uDC27";
                    gameOverIconLabel.setStyle("-fx-text-fill: -system-orange;");
                    descriptionTextLabelText = "Both players tied for winner!";
                }
            }
        }

        if (multiplayerMinefieldController.numberOfPlayers == 3) {
            // May be 1,2,3,4,5,6,7
            switch (multiplayerMinefieldController.winnerIndex) {
                // May be 1-15.
                case 1 -> {
                    // A won the game.
                    gameOverIconLabelText = A + clap;
                    descriptionTextLabelText = "A won the game within " + time + "!";
                }
                case 2 -> {
                    // B won the game.
                    gameOverIconLabelText = B + clap;
                    descriptionTextLabelText = "B won the game within " + time + "!";
                }
                case 3 -> {
                    // A and B are tied.
                    gameOverIconLabelText = A + B;
                    descriptionTextLabelSize = textLarge;
                    descriptionTextLabelText = "A and B tied for winner!";
                }
                case 4 -> {
                    // C won the game.
                    gameOverIconLabelText = C + clap;
                    descriptionTextLabelText = "C won the game within " + time + "!";
                }
                case 5 -> {
                    // A and C are tied.
                    gameOverIconLabelText = A + C;
                    descriptionTextLabelSize = textLarge;
                    descriptionTextLabelText = "A and C tied for winner!";
                }
                case 6 -> {
                    // B and C are tied.
                    gameOverIconLabelText = B + C;
                    descriptionTextLabelSize = textLarge;
                    descriptionTextLabelText = "B and C tied for winner!";
                }
                case 7 -> {
                    // A, B and C are tied.
                    gameOverIconLabelText = "\uDBC2\uDC27";
                    gameOverIconLabel.setStyle("-fx-text-fill: -system-orange;");
                    descriptionTextLabelText = "Amazing! All players tied for winner!";
                }
            }
        }

        if (multiplayerMinefieldController.numberOfPlayers == 4) {
            switch (multiplayerMinefieldController.winnerIndex) {
                // May be 1-15.
                case 1 -> {
                    // A won the game.
                    gameOverIconLabelText = A + clap;
                    descriptionTextLabelText = "A won the game within " + time + "!";
                }
                case 2 -> {
                    // B won the game.
                    gameOverIconLabelText = B + clap;
                    descriptionTextLabelText = "B won the game within " + time + "!";
                }
                case 3 -> {
                    // A and B are tied.
                    gameOverIconLabelText = A + B;
                    descriptionTextLabelSize = textLarge;
                    descriptionTextLabelText = "A and B tied for winner!";
                }
                case 4 -> {
                    // C won the game.
                    gameOverIconLabelText = C + clap;
                    descriptionTextLabelText = "C won the game within " + time + "!";
                }
                case 5 -> {
                    // A and C are tied.
                    gameOverIconLabelText = A + C;
                    descriptionTextLabelSize = textLarge;
                    descriptionTextLabelText = "A and C tied for winner!";
                }
                case 6 -> {
                    // B and C are tied.
                    gameOverIconLabelText = B + C;
                    descriptionTextLabelSize = textLarge;
                    descriptionTextLabelText = "B and C tied for winner!";
                }
                case 7 -> {
                    // A, B and C are tied.
                    gameOverIconLabelText = A + B + C;
                    gameOverIconLabelSize = iconMedium;
                    descriptionTextLabelText = "Amazing! A, B and C tied for winner!";
                }
                case 8 -> {
                    // D won the game.
                    gameOverIconLabelText = D + clap;
                    descriptionTextLabelText = "D won the game within " + time + "!";
                }
                case 9 -> {
                    // A and D are tied.
                    gameOverIconLabelText = A + D;
                    descriptionTextLabelSize = textLarge;
                    descriptionTextLabelText = "A and D tied for winner!";
                }
                case 10 -> {
                    // B and D are tied.
                    gameOverIconLabelText = B + D;
                    descriptionTextLabelSize = textLarge;
                    descriptionTextLabelText = "B and D tied for winner!";
                }
                case 11 -> {
                    // A, B and D are tied.
                    gameOverIconLabelText = A + B + D;
                    gameOverIconLabelSize = iconMedium;
                    descriptionTextLabelText = "Amazing! A, B and D tied for winner!";
                }
                case 12 -> {
                    // C and D are tied.
                    gameOverIconLabelText = C + D;
                    descriptionTextLabelSize = textLarge;
                    descriptionTextLabelText = "C and D tied for winner!";
                }
                case 13 -> {
                    // A, C and D are tied.
                    gameOverIconLabelText = A + C + D;
                    gameOverIconLabelSize = iconMedium;
                    descriptionTextLabelText = "Amazing! A, C and D tied for winner!";
                }
                case 14 -> {
                    // B, C and D are tied.
                    gameOverIconLabelText = B + C + D;
                    gameOverIconLabelSize = iconMedium;
                    descriptionTextLabelText = "Amazing! B, C and D tied for winner!";
                }
                case 15 -> {
                    // B, C and D are tied.
                    gameOverIconLabelText = "\uDBC2\uDC27";
                    gameOverIconLabel.setStyle("-fx-text-fill: -system-orange;");
                    descriptionTextLabelText = "Incredible! All four players tied for winner!";
                }
            }
        }

        gameOverIconLabel.setText(gameOverIconLabelText);
        gameOverIconLabel.setFont(new Font("SF Pro Display Regular",gameOverIconLabelSize));
        descriptionTextLabel.setText(descriptionTextLabelText);
        descriptionTextLabel.setFont(new Font("SF Pro Display Regular",descriptionTextLabelSize));
    }

    public GameOverController(AgainstAIController againstAIController) throws IOException {
        minefieldController = againstAIController;
        loadStage();
        print(againstAIController.winnerIndex);

        if (againstAIController.winnerIndex == 0) {
            gameOverIconLabel.setStyle("-fx-text-fill: -system-green;");
            gameOverIconLabel.setText("\uDBC0\uDE73");
            descriptionTextLabel.setText("You have beaten the computer!");
        } else if (againstAIController.winnerIndex == 1) {
            gameOverIconLabel.setStyle("-fx-text-fill: -mine-red;");
            gameOverIconLabel.setText("\uDBC2\uDD7A");
            descriptionTextLabel.setText("Computer has beaten you!");
        } else {
            gameOverIconLabel.setStyle("-fx-text-fill: -system-orange;");
            descriptionTextLabel.setText("You tied the computer!");
        }

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

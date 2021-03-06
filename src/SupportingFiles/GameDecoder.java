package SupportingFiles;

import SupportingFiles.DataModels.GameModel;
import app.Minefield.AgainstAIController;
import app.Minefield.MultiplayerMinefieldController;
import app.Minefield.SinglePlayerMinefieldController;
import com.google.gson.Gson;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileReader;

import static Extensions.Misc.Print.print;
import static app.PublicDefinitions.*;
import static SupportingFiles.DataModels.GameModel.*;

public class GameDecoder {
    static Gson gson = new Gson();

    public static boolean openGame(Stage mainStage) {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for json files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON", "*.json");
        fileChooser.getExtensionFilters().add(extFilter);

        Dialog dialog = new Dialog();
        dialog.setTitle("Error");
        setupInterfaceStyle(dialog.getDialogPane());
        dialog.setResizable(false);
        dialog.getDialogPane().setPrefWidth(dialog.getDialogPane().getWidth()+1);
        dialog.setHeight(dialog.getDialogPane().getHeight()+1);
        dialog.setHeaderText("Unable to Open Game.");
        Label warningLabel = new Label("\uDBC0\uDEFB");
        warningLabel.setFont(new Font("SF Pro Display Regular",52));
        warningLabel.setStyle("-fx-text-fill: -mine-red;");
        dialog.setGraphic(warningLabel);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(dialog.getDialogPane().getButtonTypes().get(0));
        cancelButton.getStyleClass().add("doneButton");

        String contentText = "";
        boolean succeeded = false;

        //Show open file dialog
        File file = fileChooser.showOpenDialog(mainStage);
        if (file != null) {
            try {
                GameModel gameModel = GameDecoder.decodeGame(file.getAbsolutePath());
                verifyGameIntegrity(gameModel);
                switch (gameModel.numberOfPlayers) {
                    case 1:
                        SinglePlayerMinefieldController singlePlayerMinefieldController = new SinglePlayerMinefieldController(gameModel, file.getAbsolutePath());
                        break;
                    case -1:
                        AgainstAIController againstAIController = new AgainstAIController(gameModel, file.getAbsolutePath());
                        break;
                    default:
                        MultiplayerMinefieldController multiplayerMinefieldController = new MultiplayerMinefieldController(gameModel,file.getAbsolutePath());
                }
                succeeded = true;
            } catch (Exception e) {
                e.printStackTrace();
                contentText = e.getMessage();
            }
        } else {
            contentText = "File does not exist.";
            return false;
        }

        if (succeeded) {
            return true;
        } else {
            Label contentLabel = new Label(contentText);
            contentLabel.setFont(new Font("SF Pro Display Regular",15));
            contentLabel.setStyle("-fx-text-fill: -text-color");
            dialog.getDialogPane().setContent(contentLabel);
            dialog.show();
            return false;
        }
    }

    public static GameModel decodeGame(String path) throws Exception {
        FileReader reader = new FileReader(path);
        GameModel gameModel = new GameModel();
        gameModel = gson.fromJson(reader, GameModel.class);
        return gameModel;
    }

    public static void verifyGameIntegrity(GameModel gameModel) throws Exception {
        //region Check Number of Players
        if (gameModel.numberOfPlayers == 0 || gameModel.numberOfPlayers > 4 || gameModel.numberOfPlayers < -1) {
            throw new InvalidGameException("Invalid Number of Players.");
        }
        //endregion

        //region Check minefield And manipulatedMinefield
        int rows,columns,mines = 0;
        rows = gameModel.minefield.length;
        columns = gameModel.minefield[0].length;

        for (MinefieldType[] i: gameModel.minefield) {
            if (i.length != columns) {
                throw new InvalidGameException("Invalid Minefield: Column Count Mismatch.");
            }
            for (MinefieldType j: i) {
                if (j == null) {
                    throw new InvalidGameException("Invalid Minefield: Null In Minefield.");
                }
                if (j == MinefieldType.MINE) {
                    mines += 1;
                }
            }
        }

        if (gameModel.manipulatedMinefield.length != rows) {
            throw new InvalidGameException("Invalid Manipulated Minefield: Row Count Mismatch.");
        }

        int alreadyProvedMines = 0;
        for (LabelType[] i: gameModel.manipulatedMinefield) {
            if (i.length != columns) {
                throw new InvalidGameException("Invalid Manipulated Minefield: Column Count Mismatch.");
            }
            for (LabelType j: i) {
                if (j == null) {
                    throw new InvalidGameException("Invalid Manipulated Minefield: Null In Manipulated Minefield.");
                }
                if (gameModel.numberOfPlayers == 1 && (j == LabelType.CORRECT || j == LabelType.WRONG)) {
                    throw new InvalidGameException("Invalid Manipulated Minefield: Unexpected LabelType.");
                }
                if (gameModel.numberOfPlayers != 1 && j == LabelType.QUESTIONED) {
                    throw new InvalidGameException("Invalid Manipulated Minefield: Unexpected LabelType.");
                }
                if (j == LabelType.CORRECT || j == LabelType.BOMBED) {
                    alreadyProvedMines += 1;
                }
            }
        }

        if (alreadyProvedMines >= mines) {
            throw new InvalidGameException("Invalid Manipulated Minefield: Mine Numbers Mismatch.");
        }

        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                if (gameModel.minefield[row][column] == MinefieldType.MINE && gameModel.manipulatedMinefield[row][column] == LabelType.CLICKED) {
                    throw new InvalidGameException("Invalid Minefield Combination: Successfully Clicked A Mine.");
                }

                if (gameModel.minefield[row][column] != MinefieldType.MINE && (gameModel.manipulatedMinefield[row][column] == LabelType.BOMBED || gameModel.manipulatedMinefield[row][column] == LabelType.CORRECT)) {
                    throw new InvalidGameException("Invalid Minefield Combination: Proved A NON-Mine Square As Mine.");
                }

                if (gameModel.minefield[row][column] != MinefieldType.MINE) {
                    int count = 0;
                    try { if (gameModel.minefield[row - 1][column - 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (gameModel.minefield[row - 1][column] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (gameModel.minefield[row - 1][column + 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (gameModel.minefield[row][column - 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (gameModel.minefield[row][column + 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (gameModel.minefield[row + 1][column - 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (gameModel.minefield[row + 1][column] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    try { if (gameModel.minefield[row + 1][column + 1] == MinefieldType.MINE) { count += 1; } } catch (Exception ignored) { }
                    if (gameModel.minefield[row][column] != MinefieldType(count)) {
                        throw new InvalidGameException("Invalid Minefield: Invalid Surrounding Mine Numbers.");
                    }
                }

            }
        }
        //endregion

        //region Check Time
        if (gameModel.timeUsed < 0) {
            throw new InvalidGameException("Invalid Time Used.");
        }
        //endregion

        //region Check Rounds
        if (gameModel.rounds < 1) {
            throw new InvalidGameException("Invalid Rounds.");
        }
        //endregion

        //region Multiplayer Check
        if (gameModel.numberOfPlayers > 1) {
            if (gameModel.players.length != gameModel.numberOfPlayers) {
                throw new InvalidGameException("Invalid Player Data: Number of Players Mismatch.");
            }
            int totalScore = 0, totalMistakes = 0;
            for (Player player: gameModel.players) {
                if (player == null) {
                    throw new InvalidGameException("Invalid Player Data: Player is Null.");
                }
                if (player.score > mines) {
                    throw new InvalidGameException("Invalid Player Data: Invalid Player Score.");
                }
                if (player.mistakes < 0 || player.mistakes > mines) {
                    throw new InvalidGameException("Invalid Player Data: Invalid Player Mistakes.");
                }
                totalScore += player.score;
                totalMistakes += player.mistakes;
            }
            if (totalScore > mines) {
                throw new InvalidGameException("Invalid Player Data: Invalid Player Score.");
            }
            if (totalScore < -mines) {
                throw new InvalidGameException("Invalid Player Data: Invalid Player Score.");
            }
            if (totalMistakes > mines) {
                throw new InvalidGameException("Invalid Player Data: Invalid Player Mistakes.");
            }
            if (gameModel.activePlayer < 0 || gameModel.activePlayer >= gameModel.numberOfPlayers) {
                throw new InvalidGameException("Invalid Active Player.");
            }
            if (gameModel.clicksPerMove < 1 || gameModel.clicksPerMove > 5) {
                throw new InvalidGameException("Invalid Clicks Per Move.");
            }
            if (gameModel.clicksLeftForActivePlayer <= 0 || gameModel.clicksLeftForActivePlayer > gameModel.clicksPerMove) {
                throw new InvalidGameException("Invalid Clicks Left For Active Player.");
            }
            if (gameModel.timeout < 30 || gameModel.timeout > 3599) {
                throw new InvalidGameException("Invalid Player Timeout.");
            }
            if (gameModel.timeLeftForActivePlayer > gameModel.timeout*1000L) {
                throw new InvalidGameException("Invalid Time Left For Active Player.");
            }
        }
        //endregion

        //region AI Check
        if (gameModel.numberOfPlayers == -1) {
            if (gameModel.aiDifficulty == null) {
                throw new InvalidGameException("Invalid AI Difficulty: Difficulty is Null.");
            }
            if (gameModel.players.length != 2) {
                throw new InvalidGameException("Invalid Player Data: Number of Players Mismatch.");
            }
            int totalScore = 0, totalMistakes = 0;
            for (Player player: gameModel.players) {
                if (player == null) {
                    throw new InvalidGameException("Invalid Player Data: Player is Null.");
                }
                if (player.score > mines) {
                    throw new InvalidGameException("Invalid Player Data: Invalid Player Score.");
                }
                if (player.mistakes < 0 || player.mistakes > mines) {
                    throw new InvalidGameException("Invalid Player Data: Invalid Player Mistakes.");
                }
                totalScore += player.score;
                totalMistakes += player.mistakes;
            }
            if (totalScore > mines) {
                throw new InvalidGameException("Invalid Player Data: Invalid Player Score.");
            }
            if (totalScore < -mines) {
                throw new InvalidGameException("Invalid Player Data: Invalid Player Score.");
            }
            if (totalMistakes > mines) {
                throw new InvalidGameException("Invalid Player Data: Invalid Player Mistakes.");
            }

        }
        //endregion
    }

    public static class InvalidGameException extends Exception {
        public InvalidGameException(String errorMessage) {
            super(errorMessage);
        }
    }

}

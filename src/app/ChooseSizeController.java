package app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import java.io.IOException;

import static Extensions.Misc.Print.print;
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
    public void showMineField(int rows, int columns, int mines) throws IOException {
//        double x = mainStage.getX() + (mainStage.getWidth() - CHOOSE_SIZE_CONTROLLER_WIDTH)/2;
//        double y = mainStage.getY() + (mainStage.getHeight() - CHOOSE_SIZE_CONTROLLER_HEIGHT)/2;
        // Game always starts at the center of the screen.
        mainStage.hide();
        MinefieldController minefieldController = new MinefieldController(rows,columns,mines);
//        minefieldController.showStage();
    }

}

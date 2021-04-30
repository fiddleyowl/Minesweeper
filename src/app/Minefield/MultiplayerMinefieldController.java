package app.Minefield;

import app.PublicDefinitions;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.awt.*;
import java.io.IOException;

import static Extensions.Misc.Print.print;
import static Extensions.TypeCasting.CastString.String;

public class MultiplayerMinefieldController extends MinefieldController {

    int[] scores;
    int[] mistakes;

    int clicksPerMove = 1;

    public MultiplayerMinefieldController(int rows, int columns, int mines, int numberOfPlayers, int clicksPerMove) throws IOException {
        super(rows, columns, mines);
        this.clicksPerMove = clicksPerMove;
        scores = new int[numberOfPlayers];
        mistakes = new int[numberOfPlayers];
    }

    @Override
    void clickedOnLabel(PublicDefinitions.MouseClickType type, int row, int column) {

    }

    @Override
    void initializeRightBorderPane() {
        GridPane playerInformationGridPane = new GridPane();
        playerInformationGridPane.setGridLinesVisible(true);

//        playerInformationGridPane.add();
        Label label1 = new Label("\uDBC0\uDC05");
        label1.setFont(new Font("SF Pro Display Regular", 80));

        VBox vBox = new VBox();

        rightBorderPane.setCenter(playerInformationGridPane);
    }

    /**
     * <p>A UI method to update informative labels on the right side of the minefield.</p>
     * <p><i>startTime</i> and <i>stopTime</i> are used to calculate elapsed time.</p>
     * <p><i>discoveredMines</i> is used to calculate remaining mines.</p>
     */
    @Override
    public void updateInformativeLabels() {
        /*
        long duration = stopTime - startTime;
        long second = (duration / 1000) % 60;
        long minute = (duration / (1000 * 60)) % 60;
        long hour = (duration / (1000 * 60 * 60)) % 24;
        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        timerLabel.setText(time);
        */
        mineLabel.setText(String(mines - discoveredMines));
    }

    @Override
    void closeStage() {

    }
}

package app.Minefield;

import app.PublicDefinitions;

import java.io.IOException;

import static Extensions.TypeCasting.CastString.String;

public class MultiplayerMinefieldController extends MinefieldController {

    public MultiplayerMinefieldController(int rows, int columns, int mines, int numberOfPlayers) throws IOException {
        super(rows, columns, mines);
    }

    @Override
    void clickedOnLabel(PublicDefinitions.MouseClickType type, int row, int column) {

    }

    @Override
    void initializeRightAnchorPane() {

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

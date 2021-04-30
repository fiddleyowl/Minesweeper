package app.Minefield;

import SupportingFiles.Audio.Sound;
import app.PublicDefinitions;
import javafx.application.Platform;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import static app.PublicDefinitions.*;

public class AgainstAIController extends MinefieldController {

    //region Variables Declaration

    boolean isFirstClick = true;
    int discoveredMines = 0;
    boolean shouldStop = false;
    boolean isWin = false;

    boolean isSaved = false;

    /**
     * Marks the time the game started.
     */
    long startTime;
    /**
     * Marks the time the game stopped. If the game is not stopped, marks the current time (every 200ms).
     */
    long stopTime;

    /**
     * <p>Updates <i>stopTime</i> and informative labels every 200ms.</p>
     * <p>If <i>shouldStop</i> becomes true, the loop returns.</p>
     */
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            //todo rewrite this implement to fit the AI against mode
            startTime = System.currentTimeMillis();
            while (true) {
//                print("while");
                stopTime = System.currentTimeMillis();
                Platform.runLater(() -> updateInformativeLabels());
                if (shouldStop) {
                    music.stop();
                    try {
                        if (isWin) {
                            Sound.win();
                        } else {
                            Sound.gameOver();
                        }
                    } catch (Exception ignored) {
                    }
                    return;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                }
            }
        }
    });

    //endregion
    public AgainstAIController(int rows, int columns, int mines) throws IOException {
        super(rows, columns, mines);
    }

    @Override
    void clickedOnLabel(MouseClickType type, int row, int column) {
        if (shouldStop) {
            return;
        }
    }

    @Override
    void initializeRightBorderPane() {

    }

    @Override
    void updateInformativeLabels() {

    }

    @Override
    void closeStage() {

    }
}

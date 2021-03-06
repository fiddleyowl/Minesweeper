package app;

import SupportingFiles.Audio.Music;
import com.jthemedetecor.OsThemeDetector;
import javafx.scene.Parent;
import javafx.scene.text.Font;
import javafx.stage.Screen;

import java.io.*;
import java.util.concurrent.TimeUnit;

import static Extensions.Misc.Print.print;
import static SupportingFiles.ConfigHelper.readConfig;

public class PublicDefinitions {
    public static final String homeDirectory = System.getProperty("user.home");
    public static final String pathSeparator = File.separator;
    public static final String systemOS = System.getProperty("os.name").toLowerCase();
    public static final String appDirectory = homeDirectory + pathSeparator + ".Minesweeper";
    public static final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
    public static final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

    public static boolean isMacOS() {
        return systemOS.contains("mac");
    }

    public static boolean isMacOSDark() {
        if (isMacOS()) {
            try {
                final Process proc = Runtime.getRuntime().exec(new String[]{"defaults", "read", "-g", "AppleInterfaceStyle"});
                proc.waitFor(50, TimeUnit.MILLISECONDS);
                // Use shell command "defaults read -g AppleInterfaceStyle" to determine whether system is in dark mode.
                return proc.exitValue() == 0;
                // Command returns "Dark" if system is in dark mode (exit code 0), fails if system is in light mode.
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                // Maybe not running on macOS?
                return false;
            }
        } else {
            return false;
        }
    }

    public static final OsThemeDetector detector = OsThemeDetector.getDetector();

    public static Music music = new Music("/Resources/Music/Raphaël Beau - Micmacs A La Gare.wav");

    /**
     * Sets up interface style for given <i>parent</i>. This includes font loading and interface style.
     * @param parent Node to apply to.
     */
    public static void setupInterfaceStyle(Parent parent) {

        Font.loadFont(Thread.currentThread().getContextClassLoader().getResourceAsStream("Resources/Font/SF-Mono-Regular.otf"), 12);
        Font.loadFont(Thread.currentThread().getContextClassLoader().getResourceAsStream("Resources/Font/SF-Pro-Display-Regular.otf"), 12);
        Font.loadFont(Thread.currentThread().getContextClassLoader().getResourceAsStream("Resources/Font/SF-Pro-Display-Semibold.otf"), 12);

        parent.getStylesheets().clear();

        switch (readConfig().appearance) {
            case 0 -> {
                // Light
                parent.getStylesheets().add("/Resources/Style/Light.css");
                parent.getStylesheets().add("/Resources/Style/Custom.css");
            }
            case 1 -> {
                // Dark
                parent.getStylesheets().add("/Resources/Style/Darcula.css");
                parent.getStylesheets().add("/Resources/Style/Custom.css");
            }
            case 2 -> {
                // Match System
                if (detector.isDark()) {
                    parent.getStylesheets().add("/Resources/Style/Darcula.css");
                } else {
                    parent.getStylesheets().add("/Resources/Style/Light.css");
                }
                parent.getStylesheets().add("/Resources/Style/Custom.css");
            }
        }

        detector.registerListener(isDark -> {
            if (readConfig().appearance == 2) {
                // Only if appearance is set to "Match System".
                parent.getStylesheets().clear();
                if (isDark) {
                    // System switched to a dark theme.
                    parent.getStylesheets().add("/Resources/Style/Darcula.css");
                    parent.getStylesheets().add("/Resources/Style/Custom.css");
                } else {
                    // System switched to a light theme.
                    parent.getStylesheets().add("/Resources/Style/Light.css");
                    parent.getStylesheets().add("/Resources/Style/Custom.css");
                }
            }
        });

    }

    /* Size Constants */
    public static final double WELCOME_CONTROLLER_WIDTH = 900;
    public static final double WELCOME_CONTROLLER_HEIGHT = 600;
    public static final double CHOOSE_MODE_CONTROLLER_WIDTH = 900;
    public static final double CHOOSE_MODE_CONTROLLER_HEIGHT = 400;
    public static final double PLAYER_COUNT_DIALOG_WIDTH = 403;
    public static final double PLAYER_COUNT_DIALOG_HEIGHT = 286;
    public static final double COMPUTER_LEVEL_DIALOG_WIDTH = 300;
    public static final double COMPUTER_LEVEL_DIALOG_HEIGHT = 210;
    public static final double CHOOSE_SIZE_CONTROLLER_WIDTH = 600;
    public static final double CHOOSE_SIZE_CONTROLLER_HEIGHT = 400;
    public static final double MINEFIELD_CONTROLLER_WIDTH = 600;
    public static final double MINEFIELD_CONTROLLER_HEIGHT = 400;
    public static final double GAME_OVER_CONTROLLER_WIDTH = 400;
    public static final double GAME_OVER_CONTROLLER_HEIGHT = 200;
    public static final double PREFERENCES_CONTROLLER_WIDTH = 600;
    public static final double PREFERENCES_CONTROLLER_HEIGHT = 400;

    /**
     * <p>An enumeration that indicates what is under the label.</p>
     * <p>There are only 10 possible cases. MINE, EMPTY, ONE to EIGHT.</p>
     * <p>A number code is manually associated to each case. -1 for MINE, 0 for EMPTY, 1 to 8 for ONE to EIGHT respectively.</p>
     */
    public enum MinefieldType {
        MINE(-1),
        EMPTY(0),
        ONE(1),
        TWO(2),
        THREE(3),
        FOUR(4),
        FIVE(5),
        SIX(6),
        SEVEN(7),
        EIGHT(8);

        private final int code;

        MinefieldType(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    /**
     * Creates a MinefieldType using the given number code.
     *
     * @param code The number code.
     * @return Returns the corresponding MinefieldType if a matching case is found, returns MinefieldType.EMPTY if failed.
     */
    public static MinefieldType MinefieldType(int code) {
        return switch (code) {
            case -1 -> MinefieldType.MINE;
            case 0 -> MinefieldType.EMPTY;
            case 1 -> MinefieldType.ONE;
            case 2 -> MinefieldType.TWO;
            case 3 -> MinefieldType.THREE;
            case 4 -> MinefieldType.FOUR;
            case 5 -> MinefieldType.FIVE;
            case 6 -> MinefieldType.SIX;
            case 7 -> MinefieldType.SEVEN;
            case 8 -> MinefieldType.EIGHT;
            default -> MinefieldType.EMPTY;
        };
    }

    /**
     * <p>An enumeration that indicates what the label should look like.</p>
     * <p>There are 7 possible cases. WRONG, CORRECT, BOMBED, CLICKED, NOT_CLICKED, FLAGGED and QUESTIONED.</p>
     * <p>A number code is manually associated to each case. -4 for WRONGLY FLAGGED, -3 for CORRECTLY FLAGGED, -2 for BOMBED, -1 for CLICKED, 0 for NOT_CLICKED, 1 for FLAGGED, 2 for QUESTIONED.</p>
     */
    public enum LabelType {
        WRONG(-4),
        CORRECT(-3),
        BOMBED(-2),
        CLICKED(-1),
        NOT_CLICKED(0),
        FLAGGED(1),
        QUESTIONED(2);

        private final int code;

        LabelType(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    /**
     * Creates a LabelType using the given number code.
     *
     * @param code The number code.
     * @return Returns the corresponding LabelType if a matching case is found, returns LabelType.NOT_CLICKED if failed.
     */
    public static LabelType LabelType(int code) {
        return switch (code) {
            case -4 -> LabelType.WRONG;
            case -3 -> LabelType.CORRECT;
            case -2 -> LabelType.BOMBED;
            case -1 -> LabelType.CLICKED;
            case 0 -> LabelType.NOT_CLICKED;
            case 1 -> LabelType.FLAGGED;
            case 2 -> LabelType.QUESTIONED;
            default -> LabelType.NOT_CLICKED;
        };
    }

    /**
     * <p>An enumeration that represents how user clicked their mouse.</p>
     * <p>There are 3 possible cases. PRIMARY, SECONDARY and TERTIARY.</p>
     * <p>A number code is manually associated to each case. 0 for PRIMARY, 1 for SECONDARY, 2 for TERTIARY.</p>
     */
    public enum MouseClickType {
        PRIMARY(0),
        SECONDARY(1),
        TERTIARY(2);

        private final int code;

        MouseClickType(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }

    /**
     * Creates a MouseClickType using the given number code.
     *
     * @param code The number code.
     * @return Returns the corresponding MouseClickType if a matching case is found, returns MouseClickType.PRIMARY if failed.
     */
    public static MouseClickType MouseClickType(int code) {
        return switch (code) {
            case 0 -> MouseClickType.PRIMARY;
            case 1 -> MouseClickType.SECONDARY;
            case 2 -> MouseClickType.TERTIARY;
            default -> MouseClickType.PRIMARY;
        };
    }

    /**
     * <p>An enumeration that represents how hard person vs. computer should be.</p>
     * <p>There are 3 possible cases. EASY, MEDIUM and HARD.</p>
     * <p>A number code is manually associated to each case. 1 for EASY, 2 for MEDIUM, 3 for HARD.</p>
     */
    public enum AIDifficulty {
        EASY(1),
        MEDIUM(2),
        HARD(3),
        IMPOSSIBLE(4);

        private final int i;

        AIDifficulty(int i) {
            this.i = i;
        }

        public int getI() {
            return this.i;
        }

        public String getName() {
            switch (this) {
                case EASY -> { return "Easy"; }
                case MEDIUM -> { return "Medium"; }
                case HARD -> { return "Hard"; }
                case IMPOSSIBLE -> { return "Impossible"; }
            }
            return "Easy";
        }
    }

    /**
     * Creates an AIDifficulty using the given number code.
     *
     * @param i The number code.
     * @return Returns the corresponding AIDifficulty if a matching case is found, returns AIDifficulty.EASY if failed.
     */
    public static AIDifficulty AIDifficulty(int i) {
        return switch (i) {
            case 1 -> AIDifficulty.EASY;
            case 2 -> AIDifficulty.MEDIUM;
            case 3 -> AIDifficulty.HARD;
            case 4 -> AIDifficulty.IMPOSSIBLE;
            default -> AIDifficulty.EASY;
        };
    }

    public static void showWelcomeScreen() throws IOException {
        WelcomeController welcomeController = new WelcomeController();
        welcomeController.showStage();
    }
}

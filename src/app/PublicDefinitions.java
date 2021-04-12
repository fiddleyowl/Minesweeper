package app;

import com.jthemedetecor.OsThemeDetector;
import javafx.scene.Parent;
import javafx.stage.Screen;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class PublicDefinitions {
    public static final String homeDirectory = System.getProperty("user.home");
    public static final String pathSeparator = File.separator;
    public static final String systemOS = System.getProperty("os.name").toLowerCase();
    public static final String appDirectory = homeDirectory + pathSeparator + ".Minesweeper";
    public static final double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
    public static final double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

    @Deprecated
    public static boolean isMacOS() {
        return systemOS.contains("mac");
    }

    @Deprecated
    public static boolean isMacOSDark() {
        if (isMacOS()) {
            try {
                final Process proc = Runtime.getRuntime().exec(new String[] {"defaults", "read", "-g", "AppleInterfaceStyle"});
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

    public static void setupDarkMode(Parent parent) {
        if (detector.isDark()) {
            parent.getStylesheets().add("/resources/Style/Darcula.css");
        } else {
            parent.getStylesheets().add("/resources/Style/Light.css");
        }
        detector.registerListener(isDark -> {
            if (isDark) {
                parent.getStylesheets().remove("/resources/Style/Light.css");
                parent.getStylesheets().add("/resources/Style/Darcula.css");
                //The OS switched to a dark theme
            } else {
                parent.getStylesheets().remove("/resources/Style/Darcula.css");
                parent.getStylesheets().add("/resources/Style/Light.css");
                //The OS switched to a light theme
            }
        });
    }

    /* Size Constants */
    public static final double WELCOME_CONTROLLER_WIDTH = 900;
    public static final double WELCOME_CONTROLLER_HEIGHT = 600;
    public static final double CHOOSE_MODE_CONTROLLER_WIDTH = 600;
    public static final double CHOOSE_MODE_CONTROLLER_HEIGHT = 400;
    public static final double PLAYER_COUNT_DIALOG_WIDTH = 300;
    public static final double PLAYER_COUNT_DIALOG_HEIGHT = 210;
    public static final double CHOOSE_SIZE_CONTROLLER_WIDTH = 600;
    public static final double CHOOSE_SIZE_CONTROLLER_HEIGHT = 400;
    public static final double MINEFIELD_CONTROLLER_WIDTH = 1200;
    public static final double MINEFIELD_CONTROLLER_HEIGHT = 800;

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
            default -> MinefieldType.EIGHT;
        };
    }

    public enum LabelType {
        BOMBED(-2),
        CLICKED(-1),
        NOT_CLICKED(0),
        FLAGGED(1),
        QUESTIONED(2);

        private final int code;

        LabelType(int code) {
            this.code = code;
        }

        private int getCode() {
            return this.code;
        }
    }

    public static LabelType LabelType(int code) {
        return switch (code) {
            case -2 -> LabelType.BOMBED;
            case -1 -> LabelType.CLICKED;
            case 0 -> LabelType.NOT_CLICKED;
            case 1 -> LabelType.FLAGGED;
            default -> LabelType.QUESTIONED;
        };
    }

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

    public static MouseClickType MouseClickType(int code) {
        return switch (code) {
            case 0 -> MouseClickType.PRIMARY;
            case 1 -> MouseClickType.SECONDARY;
            default -> MouseClickType.TERTIARY;
        };
    }
}

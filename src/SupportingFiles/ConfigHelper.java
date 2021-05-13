package SupportingFiles;

import SupportingFiles.Audio.Music;
import SupportingFiles.DataModels.ConfigModel;
import com.google.gson.Gson;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static app.PublicDefinitions.*;

public class ConfigHelper {

    public static Gson gson = new Gson();

    public static final String configFilePath = appDirectory + pathSeparator + "config.json";

    public static boolean createConfigFile() {
        if (Files.exists(Paths.get(configFilePath))) {
            return true;
        }

        try (FileWriter file = new FileWriter(configFilePath)) {
            file.write(DataEncoder.encodeConfig(new ConfigModel()));
            file.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Files.exists(Paths.get(configFilePath));
    }

    public static boolean writeConfigFile(ConfigModel configModel) {
        try (FileWriter file = new FileWriter(configFilePath)) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(DataEncoder.encodeConfig(configModel));
            file.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ConfigModel readConfig() {
        try (FileReader reader = new FileReader(configFilePath)) {
            return gson.fromJson(reader,ConfigModel.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new ConfigModel();
        }
    }

    public static boolean isMusicEnabled() {
        return readConfig().isMusicEnabled;
    }

    public static void setMusicEnabled(boolean enabled) {
        ConfigModel configModel = new ConfigModel();
        configModel = readConfig();
        configModel.isMusicEnabled = enabled;
        writeConfigFile(configModel);
    }

    public static boolean isSoundEffectsEnabled() {
        return readConfig().isSoundEffectsEnabled;
    }

    public static void setSoundEffectsEnabled(boolean enabled) {
        ConfigModel configModel = new ConfigModel();
        configModel = readConfig();
        configModel.isSoundEffectsEnabled = enabled;
        writeConfigFile(configModel);
    }

    public static int getAppearanceSettings() {
        return readConfig().appearance;
    }

    public static void setAppearance(int appearance) {
        ConfigModel configModel = new ConfigModel();
        configModel = readConfig();
        configModel.appearance = appearance;
        writeConfigFile(configModel);

        for (Window window : Window.getWindows()) {
            setupInterfaceStyle(window.getScene().getRoot());
        }

    }

    public static boolean isQuestionMarksEnabled() {
        return readConfig().enableQuestionMarks;
    }

    public static void setQuestionMarksEnabled(boolean enabled) {
        ConfigModel configModel = new ConfigModel();
        configModel = readConfig();
        configModel.enableQuestionMarks = enabled;
        writeConfigFile(configModel);
    }

    public static boolean isChordEnabled() {
        return readConfig().enableChord;
    }

    public static void setChordEnabled(boolean enabled) {
        ConfigModel configModel = new ConfigModel();
        configModel = readConfig();
        configModel.enableChord = enabled;
        writeConfigFile(configModel);
    }

    public static boolean isOpenAllSquaresSurroundingZeroEnabled() {
        return readConfig().openAllSquaresSurroundingZero;
    }

    public static void setOpenAllSquaresSurroundingZeroEnabled(boolean enabled) {
        ConfigModel configModel = new ConfigModel();
        configModel = readConfig();
        configModel.openAllSquaresSurroundingZero = enabled;
        writeConfigFile(configModel);
    }

    public static int getMusicVolume() {
        return readConfig().musicVolume;
    }

    public static void setMusicVolume(int volume) {
        ConfigModel configModel = new ConfigModel();
        configModel = readConfig();
        configModel.musicVolume = volume;
        writeConfigFile(configModel);
        music.setVolume(volume);
    }

    public static int getSoundEffectsVolume() {
        return readConfig().soundEffectsVolume;
    }

    public static void setSoundEffectsVolume(int volume) {
        ConfigModel configModel = new ConfigModel();
        configModel = readConfig();
        configModel.soundEffectsVolume = volume;
        writeConfigFile(configModel);
    }

}

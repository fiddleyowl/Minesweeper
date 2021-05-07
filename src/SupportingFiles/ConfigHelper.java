package SupportingFiles;

import SupportingFiles.DataModels.ConfigModel;
import com.google.gson.Gson;

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

}

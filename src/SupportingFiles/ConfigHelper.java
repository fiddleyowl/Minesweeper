package SupportingFiles;

import SupportingFiles.DataModels.ConfigModel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static Extensions.Misc.Print.*;
import static app.PublicDefinitions.*;

public class ConfigHelper {

    public static final JSONParser jsonParser = new JSONParser();
    public static final String configFilePath = appDirectory + pathSeparator + "config.json";

    public static boolean createConfigFile() {
        if (Files.exists(Paths.get(configFilePath))) {
            return true;
        }
        JSONObject config = new JSONObject();
        config.put("isMusicEnabled",true);
        config.put("isSoundEffectsEnabled",true);
        try (FileWriter file = new FileWriter(configFilePath)) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(config.toJSONString());
            file.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Files.exists(Paths.get(configFilePath));
    }

    public static boolean writeConfigFile(ConfigModel configModel) {
        JSONObject config = new JSONObject();
        config.put("isMusicEnabled",configModel.isMusicEnabled);
        config.put("isSoundEffectsEnabled",configModel.isSoundEffectsEnabled);
        try (FileWriter file = new FileWriter(configFilePath)) {
            //We can write any JSONArray or JSONObject instance to the file
            file.write(config.toJSONString());
            file.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ConfigModel readConfig() {
        try (FileReader reader = new FileReader(configFilePath)) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

//            JSONArray configArray = (JSONArray) obj;
//            JSONObject jsonObject = (JSONObject) configArray.get(0);
            JSONObject jsonObject = (JSONObject) obj;
            ConfigModel configModel = new ConfigModel();
            configModel.isMusicEnabled = (boolean) jsonObject.get("isMusicEnabled");
            configModel.isSoundEffectsEnabled = (boolean) jsonObject.get("isSoundEffectsEnabled");
            return configModel;
        } catch (Exception e) {
            e.printStackTrace();
            ConfigModel configModel = new ConfigModel();
            configModel.isMusicEnabled = true;
            configModel.isSoundEffectsEnabled = true;
            return configModel;
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

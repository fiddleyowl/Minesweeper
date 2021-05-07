package SupportingFiles;

import SupportingFiles.DataModels.ConfigModel;
import SupportingFiles.DataModels.GameModel;
import com.google.gson.Gson;

public class DataEncoder {

    static Gson gson = new Gson();

    public static String encodeGame(GameModel gameModel) {
        return gson.toJson(gameModel);
    }

    public static String encodeConfig(ConfigModel configModel) {
        return gson.toJson(configModel);
    }
}

package SupportingFiles;

import SupportingFiles.DataModels.GameModel;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;
import static app.PublicDefinitions.*;

public class GameEncoder {

    public static String encode(GameModel gameModel) {
        Gson gson = new Gson();
        return gson.toJson(gameModel);
    }
}

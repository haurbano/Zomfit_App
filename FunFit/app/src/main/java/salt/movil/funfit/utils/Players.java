package salt.movil.funfit.utils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import salt.movil.funfit.models.Player;

/**
 * Created by Hamilton Urbano on 2/7/2017.
 */

public class Players {
    private static Players instance;

    public static Players getInstance(){
        if (instance==null)
            instance = new Players();
        return instance;
    }

    public List<Player> parsePlayer(String playersString){
        List<Player> data = new ArrayList<>();
        try {
            JSONArray playersJson = new JSONArray(playersString);
            for (int i=0;i<playersJson.length();i++){
                Player p = new Player();
                p.setNumberKeys(playersJson.getJSONObject(i).getInt("keys"));
                p.setUsername(playersJson.getJSONObject(i).getString("name"));
                data.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
}

package salt.movil.funfit.utils;

import java.util.ArrayList;
import java.util.List;

import salt.movil.funfit.models.Player;

/**
 * Created by Hamilton Urbano on 2/7/2017.
 */

public class Players {

    public static Players instance;
    private static List<Player> players;

    public static Players getInstace(){
        if(instance==null){
            instance = new Players();
            players = new ArrayList<>();
        }

        return instance;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}

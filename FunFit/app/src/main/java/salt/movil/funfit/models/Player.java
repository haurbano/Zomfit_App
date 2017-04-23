package salt.movil.funfit.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hamilton Urbano on 06/11/2016.
 */

public class Player {

    String username;
    int time = 60;
    int numberKeys = 0;
    List<Integer> codesReaders;

    private static Player player;

    public static Player getInstance(){
        if (player==null){
            player = new Player();
        }
        return player;
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getNumberKeys() {
        return numberKeys;
    }

    public void setNumberKeys(int numberKeys) {
        this.numberKeys = numberKeys;
    }

    public void addKey(){
        if (numberKeys<3)
            numberKeys = numberKeys+1;
    }

    public List<Integer> getCodesReaders() {
        if (codesReaders==null)
            codesReaders = new ArrayList<>();
        return codesReaders;
    }

    public void addCodeReaded(int code){
        codesReaders.add(code);
    }
}

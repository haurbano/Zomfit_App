package salt.movil.funfit.models;

/**
 * Created by Hamilton Urbano on 06/11/2016.
 */

public class Player {

    private static Player player;

    public static Player getInstance(){
        if (player==null)
            player = new Player();
        return player;
    }

    String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

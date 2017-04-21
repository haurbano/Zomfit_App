package salt.movil.funfit.utils;

/**
 * Created by Hamilton Urbano on 01/11/2016.
 */

public class Constants {
    //event listen
    public static final int EVENT_START_GAME_PLAYERS = 10;
    public static final int EVENT_REDUCE_TIME_PLAYERS = 11;
    public static final int EVENT_REMOVE_KEY = 12;

    //events emit
    public static final String EMIT_REGISTER_PLAYER = "register_player";
    public static final String EMIT_REDUCE_TIME_PLAYERS = "reduce_time_players";
    public static final String EMIT_REMOVE_ENEMY_KEY = "remove_enemy_key";
}

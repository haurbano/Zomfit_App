package salt.movil.funfit.utils;

/**
 * Created by Hamilton Urbano on 01/11/2016.
 */

public class Constants {

    //Events listen
    public static final String EVENT_START_GAME_PLAYERS = "start_game_players";
    public static final String EVENT_REDUCE_TIME_PLAYERS = "reduce_time_players";
    public static final String EVENT_REMOVE_KEY = "romove_key";
    public static final String EVENT_END_GAME = "end_game";

    //Events listen for callback
    public static final int EVENT_START_GAME_PLAYERS_CB = 10;
    public static final int EVENT_REDUCE_TIME_PLAYERS_CB = 11;
    public static final int EVENT_REMOVE_KEY_CB = 12;
    public static final int EVENT_END_GAME_CB = 13;

    //Events send
    public static final String EMIT_WIN_GAME = "win_game";
    public static final String EMIT_REGISTER_PLAYER = "register_player";
    public static final String EMIT_REDUCE_TIME_PLAYERS = "reduce_time_players";
    public static final String EMIT_REMOVE_ENEMY_KEY = "remove_enemy_key";
}

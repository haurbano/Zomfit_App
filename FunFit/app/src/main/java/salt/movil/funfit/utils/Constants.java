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
    public static final String EVENT_PLAYER_LEAVE_GAME = "player_leave_game";
    public static final String EVENT_FOUND_ALL_KEYS = "found all keys";
    public static final String EVENT_ENEMY_FOUND_KEY = "enemy_found_key";

    //Events listen for callback
    public static final int EVENT_START_GAME_PLAYERS_CB = 10;
    public static final int EVENT_REDUCE_TIME_PLAYERS_CB = 11;
    public static final int EVENT_REMOVE_KEY_CB = 12;
    public static final int EVENT_END_GAME_CB = 13;
    public static final int EVENT_PLAYER_LEAVE_GAME_CB = 14;
    public static final int EVENT_FOUND_ALL_KEYS_CB = 15;
    public static final int EVENT_ENEMY_FOUND_KEY_CB = 16;

    //Events send
    public static final String EMIT_EXIT_PLAYER = "player_exit";
    public static final String EMIT_WIN_GAME = "win_game";
    public static final String EMIT_REGISTER_PLAYER = "register_player";
    public static final String EMIT_REDUCE_TIME_PLAYERS = "reduce_time_players";
    public static final String EMIT_REMOVE_ENEMY_KEY = "remove_enemy_key";
    public static final String EMIT_TEST_CONECTION = "test_connection";
    public static final String EMIT_FOUND_ALL_KEYS = "found all keys";
    public static final String EMIT_KEY_FOUND = "found_1_key";
    public static final String EMIT_GET_PLAYERS = "get_players";

    //Others
    public static final String SHARED_PREFERENCE_NAME = "shared preference salt";
    public static final String SHARED_IP_NAME = "ip of server";
}

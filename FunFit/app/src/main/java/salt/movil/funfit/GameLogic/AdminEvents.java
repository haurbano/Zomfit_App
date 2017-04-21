package salt.movil.funfit.GameLogic;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URISyntaxException;

import salt.movil.funfit.models.Player;
import salt.movil.funfit.net.MySocket;
import salt.movil.funfit.utils.Constants;
import salt.movil.funfit.utils.IsocketCallBacks;

/**
 * Created by Hamilton Urbano on 01/11/2016.
 */

public class AdminEvents {

    private Socket mSocket;
    private IsocketCallBacks isocketCallBacks;

    private boolean connectSucces;

    public AdminEvents() {
        try {
            mSocket = MySocket.getInstance();
            connectSucces = true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.e("salt1","No connect with socket, error: "+e.toString());
            connectSucces = false;
        }
    }


    public void listenEvents(IsocketCallBacks isocketCallBacks){
        if (!connectSucces){
            return;
        }
        this.isocketCallBacks = isocketCallBacks;
        mSocket.on("start_game_players",startGame);
        mSocket.on("reduce_time_players",reduceTime);
        mSocket.on("romove_key",removeKey);
    }

    //region Events
    private Emitter.Listener startGame = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.i("salt1","Llega al evento de starGame");
            isocketCallBacks.onEvent(Constants.EVENT_START_GAME_PLAYERS, args);
        }
    };

    private Emitter.Listener reduceTime = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JsonObject jo = new Gson().fromJson(args[0].toString(),JsonObject.class);
            if (jo.get("player").getAsString().equals(Player.getInstance().getUsername())){
                isocketCallBacks.onEvent(Constants.EVENT_REDUCE_TIME_PLAYERS,args);
            }

        }
    };

    private Emitter.Listener removeKey = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JsonObject jo = new Gson().fromJson(args[0].toString(),JsonObject.class);
            if (jo.get("player").getAsString().equals(Player.getInstance().getUsername())){
                isocketCallBacks.onEvent(Constants.EVENT_REMOVE_KEY,args);
            }
        }
    };
    //endregion
}

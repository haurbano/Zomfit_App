package salt.movil.funfit.net;

import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import salt.movil.funfit.utils.Constants;
import salt.movil.funfit.utils.IsocketCallBacks;

/**
 * Created by Hamilton Urbano on 01/11/2016.
 */

public class AdminEvents {

    Socket mSocket;
    IsocketCallBacks isocketCallBacks;

    boolean connectSucces;

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
            isocketCallBacks.onEvent(Constants.EVENT_REDUCE_TIME_PLAYERS,args);
        }
    };
    //endregion
}

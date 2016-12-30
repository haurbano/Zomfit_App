package salt.movil.funfit.GameLogic;

import android.util.Log;

import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import java.net.URISyntaxException;

import salt.movil.funfit.background.TimerUser;
import salt.movil.funfit.models.Player;
import salt.movil.funfit.net.MySocket;
import salt.movil.funfit.utils.Constants;

/**
 * Created by Hamilton Urbano on 26/12/2016.
 */

public class AdminResultQR {

    //region Contanst
    public static final int ADD_TIME_10 = 1;
    public static final int ADD_TIME_5 = 2;
    public static final int ADD_TIME_7 = 3;
    public static final int REDUCE_TIME_10 = 4;
    public static final int REDUCE_TIME_5 = 5;
    public static final int REDUCE_TIME_7 = 6;
    public static final int KEY1 = 7;
    public static final int KEY2 = 8;
    public static final int KEY3 = 9;
    public static final int READED_CODE = 10;

    //endregion

    public interface IResultQr{
        void setActionQr(int code, int value);
    }

    TimerUser timerUser;
    IResultQr iResultQr;

    public AdminResultQR(TimerUser timerUser, IResultQr iResultQr) {
        this.timerUser = timerUser;
        this.iResultQr = iResultQr;
    }

    public void handleResult(String result){
        int code = Integer.parseInt(result);
        if (Player.getInstance().getCodesReaders().contains(code)){
            iResultQr.setActionQr(READED_CODE,0);
        }else {
            aplyCode(code);
        }
    }

    private void aplyCode(int code){
        switch (code){
            case ADD_TIME_10:
                addTime(10);
                break;
            case ADD_TIME_5:
                addTime(5);
                break;
            case ADD_TIME_7:
                addTime(7);
                break;
            case REDUCE_TIME_10:
                reduceTime(10);
                break;
            case REDUCE_TIME_7:
                reduceTime(7);
                break;
            case REDUCE_TIME_5:
                reduceTime(5);
                break;
            case KEY1:
                addKey();
                break;
            case KEY2:
                addKey();
                break;
            case KEY3:
                addKey();
                break;
        }
        Player.getInstance().addCodeReader(code);
    }

    private void addTime(int time){
        timerUser.addTime(time);
        iResultQr.setActionQr(ADD_TIME_5,time);
    }

    private void reduceTime(int time){
        try {
            sendTimeToServer(time);
            iResultQr.setActionQr(REDUCE_TIME_5,time);
        } catch (URISyntaxException e) {
            Log.e("salt1","Error connection socket in send reduce time: "+e.toString());
        }
    }

    private void sendTimeToServer(int time) throws URISyntaxException {
        Socket socket = MySocket.getInstance();
        JsonObject jo = new JsonObject();
        jo.addProperty("player",Player.getInstance().getUsername());
        jo.addProperty("time",time);
        socket.emit(Constants.EMIT_REDUCE_TIME_PLAYERS,jo);
    }

    private void addKey(){
        Player.getInstance().addKey();
        iResultQr.setActionQr(KEY1,0);
    }
}

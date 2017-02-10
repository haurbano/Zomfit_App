package salt.movil.funfit.GameLogic;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import java.net.URISyntaxException;
import java.util.concurrent.ThreadLocalRandom;

import salt.movil.funfit.background.TimerUser;
import salt.movil.funfit.models.Player;
import salt.movil.funfit.net.MySocket;
import salt.movil.funfit.utils.Constants;

import static android.R.attr.breadCrumbShortTitle;
import static android.R.attr.max;

/**
 * Created by Hamilton Urbano on 26/12/2016.
 */

public class AdminResultQR {

    //region Contanst
    public static final int ADD_TIME = 1;
    public static final int REDUCE_TIME = 2;
    public static final int REMOVE_ENEMY_KEY = 3;
    public static final int READED_CODE = 4;
    public static final int KEY1 = 100;
    public static final int KEY2 = 101;
    public static final int KEY3 = 102;

    //endregion

    public interface IResultQr{
        void setActionQr(int code, int value);
    }

    IResultQr iResultQr;

    public AdminResultQR(IResultQr iResultQr) {
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
            case KEY1:
                addKey();
                break;
            case KEY2:
                addKey();
                break;
            case KEY3:
                addKey();
                break;
            default:
                RandomPower();
                break;
        }
        Player.getInstance().addCodeReader(code);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void RandomPower(){
        int minPower = 1;
        int maxPower = 3;
        int minTime = (int) Math.round(Player.getInstance().getTime() * 0.1);
        int maxTime = (int) Math.round(Player.getInstance().getTime() * 0.1);
        int power = ThreadLocalRandom.current().nextInt(minPower, maxPower + 1);
        int value = ThreadLocalRandom.current().nextInt(minTime, maxTime + 1);

        selectPower(power,value);
    }

    private void selectPower(int power, int value){
        switch (power){
            case ADD_TIME:
                addTime(value);
                break;
            case REDUCE_TIME:
                reduceTime(value);
                break;
            case REMOVE_ENEMY_KEY:
                removeEnemyKey();
                break;
        }
    }

    private void addTime(int time){
        iResultQr.setActionQr(ADD_TIME,time);
    }

    private void reduceTime(int time){
        iResultQr.setActionQr(REDUCE_TIME,time);
    }

    private void removeEnemyKey(){
        iResultQr.setActionQr(REMOVE_ENEMY_KEY,1);
    }

    private void addKey(){
        Player.getInstance().addKey();
        iResultQr.setActionQr(KEY1,1);
    }
}

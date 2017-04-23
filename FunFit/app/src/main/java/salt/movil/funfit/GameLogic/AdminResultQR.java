package salt.movil.funfit.GameLogic;

import android.annotation.TargetApi;
import android.os.Build;

import java.security.PublicKey;
import java.util.concurrent.ThreadLocalRandom;

import salt.movil.funfit.models.Player;

/**
 * Created by Hamilton Urbano on 26/12/2016.
 */

public class AdminResultQR {

    //region Contanst
    //Qr codes for power
    public static final int ADD_TIME = 1;
    public static final int REDUCE_TIME = 2;
    public static final int REDUCE_ENEMY_TIME = 3;
    public static final int REMOVE_ENEMY_KEY = 4;

    //Callback instrucctions
    public static final int READED_CODE = 5;
    public static final int MISS_KEYS_FOR_CURE = 6;
    public static final int GOT_CURE = 7;

    //Qr codes for keys and cure
    public static final int KEY1 = 100;
    public static final int KEY2 = 101;
    public static final int KEY3 = 102;
    public static final int CURE = 105;

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
            case CURE:
                getCure();
                break;
            default:
                RandomPower();
                break;
        }
        if(code!=CURE)
            Player.getInstance().addCodeReaded(code);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void RandomPower(){
        int minPower = 1;
        int maxPower = 4;
        int minTime = (int) Math.round(Player.getInstance().getTime() * 0.1);
        int maxTime = (int) Math.round(Player.getInstance().getTime() * 0.3);
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
            case REDUCE_ENEMY_TIME:
                reduceEnemyTime(value);
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

    private void reduceEnemyTime(int time){
        iResultQr.setActionQr(REDUCE_ENEMY_TIME,time);
    }

    private void removeEnemyKey(){
        iResultQr.setActionQr(REMOVE_ENEMY_KEY,1);
    }

    private void addKey(){
        Player.getInstance().addKey();
        iResultQr.setActionQr(KEY1,1);
    }

    private void getCure(){
        if (Player.getInstance().getNumberKeys() == 3){
            iResultQr.setActionQr(GOT_CURE,0);
        }else {
            int miss_keys = 3 - Player.getInstance().getNumberKeys();
            iResultQr.setActionQr(MISS_KEYS_FOR_CURE,miss_keys);
        }
    }
}

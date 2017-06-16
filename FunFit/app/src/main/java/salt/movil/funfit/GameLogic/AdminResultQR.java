package salt.movil.funfit.GameLogic;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;

import java.util.Random;

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
    public static final int KEY0 = 99;
    public static final int KEY1 = 100;
    public static final int KEY2 = 101;
    public static final int KEY3 = 102;
    public static final int KEY4 = 103;
    public static final int KEY5 = 104;
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
        Log.i("haur:Adminresul","resul int: "+code);
        if (Player.getInstance().getCodesReaders().contains(code)){
            iResultQr.setActionQr(READED_CODE,0);
        }else {
            aplyCode(code);
        }
    }

    private void aplyCode(int code){
        Log.i("haur:AdminRes","Code aplyCode: "+code);
        switch (code){
            case KEY0:
                Log.i("haur:AdminRes","Case Key0 ");
                addKey();
                break;
            case KEY1:
                Log.i("haur:AdminRes","Case Key1 ");
                addKey();
                break;
            case KEY2:
                Log.i("haur:AdminRes","Case Key2 ");
                addKey();
                break;
            case KEY3:
                Log.i("haur:AdminRes","Case Key3 ");
                addKey();
                break;
            case KEY4:
                Log.i("haur:AdminRes","Case Key4 ");
                addKey();
                break;
            case KEY5:
                Log.i("haur:AdminRes","Case Key5 ");
                addKey();
                break;
            case CURE:
                Log.i("haur:AdminRes","Case Key6 ");
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
        int minTime = (int) Math.round(Player.getInstance().getTime() * 0.01);
        int maxTime = (int) Math.round(Player.getInstance().getTime() * 0.05);
        Random r = new Random();
        int power = r.nextInt(maxPower - minPower + 1) + minPower;
        int value = r.nextInt(maxTime - minTime + 1) + minTime;
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
        Log.i("haur:AdminResul","addKey method: #keys: "+Player.getInstance().getNumberKeys());
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

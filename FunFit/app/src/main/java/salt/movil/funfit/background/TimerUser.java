package salt.movil.funfit.background;

import android.os.Message;
import android.os.Handler;

import salt.movil.funfit.models.Player;


/**
 * Created by Hamilton Urbano on 19/11/2016.
 */

public class TimerUser extends Thread{

    public static final int TIME_USER_TAG = 0;

    Handler handler;

    boolean running;
    int time;

    public TimerUser(Handler handler){
        this.handler = handler;
        running = true;
        time = Player.getInstance().getTime();
    }

    @Override
    public void run() {
        while (running || time>-1){
            try {
                Thread.sleep(1100);
                time = time - 1;
                Message msg = handler.obtainMessage();
                msg.what = TIME_USER_TAG;
                msg.arg1 = time;

                handler.sendMessage(msg);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addTime(int timeAdd){
        time = timeAdd + time;
    }

    public void reduceTime(int timeReduce){
        time = time - timeReduce;
    }

    public void stopTimer(){
        running = false;
    }
}

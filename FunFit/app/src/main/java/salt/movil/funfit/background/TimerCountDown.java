package salt.movil.funfit.background;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Hamilton Urbano on 4/19/2017.
 */

public class TimerCountDown extends Thread {

    Handler handler;

    public  TimerCountDown(Handler handler){
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(3500);
            Message msg = handler.obtainMessage();
            msg.arg1 = 1;
            handler.sendMessage(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package salt.movil.funfit.ui;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import salt.movil.funfit.R;
import salt.movil.funfit.background.TimerUser;
import salt.movil.funfit.models.Player;
import salt.movil.funfit.net.AdminEvents;
import salt.movil.funfit.utils.Constants;
import salt.movil.funfit.utils.IsocketCallBacks;

public class MainActivity extends AppCompatActivity implements IsocketCallBacks {

    TextView txtTimePlayer;
    TimerUser timerUser;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TimerUser.TIME_USER_TAG){
                showTime(msg.arg1);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenMode();
        setContentView(R.layout.activity_main);

        getViews();
        initSocket();
    }

    //region FullScreenMode
    private void fullScreenMode() {
        //Colocar en modo full Screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Mantener la pantalla encendida
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
    //endregion

    //region GetViews
    private void getViews(){
        txtTimePlayer = (TextView) findViewById(R.id.time_player_main_activity);
    }
    //endregion

    //region Timer
    private void initTime(){
        if (timerUser==null){
            timerUser  = new TimerUser(handler);
            timerUser.start();
        }
    }

    private void addTime(int timeAdd){

    }

    private void reduceTime(int timeReduce){
        if (timerUser!=null){
            timerUser.reduceTime(timeReduce);
        }
    }

    private void showTime(int time){
        if (time>0){
            txtTimePlayer.setText(" "+time);
        }else if (timerUser!=null){
            timerUser.stopTimer();
            Toast.makeText(this,"Perdio",Toast.LENGTH_SHORT).show();
        }
    }

    //endregion

    //region OnResume , OnDestroy

    @Override
    protected void onResume() {
        super.onResume();
        initTime();
    }

    @Override
    protected void onDestroy() {
        if (timerUser!=null)
            timerUser.stopTimer();
        timerUser = null;
        super.onDestroy();
    }

    //endregion

    //region Socket
    private void initSocket(){
        AdminEvents adminEvents = new AdminEvents();
        adminEvents.listenEvents(this);
    }

    @Override
    public void onEvent(int type, Object... args) {
        switch (type){
            case Constants.EVENT_REDUCE_TIME_PLAYERS:
                if (!args[0].equals(Player.getInstance().getUsername())){
                    reduceTime(Integer.parseInt(args[1].toString()));
                }
                break;
        }
    }
    //endregion

}

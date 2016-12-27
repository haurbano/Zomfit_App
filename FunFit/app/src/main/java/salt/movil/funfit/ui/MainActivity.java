package salt.movil.funfit.ui;

import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import salt.movil.funfit.GameLogic.AdminResultQR;
import salt.movil.funfit.R;
import salt.movil.funfit.background.TimerUser;
import salt.movil.funfit.databinding.ActivityMainBinding;
import salt.movil.funfit.models.Player;
import salt.movil.funfit.GameLogic.AdminEvents;
import salt.movil.funfit.ui.fragments.QRScannerFragment;
import salt.movil.funfit.utils.Constants;
import salt.movil.funfit.utils.IsocketCallBacks;

public class MainActivity extends AppCompatActivity implements IsocketCallBacks, QRScannerFragment.Ireader {

    //region Big views
    ActivityMainBinding binding;
    QRScannerFragment qrScannerFragment;
    //endregion

    //region Vars
    AdminResultQR adminResultQR;
    TimerUser timerUser;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TimerUser.TIME_USER_TAG){
                showTime(msg.arg1);
            }
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenMode();
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
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

    //region Timer
    private void initTime(){
        if (timerUser==null){
            timerUser  = new TimerUser(handler);
            timerUser.start();
        }
    }

    private void addTime(int timeAdd){
        timerUser.addTime(timeAdd);
    }

    private void reduceTime(int timeReduce){
        if (timerUser!=null){
            timerUser.reduceTime(timeReduce);
        }
    }

    private void showTime(int time){
        if (time>0){
            binding.contentTopOption.timePlayerMainActivity.setText(""+time);
        }else if (timerUser!=null){
            timerUser.stopTimer();
            Toast.makeText(this,"Perdio",Toast.LENGTH_SHORT).show();
        }
    }

    //endregion

    //region QR Reader
    private void initQRReader(){
        qrScannerFragment = new QRScannerFragment();
        qrScannerFragment.setInterface(this);
        changefragment(qrScannerFragment,R.id.frame_content_qr_reader);
        qrScannerFragment.startScann();
    }

    private void changefragment(Fragment fragment, int idLayout){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(idLayout,fragment);
        transaction.commit();
    }

    @Override
    public void setResult(String result) {
        if (adminResultQR == null)
            adminResultQR= new AdminResultQR();
        adminResultQR.handleResult(result,timerUser);
        initQRReader();
    }
    //endregion

    //region OnResume , OnDestroy
    @Override
    protected void onResume() {
        super.onResume();
        initTime();
        initQRReader();
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
        JsonObject jo = new Gson().fromJson(args[0].toString(),JsonObject.class);
        Log.i("salt1","Json que llega: "+jo);
        Log.i("salt1","Json que llega: "+args[0].toString());
        switch (type){
            case Constants.EVENT_REDUCE_TIME_PLAYERS:
                if (!jo.get("player").equals(Player.getInstance().getUsername())){
                    reduceTime(Integer.parseInt(jo.get("time").toString()));
                }
                break;
        }
    }
    //endregion

}

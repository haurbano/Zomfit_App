package salt.movil.funfit.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import salt.movil.funfit.GameLogic.AdminEvents;
import salt.movil.funfit.GameLogic.AdminResultQR;
import salt.movil.funfit.R;
import salt.movil.funfit.background.TimerUser;
import salt.movil.funfit.databinding.ActivityMainBinding;
import salt.movil.funfit.models.Player;
import salt.movil.funfit.models.Power;
import salt.movil.funfit.ui.adapters.PowerAdapter;
import salt.movil.funfit.ui.fragments.QRScannerFragment;
import salt.movil.funfit.utils.Constants;
import salt.movil.funfit.utils.IsocketCallBacks;
import salt.movil.funfit.utils.Players;
import salt.movil.funfit.utils.alerts.AlertGeneral;
import salt.movil.funfit.utils.alerts.AlertPlayers;

public class MainActivity extends AppCompatActivity implements IsocketCallBacks, QRScannerFragment.Ireader, AdminResultQR.IResultQr, AdapterView.OnItemClickListener {

    //region Big views
    ActivityMainBinding binding;
    QRScannerFragment qrScannerFragment;
    //endregion

    //region Vars
    AdminResultQR adminResultQR;
    TimerUser timerUser;
    List<Power> powers;

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
        initPowers();
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
            Intent intent = new Intent(this,GameOverActivity.class);
            startActivity(intent);
        }
    }

    //endregion

    //region QR Reader
    private void initQRReader(){
        qrScannerFragment = new QRScannerFragment();
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
            adminResultQR= new AdminResultQR(this);
        adminResultQR.handleResult(result);
        initQRReader();
    }

    @Override
    public void setActionQr(int code, int value) {
        switch (code){
            case AdminResultQR.KEY1:
                addKey();
                break;
            case AdminResultQR.ADD_TIME:
                timerUser.addTime(value);
                showAlert("Bien","Tienes más tiempo");
                break;
            case AdminResultQR.REDUCE_TIME:
                addPower(Power.REDUCE_TIME_ACCTION,value);
                break;
            case AdminResultQR.REMOVE_ENEMY_KEY:
                addPower(Power.REMOVE_ENEMY_KEY, value);
                break;
            case AdminResultQR.READED_CODE:
                showAlert("Ya lo leiste","Busca otro codigo");
                break;
        }
    }

    private void addKey(){
        int numberOfKeys = Player.getInstance().getNumberKeys();
        if (numberOfKeys == 3){
            showAlert("Bien","Tienes 3 llaves, corre a la cura");
        }else {
            binding.contentTopOption.txtNumberOfKeys.setText(numberOfKeys+"");
        }
    }

    private void showAlert(String title, String msj){
        AlertGeneral alert = AlertGeneral.newInstance(title,msj);
        alert.show(getSupportFragmentManager(),"tag");
    }

    private void addPower(String acction, int value){
        if (powers==null)
            powers = new ArrayList<>();
        Power power;
        if (acction.equals(Power.REDUCE_TIME_ACCTION))
            power = new Power(acction,value,R.drawable.ic_clock);
        else
            power = new Power(acction,value,R.drawable.ic_key);

        powers.add(power);
        showPowers(powers);
    }

    private void showPowers(List<Power> data){
        PowerAdapter adapter = new PowerAdapter(data,this);
        binding.contentPowersLayout.listViewPowers.setAdapter(adapter);
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
        switch (type){
            case Constants.EVENT_REDUCE_TIME_PLAYERS:
                    reduceTime(Integer.parseInt(jo.get("time").toString()));
                    showAlert(":(","You have less time by "+jo.get("sender").getAsString());
                break;
            case Constants.EVENT_REMOVE_KEY:
                removeKey(jo);
                break;
        }
    }
    //endregion

    //region  Powers
    private void initPowers(){
        binding.contentPowersLayout.listViewPowers.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Power currentPower = powers.get(position);
        powers.remove(position);
        showPowers(powers);

        AlertPlayers alertPlayers = new AlertPlayers();
        alertPlayers.setArguments(Players.getInstace().getPlayers(),currentPower);
        alertPlayers.show(getSupportFragmentManager(),"tag");
    }

    private void removeKey(JsonObject jo){
        final int numberKeys = Player.getInstance().getNumberKeys();
        if (numberKeys!=0){
            final String sender = jo.get("sender").getAsString();
            Player.getInstance().setNumberKeys(numberKeys-1);
            this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.contentTopOption.txtNumberOfKeys.setText((numberKeys-1)+"");
                    showAlert("Menos una llave","El enemigo "+sender+" te a quitado una llave");
                }
            });
        }
    }


    //endregion

}

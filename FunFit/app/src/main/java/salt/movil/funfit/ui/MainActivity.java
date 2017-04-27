package salt.movil.funfit.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import salt.movil.funfit.GameLogic.AdminEvents;
import salt.movil.funfit.GameLogic.AdminResultQR;
import salt.movil.funfit.R;
import salt.movil.funfit.background.TimerUser;
import salt.movil.funfit.databinding.ActivityMainBinding;
import salt.movil.funfit.models.Player;
import salt.movil.funfit.models.Power;
import salt.movil.funfit.net.MySocket;
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
    MediaPlayer mPLayer;

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
        mPLayer = MediaPlayer.create(this,R.raw.beep);
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
            if (time<11)
                soundPip();
            binding.timePlayer.setText(""+formatSeconds(time));
        }else if (timerUser!=null){
            timerUser.stopTimer();
            mPLayer.stop();
            Intent intent = new Intent(this,GameOverActivity.class);
            startActivity(intent);
        }
    }

    private void soundPip(){
        if (mPLayer.isPlaying())
            mPLayer.stop();
        mPLayer.start();
    }

    private String formatSeconds(int sec){
        long minutes = TimeUnit.SECONDS.toMinutes(sec);
        long seconds = TimeUnit.SECONDS.toSeconds(sec) - TimeUnit.MINUTES.toSeconds(minutes);
        String formato = "%02d:%02d";
        return String.format(formato, minutes, seconds);
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

    //Method for render accions on IU
    @Override
    public void setActionQr(int code, int value) {
        switch (code){
            case AdminResultQR.KEY1:
                addKey();
                break;

            case AdminResultQR.ADD_TIME:
                timerUser.addTime(value);
                showAlert("Bien","+ tiempo");
                break;
            case AdminResultQR.REDUCE_TIME:
                timerUser.reduceTime(value);
                showAlert(":(","- "+value+"segundos");
                break;

            case AdminResultQR.REDUCE_ENEMY_TIME:
                addPower(Power.REDUCE_TIME_ACCTION,value);
                break;

            case AdminResultQR.REMOVE_ENEMY_KEY:
                addPower(Power.REMOVE_ENEMY_KEY, value);
                break;

            case AdminResultQR.READED_CODE:
                break;

            case AdminResultQR.GOT_CURE:
                winGame();
                break;

            case AdminResultQR.MISS_KEYS_FOR_CURE:
                showAlert("Necesitas mas llaves","Te faltan "+value+" llaves");
                break;
        }
    }

    private void addKey(){
        int numberOfKeys = Player.getInstance().getNumberKeys();
        if (numberOfKeys == 3){
            showAlert("Bien","Tienes 3 llaves, corre a la cura");
            binding.contentPowersLayout.key3.setImageResource(R.drawable.ic_key);
        }else {
            showAlert("¡BIEN!","+ 1 llave");
            switch (numberOfKeys){
                case 1:
                    binding.contentPowersLayout.key1.setImageResource(R.drawable.ic_key);
                    break;
                case 2:
                    binding.contentPowersLayout.key2.setImageResource(R.drawable.ic_key);
                    break;
            }
        }
    }

    private void winGame(){
        try {
            Socket socket = MySocket.getInstance();
            JsonObject jo = new JsonObject();
            jo.addProperty("sender",Player.getInstance().getUsername());
            socket.emit(Constants.EMIT_WIN_GAME,jo);
        } catch (URISyntaxException e) {
            Log.e("MainActivity:winGame","Can't connect with cocket");
            e.printStackTrace();
        }
        Intent intent = new Intent(this,WinActivity.class);
        startActivity(intent);
    }

    private void showAlert(String title, String msj){
        AlertGeneral alert = AlertGeneral.newInstance(title,msj);
        alert.show(getSupportFragmentManager(),"tag");
    }

    //endregion

    //region Animations
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void animAddPower(Power power){
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int height = point.y;
        binding.imageForAnimatePower.setImageResource(power.getImage());
        binding.imageForAnimatePower.setVisibility(View.VISIBLE);
        binding.imageForAnimatePower.animate().setDuration(1500).y(10).x(height).scaleY(0.5f).scaleX(0.5f).withEndAction(runnable);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.imageForAnimatePower.setVisibility(View.INVISIBLE);
                }
            });
        }
    };

    private void animAplyPower(){

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
        exitPlayer();
        super.onDestroy();
    }

    private void exitPlayer() {
        try {
            Socket socket = MySocket.getInstance();
            JsonObject jo = new JsonObject();
            jo.addProperty("sender",Player.getInstance().getUsername());
            socket.emit(Constants.EMIT_EXIT_PLAYER,jo);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        showAlert("Salir?","Deseas salir");
    }

    //endregion

    //region Socket Listener
    private void initSocket(){
        AdminEvents adminEvents = new AdminEvents();
        adminEvents.listenEvents(this);
    }

    @Override
    public void onEvent(int type, Object... args) {
        JsonObject jo = new Gson().fromJson(args[0].toString(),JsonObject.class);
        switch (type){
            case Constants.EVENT_REDUCE_TIME_PLAYERS_CB:
                    reduceTime(Integer.parseInt(jo.get("time").toString()));
                    showAlert(":(","Tienes menos tiempo, culpa de: "+jo.get("sender").getAsString());
                break;
            case Constants.EVENT_REMOVE_KEY_CB:
                removeKey(jo);
                break;
            case Constants.EVENT_END_GAME_CB:
                Intent intent = new Intent(this,GameOverActivity.class);
                startActivity(intent);
                break;

            case Constants.EVENT_PLAYER_LEAVE_GAME_CB:
                showAlert("Uno Menos", jo.get("sender").toString()+" Avandyono el juego");
                break;
        }
    }
    //endregion

    //region  Powers
    private void initPowers(){
        binding.contentPowersLayout.listViewPowers.setOnItemClickListener(this);
        Power powerFake = new Power(Power.FAKE_POWER,0,R.drawable.ic_fake_power,true);
        if (powers == null)
            powers = new ArrayList<>();

        powers.add(powerFake);
        powers.add(powerFake);
        powers.add(powerFake);

        showPowers(powers);
    }

    private void addPower(String acction, int value){
        if (powers==null)
            powers = new ArrayList<>();
        if (powers.size()>0)
            if (powers.get(0).isFake())
                powers.clear();

        Power power;
        if (acction.equals(Power.REDUCE_TIME_ACCTION))
            power = new Power(acction,value,R.drawable.ic_clock_life_time, false);
        else
            power = new Power(acction,value,R.drawable.ic_key, false);

        animAddPower(power);
        powers.add(power);
        showPowers(powers);
    }

    private void showPowers(List<Power> data){
        PowerAdapter adapter = new PowerAdapter(data,this);
        binding.contentPowersLayout.listViewPowers.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Power currentPower = powers.get(position);

        if (currentPower.isFake())
            return;

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
            showAlert("Menos una llave", "Te la quito "+sender);
            switch (numberKeys){
                case 1:
                    binding.contentPowersLayout.key1.setImageResource(R.drawable.ic_key);
                    break;
                case 2:
                    binding.contentPowersLayout.key2.setImageResource(R.drawable.ic_key);
                    break;
                case 3:
                    binding.contentPowersLayout.key3.setImageResource(R.drawable.ic_key);
                    break;
            }
        }
    }

    //endregion

}

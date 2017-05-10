package salt.movil.funfit.ui;

import android.annotation.TargetApi;
import android.app.Activity;
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

import com.github.nkzawa.socketio.client.Ack;
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
import salt.movil.funfit.ui.alerts.AlertGeneral;
import salt.movil.funfit.ui.alerts.AlertPlayers;
import salt.movil.funfit.ui.alerts.AlertSettingMain;
import salt.movil.funfit.ui.fragments.QRScannerFragment;
import salt.movil.funfit.utils.Constants;
import salt.movil.funfit.utils.IsocketCallBacks;
import salt.movil.funfit.utils.Players;

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
        binding.setHandler(this);
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
            intent.putExtra("msj","Se te acabo el tiempo");
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
                showAlert("Bien","+ tiempo",R.mipmap.more_time);
                break;
            case AdminResultQR.REDUCE_TIME:
                timerUser.reduceTime(value);
                showAlert(":(","- "+value+"segundos",R.mipmap.less_time);
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
                showAlert("Necesitas mas llaves","Te faltan "+value+" llaves",R.drawable.ic_miss_keys);
                break;
        }
    }

    private void addKey(){
        int numberOfKeys = Player.getInstance().getNumberKeys();
        JsonObject jo = new JsonObject();
        jo.addProperty("sender",Player.getInstance().getUsername());
        jo.addProperty("keys",Player.getInstance().getNumberKeys());

        if (numberOfKeys == 3){
            showAlert("Bien","Tienes 3 llaves, corre a la cura",R.drawable.ic_three_keys);
            binding.contentPowersLayout.key3.setImageResource(R.drawable.ic_key);
            try {
                Socket socket = MySocket.getInstance(this);
                socket.emit(Constants.EMIT_FOUND_ALL_KEYS,jo);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        }else {
            showAlert("¡BIEN!","+ 1 llave",R.mipmap.one_key_more);
            try {
                Socket socket = MySocket.getInstance(this);
                socket.emit(Constants.EMIT_KEY_FOUND,jo);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
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
            Socket socket = MySocket.getInstance(this);
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

    private void showAlert(String title, String msj, int icon){
        AlertGeneral alert = AlertGeneral.newInstance(title,msj,icon);
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
            Socket socket = MySocket.getInstance(this);
            JsonObject jo = new JsonObject();
            jo.addProperty("sender",Player.getInstance().getUsername());
            socket.emit(Constants.EMIT_EXIT_PLAYER,jo);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        showAlert("Salir?","Deseas salir",R.mipmap.first_activity_zombie);
    }

    //endregion

    //region Socket Listener
    private void initSocket(){
        AdminEvents adminEvents = new AdminEvents(this);
        adminEvents.listenEvents(this);
    }

    @Override
    public void onEvent(int type, Object... args) {
        Log.i("haur","onEvent: "+type + " "+args.toString() );
        JsonObject jo = new Gson().fromJson(args[0].toString(),JsonObject.class);
        String sender = jo.get("sender").getAsString();
        switch (type){
            case Constants.EVENT_REDUCE_TIME_PLAYERS_CB:
                reduceTime(Integer.parseInt(jo.get("time").toString()));
                showAlert(":(","Tienes menos tiempo, culpa de: "+sender,R.mipmap.less_time);
                break;
            case Constants.EVENT_REMOVE_KEY_CB:
                removeKey(jo);
                break;
            case Constants.EVENT_END_GAME_CB:
                Intent intent = new Intent(this,GameOverActivity.class);
                String msj = sender +" Gano!";
                intent.putExtra("msj",msj);
                startActivity(intent);
                break;
            case Constants.EVENT_PLAYER_LEAVE_GAME_CB:
                showAlert("Uno Menos", sender+" Avandono el juego",R.drawable.ic_zombie_less);
                break;
            case Constants.EVENT_FOUND_ALL_KEYS_CB:
                showAlert("Alguien ganará",sender+" tiene 3 llaves",R.drawable.ic_someone_have_keys);
                break;
            case Constants.EVENT_ENEMY_FOUND_KEY_CB:
                showAlert("Enemigo encontro 1 llave",sender+" encontro 1 llave",R.mipmap.first_activity_zombie);
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

        if (powers.size()<3){
            Power power;
            if (acction.equals(Power.REDUCE_TIME_ACCTION))
                power = new Power(acction,value,R.drawable.ic_clock_life_time, false);
            else
                power = new Power(acction,value,R.drawable.ic_key, false);

            animAddPower(power);
            powers.add(power);
            showPowers(powers);
        }

    }

    private void showPowers(List<Power> data){
        PowerAdapter adapter = new PowerAdapter(data,this);
        binding.contentPowersLayout.listViewPowers.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        final Power currentPower = powers.get(position);

        if (currentPower.isFake())
            return;

        powers.remove(position);
        showPowers(powers);

        try {
            Socket socket = MySocket.getInstance((Activity) view.getContext());
            JsonObject jo = new JsonObject();
            jo.addProperty("sender",Player.getInstance().getUsername());
            socket.emit(Constants.EMIT_GET_PLAYERS, jo, new Ack() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showPlayersAlert(currentPower,args[0].toString());
                        }
                    });
                }
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


    }

    private void showPlayersAlert(Power currentPower, String playersString){
        List<Player> players = Players.getInstance().parsePlayer(playersString);
        AlertPlayers alertPlayers = new AlertPlayers();
        alertPlayers.setArguments(players,currentPower);
        alertPlayers.show(getSupportFragmentManager(),"tag");
    }

    private void removeKey(final JsonObject jo){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int numberKeys = Player.getInstance().getNumberKeys();
                if (numberKeys!=0){
                    final String sender = jo.get("sender").getAsString();
                    Player.getInstance().setNumberKeys(numberKeys-1);
                    showAlert("Menos una llave", "Te la quito "+sender,R.drawable.ic_key_less);
                    switch (numberKeys){
                        case 1:
                            binding.contentPowersLayout.key1.setImageResource(R.drawable.ic_grey_key);
                            break;
                        case 2:
                            binding.contentPowersLayout.key2.setImageResource(R.drawable.ic_grey_key);
                            break;
                        case 3:
                            binding.contentPowersLayout.key3.setImageResource(R.drawable.ic_grey_key);
                            break;
                    }
                }
            }
        });
    }

    //endregion

    //region Settings
    public void showAlertSettings(){
        AlertSettingMain alert = AlertSettingMain.newInstance();
        alert.show(getFragmentManager(),"tag");
    }
    //endregion

}

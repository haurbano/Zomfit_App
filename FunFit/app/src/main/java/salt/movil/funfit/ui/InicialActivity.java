package salt.movil.funfit.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;

import pl.droidsonroids.gif.GifDrawable;
import salt.movil.funfit.GameLogic.AdminEvents;
import salt.movil.funfit.R;
import salt.movil.funfit.background.TimerCountDown;
import salt.movil.funfit.models.Player;
import salt.movil.funfit.net.MySocket;
import salt.movil.funfit.utils.Constants;
import salt.movil.funfit.utils.IsocketCallBacks;
import salt.movil.funfit.utils.Players;

public class InicialActivity extends AppCompatActivity implements IsocketCallBacks, View.OnClickListener {

    //region Views
    EditText edtUsername;
    Button btnContinue;
    TextView txtRegistered;
    //endregion

    //region Vars
    Socket socket;
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenMode();
        setContentView(R.layout.activity_inicial);
        initSockets();
        getViews();
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

    //region getViews
    private void getViews(){
        edtUsername = (EditText) findViewById(R.id.edit_username_inicial_activity);
        btnContinue = (Button) findViewById(R.id.btn_continue_inicial_activity);
        txtRegistered = (TextView) findViewById(R.id.txt_registered_user_initial_activity);

        Typeface wordType = Typeface.createFromAsset(getAssets(),"fonts/zombie_font.ttf");
        txtRegistered.setTypeface(wordType);
        edtUsername.setTypeface(wordType);
        btnContinue.setTypeface(wordType);

        btnContinue.setOnClickListener(this);
    }
    //endregion

    //region Sockets
    private void initSockets(){
        AdminEvents adminEvents = new AdminEvents();
        adminEvents.listenEvents(this);
    }

    @Override
    public void onEvent(int type, Object... args) {
        switch (type){
            case Constants.EVENT_START_GAME_PLAYERS_CB:
                startGame(args);
                break;
        }
    }
    //endregion

    //region Actions game
    private void startGame(Object... args){
        int time = Integer.parseInt((String) args[1]);
        Player.getInstance().setTime(time);
        try {
            JSONArray players = new JSONArray(args[0].toString());
            for (int i=0; i<players.length();i++){
                Player player = new Player();
                player.setNumberKeys(players.getJSONObject(i).getInt("keys"));
                player.setUsername(players.getJSONObject(i).getString("name"));
                Players.getInstace().getPlayers().add(player);
            }
            //Init countdown
            showCountdown();
        } catch (JSONException e) {
            Log.e("InitialActivity","Error whe try convert String to JSONArray");
            e.printStackTrace();
        }
    }

    private void showCountdown(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final ImageView imageView = new ImageView(this);
        imageView.setBackgroundColor(getResources().getColor(R.color.transparent));

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    GifDrawable gif = new GifDrawable(getResources(),R.mipmap.countdown);
                    imageView.setImageDrawable(gif);
                    builder.setView(imageView);
                    builder.create().show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        TimerCountDown timerCountDown = new TimerCountDown(handler);
        timerCountDown.start();
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            navigateToGame();
        }
    };

    private void navigateToGame(){
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    private void registerPlayer(){
        String username = edtUsername.getText().toString();
        if (!username.isEmpty()){
            Player.getInstance().setUsername(username);
            try {
                socket = MySocket.getInstance();
                socket.emit(Constants.EMIT_REGISTER_PLAYER, username, new Ack() {
                    @Override
                    public void call(Object... args) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btnContinue.setEnabled(false);
                                btnContinue.setText("Registrado");
                                txtRegistered.setText("Registrado, espera a que inicie la partida");
                            }
                        });

                    }
                });
            } catch (URISyntaxException e) {
                Log.e("salt1","No connect socket, when register player, error: "+e.toString());
                e.printStackTrace();
            }
        }else {
            Log.i("salt1","Nombre empty");
        }
    }
    //endregion

    //region OnClik and onDestroy
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_continue_inicial_activity:
                registerPlayer();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (socket!=null && socket.connected())
            socket.disconnect();
        super.onDestroy();
    }
    //endregion
}

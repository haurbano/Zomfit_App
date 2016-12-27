package salt.movil.funfit.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import salt.movil.funfit.R;
import salt.movil.funfit.models.Player;
import salt.movil.funfit.GameLogic.AdminEvents;
import salt.movil.funfit.net.MySocket;
import salt.movil.funfit.utils.Constants;
import salt.movil.funfit.utils.IsocketCallBacks;

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
            case Constants.EVENT_START_GAME_PLAYERS:
                startGame(args);
                break;
        }
    }
    //endregion

    //region Actions game
    private void startGame(Object... args){
        Intent intent = new Intent(this,MainActivity.class);
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
                                txtRegistered.setVisibility(View.VISIBLE);
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

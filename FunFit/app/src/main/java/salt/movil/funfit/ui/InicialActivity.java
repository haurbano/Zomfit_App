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

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import salt.movil.funfit.R;
import salt.movil.funfit.models.Player;
import salt.movil.funfit.net.AdminEvents;
import salt.movil.funfit.net.MySocket;
import salt.movil.funfit.utils.Constants;
import salt.movil.funfit.utils.IsocketCallBacks;

public class InicialActivity extends AppCompatActivity implements IsocketCallBacks, View.OnClickListener {

    //region Views
    EditText edtUsername;
    Button btnContinue;
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
        Log.i("salt1","on event: "+type);
        switch (type){
            case Constants.EVENT_START_GAME_PLAYERS:
                startGame(args);
                break;
        }
    }
    //endregion

    //region Actions game
    private void startGame(Object... args){
        Log.i("salt1","Inicio juego: "+args.toString());
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
    //endregion

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_continue_inicial_activity:
                registerPlayer();
                break;
        }
    }

    private void registerPlayer(){
        String username = edtUsername.getText().toString();
        Player.getInstance().setUsername(username);

        try {
            socket = MySocket.getInstance();
            socket.emit(Constants.EMIT_REGISTER_PLAYER,username);
            Log.i("salt1","Socket enviado de registro");
        } catch (URISyntaxException e) {
            Log.e("salt1","No connect socket, when register player, error: "+e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        socket.disconnect();
        super.onStop();
    }
}

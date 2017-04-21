package salt.movil.funfit.utils.alerts;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;

import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.JsonObject;

import java.net.URISyntaxException;
import java.util.List;

import salt.movil.funfit.R;
import salt.movil.funfit.databinding.LayoutAlertPlayersBinding;
import salt.movil.funfit.models.Player;
import salt.movil.funfit.models.Power;
import salt.movil.funfit.net.MySocket;
import salt.movil.funfit.ui.adapters.PlayerAdapter;
import salt.movil.funfit.utils.Constants;

/**
 * Created by Hamilton Urbano on 2/7/2017.
 */

public class AlertPlayers extends DialogFragment implements AdapterView.OnItemClickListener {

    List<Player> players;
    Power power;
    AlertDialog alertDialog;

    public void setArguments(List<Player> players, Power power ) {
        this.players = players;
        this.power = power;

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutAlertPlayersBinding binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),R.layout.layout_alert_players,null,false);

        PlayerAdapter adapter = new PlayerAdapter(players,getContext());
        binding.listPlayers.setAdapter(adapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot());

        binding.listPlayers.setOnItemClickListener(this);
        alertDialog = builder.create();

        return alertDialog;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Player player = players.get(position);
        aplyAction(player);
    }

    private void aplyAction(Player player){

        switch (power.getAccion()){
            case Power.REDUCE_TIME_ACCTION:
                sendReduceTimeToServer(power.getValue(), player);
                break;
            case Power.REMOVE_ENEMY_KEY:
                removeEnemyKeyToServer(player);
                break;
        }

    }

    private void sendReduceTimeToServer(int time, Player player) {
        Socket socket = null;
        try {
            socket = MySocket.getInstance();
            JsonObject jo = new JsonObject();
            jo.addProperty("player",player.getUsername());
            jo.addProperty("time",time);
            jo.addProperty("sender",Player.getInstance().getUsername());
            socket.emit(Constants.EMIT_REDUCE_TIME_PLAYERS,jo);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        alertDialog.hide();
    }

    private void removeEnemyKeyToServer(Player player){
        Socket socket = null;
        try {
            socket = MySocket.getInstance();
            JsonObject jo = new JsonObject();
            jo.addProperty("player",player.getUsername());
            jo.addProperty("sender",Player.getInstance().getUsername());
            socket.emit(Constants.EMIT_REMOVE_ENEMY_KEY,jo);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        alertDialog.hide();
    }
}

package salt.movil.funfit.ui.alerts;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import salt.movil.funfit.R;
import salt.movil.funfit.net.MySocket;
import salt.movil.funfit.utils.Constants;

/**
 * Created by haurbano on 29/04/2017.
 */

public class AlertConfigIp extends DialogFragment {

    EditText editText;
    TextView txtInfo;

    public static AlertConfigIp newInstanse(String ip){
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SHARED_IP_NAME,ip);

        AlertConfigIp alert = new AlertConfigIp();
        alert.setArguments(bundle);
        return alert;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.layout_alert_config_ip,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        editText = (EditText) view.findViewById(R.id.edt_config_ip);
        txtInfo = (TextView) view.findViewById(R.id.txt_info_config_ip);

        String ip = getArguments().getString(Constants.SHARED_IP_NAME);
        editText.setHint(ip);

        builder.setPositiveButton("Cambiar IP",positiveButton);
        builder.setNegativeButton("Cerrar",null);
        return builder.create();
    }

    DialogInterface.OnClickListener positiveButton = new DialogInterface.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            txtInfo.setText("Esperando respuesta...");
            SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,Context.MODE_PRIVATE).edit();
            editor.putString(Constants.SHARED_IP_NAME,editText.getText().toString());
            editor.apply();
            try {
                Socket socket = MySocket.getInstance(getActivity());
                socket.emit(Constants.EMIT_TEST_CONECTION, "test conection", new Ack() {
                    @Override
                    public void call(Object... args) {
                        txtInfo.setText("Conectado");
                    }
                });
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    };

}

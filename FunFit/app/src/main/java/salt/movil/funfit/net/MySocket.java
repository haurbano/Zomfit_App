package salt.movil.funfit.net;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import salt.movil.funfit.utils.Constants;

/**
 * Created by Hamilton Urbano on 01/11/2016.
 */

public class MySocket {
    private static Socket instance;

    public static Socket getInstance(Activity activity) throws URISyntaxException {

        if (instance==null){
            SharedPreferences preferences = activity.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME,Context.MODE_PRIVATE);
            String ip = preferences.getString(Constants.SHARED_IP_NAME,"192.168.1.2");
            instance = IO.socket("http://"+ip+":3000/");
        }

        if (!instance.connected())
            instance.connect();

        return instance;
    }


}

package salt.movil.funfit.net;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

/**
 * Created by Hamilton Urbano on 01/11/2016.
 */

public class MySocket {
    private static Socket instance;

    public static Socket getInstance() throws URISyntaxException {
        if (instance==null)
            instance = IO.socket("http://192.168.0.14:3000/");
        if (!instance.connected())
            instance.connect();

        return instance;
    }


}

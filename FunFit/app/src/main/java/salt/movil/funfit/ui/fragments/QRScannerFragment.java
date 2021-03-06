package salt.movil.funfit.ui.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Fragment QR reader
 */
public class QRScannerFragment extends Fragment implements ZXingScannerView.ResultHandler {

    ZXingScannerView scannerView;
    Ireader ireader;

    public QRScannerFragment() {
        // Required empty public constructor
    }

    public interface Ireader{
        void setResult(String result);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ireader = (Ireader) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        scannerView = new ZXingScannerView((Context) ireader);
        scannerView.setResultHandler(this);
        if (scannerView!=null)
            scannerView.startCamera();
        else
            Log.i("haur","Scannview null");
        return scannerView;
    }




    @Override
    public void handleResult(Result result) {
        Log.i("haur:QRScanner","result: "+result.getText());
        ireader.setResult(result.getText());
    }

    public void startScann(){
        if (scannerView!=null)
            scannerView.startCamera();
        else
            Log.i("haur","Scannview null");
    }

    @Override
    public void onDestroy() {
        if (scannerView!=null)
            scannerView.stopCamera();
        super.onDestroy();
    }
}

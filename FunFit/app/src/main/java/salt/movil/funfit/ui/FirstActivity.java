package salt.movil.funfit.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;


import salt.movil.funfit.R;

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btnCredits, btnPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreenMode();
        setContentView(R.layout.activity_first);

        btnCredits = (ImageButton) findViewById(R.id.btn_credits_first_activity);
        btnPlay = (ImageButton) findViewById(R.id.btn_play_first_activity);

        btnPlay.setOnClickListener(this);
        btnCredits.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermisions();
    }

    private void requestPermisions(){
        int permisos = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permisos == PackageManager.PERMISSION_DENIED){
           boolean cantRequestPermission =  ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA);
            if (!cantRequestPermission){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 101:
                if (grantResults.length > 0){
                    Log.d("FirstActivity:onRequest","permisio camera ok");
                }
                break;
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play_first_activity:
                Intent intent = new Intent(this, InicialActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_credits_first_activity:
                Intent intent1 = new Intent(this,CreditsActivity.class);
                startActivity(intent1);
                break;
        }
    }
}

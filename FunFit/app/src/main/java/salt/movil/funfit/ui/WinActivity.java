package salt.movil.funfit.ui;

import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import salt.movil.funfit.R;
import salt.movil.funfit.databinding.ActivityWinBinding;
import salt.movil.funfit.databinding.LayoutAlertGeneralBinding;

public class WinActivity extends AppCompatActivity {

    ActivityWinBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fullScreenMode();
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_win);

        setFonts();
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

    private void setFonts(){
        Typeface wordType = Typeface.createFromAsset(getAssets(),"fonts/zombie_font.ttf");
        binding.txtGanasteWinActivity.setTypeface(wordType);
    }
}

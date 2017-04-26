package salt.movil.funfit.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import salt.movil.funfit.R;
import salt.movil.funfit.databinding.ActivityGameOverBinding;
import salt.movil.funfit.databinding.LayoutAlertGeneralBinding;
import salt.movil.funfit.models.Player;

public class GameOverActivity extends AppCompatActivity {

    ActivityGameOverBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_game_over);
        binding.setPlayer(Player.getInstance());

        setFonts();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startActivity(startMain);
    }

    private void setFonts(){
        Typeface wordType = Typeface.createFromAsset(getAssets(),"fonts/fonty.ttf");
        binding.txtNameUserGameOver.setTypeface(wordType);
        binding.txtGameOver.setTypeface(wordType);
    }
}

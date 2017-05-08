package salt.movil.funfit.ui.alerts;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import salt.movil.funfit.R;
import salt.movil.funfit.databinding.LayoutSettingMainActivityBinding;

/**
 * Created by haurbano on 30/04/2017.
 */

public class AlertSettingMain extends DialogFragment{

    LayoutSettingMainActivityBinding binding;

    public static AlertSettingMain newInstance(){
        AlertSettingMain fragment = new AlertSettingMain();
        return fragment;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),R.layout.layout_setting_main_activity,null,false);
        binding.setHandler(this);

        Typeface wordType = Typeface.createFromAsset(getActivity().getAssets(),"fonts/zombie_font.ttf");

        builder.setView(binding.getRoot());

        binding.txtSoundAlertSettingsMain.setTypeface(wordType);
        binding.txtSalirAlertSettingMain.setTypeface(wordType);
        binding.txtVolverSettingAlertsMain.setTypeface(wordType);

        return builder.create();
    }

    public void changeSound(){
        if (binding.txtSoundAlertSettingsMain.getText() == "On"){
            binding.txtSoundAlertSettingsMain.setText("Off");
            binding.imgSoundMainActivity.setImageResource(R.drawable.ic_sound_off);
            binding.txtSoundAlertSettingsMain.setTextColor(getResources().getColor(R.color.red));
        }else {
            binding.txtSoundAlertSettingsMain.setText("On");
            binding.imgSoundMainActivity.setImageResource(R.drawable.ic_sound_on);
            binding.txtSoundAlertSettingsMain.setTextColor(getResources().getColor(R.color.green));
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void exitGame(){
        getActivity().finishAffinity();
    }

    public void backGame(){
        dismiss();
    }
}

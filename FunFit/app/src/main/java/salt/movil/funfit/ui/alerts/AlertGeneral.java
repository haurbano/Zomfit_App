package salt.movil.funfit.ui.alerts;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import salt.movil.funfit.R;
import salt.movil.funfit.databinding.LayoutAlertGeneralBinding;

/**
 * Created by Hamilton Urbano on 27/12/2016.
 */

public class AlertGeneral extends DialogFragment{

    public static AlertGeneral newInstance(String title, String mesagge, int icon) {
        Bundle args = new Bundle();
        args.putString("title",title);
        args.putString("message",mesagge);
        args.putInt("icon",icon);

        AlertGeneral fragment = new AlertGeneral();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String mesagge = getArguments().getString("message");
        int icon = getArguments().getInt("icon");

        LayoutAlertGeneralBinding binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),R.layout.layout_alert_general,null,false);
        binding.setHandler(this);
        binding.txtTitleAlertGeneral.setText(title);
        binding.txtMessageAlertGeneral.setText(mesagge);
        binding.imageView.setImageResource(icon);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot());
        setFonts(binding);
        return builder.create();
    }

    private void setFonts(LayoutAlertGeneralBinding binding){
        Typeface wordType = Typeface.createFromAsset(getActivity().getAssets(),"fonts/zombie_font.ttf");
        binding.txtTitleAlertGeneral.setTypeface(wordType);
        binding.txtMessageAlertGeneral.setTypeface(wordType);
    }

    public void btnContinue(){
        dismiss();
    }

}

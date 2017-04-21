package salt.movil.funfit.utils.alerts;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
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

    public static AlertGeneral newInstance(String title, String mesagge) {
        Bundle args = new Bundle();
        args.putString("title",title);
        args.putString("message",mesagge);

        AlertGeneral fragment = new AlertGeneral();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String mesagge = getArguments().getString("message");

        LayoutAlertGeneralBinding binding = DataBindingUtil.inflate(getActivity().getLayoutInflater(),R.layout.layout_alert_general,null,false);
        binding.txtTitleAlertGeneral.setText(title);
        binding.txtMessageAlertGeneral.setText(mesagge);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(binding.getRoot());

        return builder.create();
    }

}

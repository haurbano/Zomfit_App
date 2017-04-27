package salt.movil.funfit.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;
import java.util.zip.Inflater;

import salt.movil.funfit.R;
import salt.movil.funfit.databinding.PowerAdapterLayoutBinding;
import salt.movil.funfit.models.Power;

/**
 * Created by Hamilton Urbano on 2/4/2017.
 */

public class PowerAdapter extends BaseAdapter {
    List<Power> data;
    Context context;

    public PowerAdapter(List<Power> data, Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PowerAdapterLayoutBinding binding;
        if (convertView!=null)
            binding = (PowerAdapterLayoutBinding) convertView.getTag();
        else {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.power_adapter_layout, null, false);
            convertView = binding.getRoot();
        }

        Power power = data.get(position);
        binding.imagePowerAdapter.setImageResource(power.getImage());
        binding.valuePowerAdapter.setText(power.getValue()+"");
        binding.txtNamePowerAdapter.setText(power.getAccion());

        Typeface wordType = Typeface.createFromAsset(context.getAssets(),"fonts/zombie_font_names.ttf");

        binding.txtNamePowerAdapter.setTypeface(wordType);
        binding.valuePowerAdapter.setTypeface(wordType);

        if (power.isFake()){
            binding.txtNamePowerAdapter.setTextColor(ContextCompat.getColor(context,R.color.colorGray));
            binding.valuePowerAdapter.setTextColor(ContextCompat.getColor(context,R.color.colorGray));
        }else{
            binding.txtNamePowerAdapter.setTextColor(ContextCompat.getColor(context,R.color.colorPrimaryDark));
            binding.valuePowerAdapter.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
        }

        convertView.setTag(binding);
        return convertView;
    }
}

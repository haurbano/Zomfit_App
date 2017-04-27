package salt.movil.funfit.ui.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import salt.movil.funfit.R;
import salt.movil.funfit.databinding.PlayerAdapterLayoutBinding;
import salt.movil.funfit.databinding.PowerAdapterLayoutBinding;
import salt.movil.funfit.models.Player;
import salt.movil.funfit.models.Power;

/**
 * Created by Hamilton Urbano on 2/7/2017.
 */

public class PlayerAdapter extends BaseAdapter {
    List<Player> data;
    Context context;

    public PlayerAdapter(List<Player> data, Context context) {
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
        PlayerAdapterLayoutBinding binding;
        if (convertView!=null)
            binding = (PlayerAdapterLayoutBinding) convertView.getTag();
        else {
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.player_adapter_layout, null, false);
            convertView = binding.getRoot();
        }

        Player player = data.get(position);
        binding.txtNamePlayerAdapter.setText(player.getUsername());
        Typeface wordType = Typeface.createFromAsset(context.getAssets(),"fonts/zombie_font_names.ttf");
        binding.txtNamePlayerAdapter.setTypeface(wordType);
        convertView.setTag(binding);
        return convertView;
    }
}

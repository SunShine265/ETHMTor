package com.goodproductssoft.flypoolmonitor.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.goodproductssoft.flypoolmonitor.MyPreferences;
import com.goodproductssoft.flypoolmonitor.R;
import com.goodproductssoft.flypoolmonitor.models.Miner;

import java.util.ArrayList;

/**
 * Created by user on 4/18/2018.
 */

public class IdMinerAdapter extends ArrayAdapter<Miner> {
    ArrayList<Miner> data;
    TextView title_coin;
    Context context;
    MyPreferences myPreferences;
    ArrayList<Miner> miners;
    Miner minerIsActive;
    static String endpoint = "https://api-zcash.flypool.org";

    public IdMinerAdapter(@NonNull Context context, ArrayList<Miner> data) {
        super(context, 0, data);
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_id_miner, parent, false);
        }
        TextView id_miner, email, payout, ip, title_type_coin;
        ImageView btn_delete_id_miner;
        Switch id_on_off_notification;

        Miner item = data.get(position);
        id_miner = (TextView) convertView.findViewById(R.id.id_miner);
        title_coin = (TextView) convertView.findViewById(R.id.title_coin);
        id_on_off_notification = (Switch)convertView.findViewById(R.id.id_on_off_notification);
        email = (TextView) convertView.findViewById(R.id.email);
        payout = (TextView) convertView.findViewById(R.id.payout);
        ip = (TextView) convertView.findViewById(R.id.ip);
        title_type_coin = (TextView) convertView.findViewById(R.id.title_type_coin);
        myPreferences = new MyPreferences();
        miners = myPreferences.GetIdMiners(context);
        minerIsActive = GetMinerIdActive();

        id_miner.setText(item.getId());
        title_coin.setText(context.getString(R.string.zcash));
        if(id_on_off_notification.isChecked()){
            minerIsActive.setNotification(true);
            myPreferences.UpdateMiner(getContext(), minerIsActive);
        }
        else {
            minerIsActive.setNotification(false);
            myPreferences.UpdateMiner(getContext(), minerIsActive);
        }
        id_on_off_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    minerIsActive.setNotification(true);
                    myPreferences.UpdateMiner(getContext(), minerIsActive);
                }
                else {
                    minerIsActive.setNotification(false);
                    myPreferences.UpdateMiner(getContext(), minerIsActive);
                }
            }
        });
        email.setText(item.getSettings().getEmail());
        ip.setText(item.getSettings().getIp());
        payout.setText(String.valueOf(item.getSettings().getPayout()));
        return convertView;
    }

    private Miner GetMinerIdActive(){
        if(miners != null && !miners.isEmpty()) {
            for (int i = 0; i < miners.size(); i++) {
                if (miners.get(i).isActive()) {
                    return miners.get(i);
                }
            }
        }
        return null;
    }
}

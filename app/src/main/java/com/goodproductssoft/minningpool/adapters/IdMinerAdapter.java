package com.goodproductssoft.minningpool.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.goodproductssoft.minningpool.MyPreferences;
import com.goodproductssoft.minningpool.R;
import com.goodproductssoft.minningpool.models.Miner;

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

    static String endpointEth = "https://api.ethermine.org";
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
        Miner item = data.get(position);
        TextView id_miner, email, payout, ip, title_type_coin, edit_miner_settings;
        ImageView btn_delete_id_miner;
        Switch id_on_off_notification;
        CheckBox id_send_email;

        id_miner = convertView.findViewById(R.id.id_miner);
        title_coin = convertView.findViewById(R.id.title_coin);
        btn_delete_id_miner = convertView.findViewById(R.id.btn_delete_id_miner);
        title_type_coin = convertView.findViewById(R.id.title_type_coin);
        email = convertView.findViewById(R.id.email);
        payout = convertView.findViewById(R.id.payout);
        ip = convertView.findViewById(R.id.ip);
        id_on_off_notification = convertView.findViewById(R.id.id_on_off_notification);
        id_send_email = convertView.findViewById(R.id.id_send_email);
        edit_miner_settings = convertView.findViewById(R.id.edit_miner_settings);
        myPreferences = MyPreferences.getInstance();
        miners = myPreferences.GetIdMiners();
        minerIsActive = GetMinerIdActive();

        //id_miner.setText(item.getId());
        String urlTxHash = "";
        if(item.getType() == Miner.CoinType.ETH){
            urlTxHash = "https://www.etherchain.org/account/" + item.getId();
        }
        else if(item.getType() == Miner.CoinType.ETC){
            urlTxHash = "https://etcchain.com/addr/" + item.getId();
        }
        else {
            urlTxHash = "https://explorer.zcha.in/accounts/" + item.getId();
        }
        String linkedText = String.format("<a href=\"%s\">"+ item.getId() + "</a> ", urlTxHash);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            id_miner.setText(Html.fromHtml(linkedText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            id_miner.setText(Html.fromHtml(linkedText));
        }
        id_miner.setMovementMethod(LinkMovementMethod.getInstance());


        if(item.getEndpoint().equals(endpointEth)){
            title_coin.setText(context.getString(R.string.eth));
        }
        else {
            title_coin.setText(context.getString(R.string.etc));
        }

        if(id_on_off_notification.isChecked()){
            minerIsActive.setNotification(true);
            myPreferences.UpdateMiner(minerIsActive);
        }
        else {
            minerIsActive.setNotification(false);
            myPreferences.UpdateMiner(minerIsActive);
        }
        //update Ui switch
        if(item.isNotification()){
            id_on_off_notification.setChecked(true);
        }
        else
            id_on_off_notification.setChecked(false);
        id_on_off_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    minerIsActive.setNotification(true);
                    myPreferences.UpdateMiner(minerIsActive);
                }
                else {
                    minerIsActive.setNotification(false);
                    myPreferences.UpdateMiner(minerIsActive);
                }
            }
        });
        email.setText(item.getSettings().getEmail());
        ip.setText(item.getSettings().getIp());
        payout.setText(String.valueOf(item.getSettings().getPayout()));
        if(item.getEndpoint().equals(endpointEth)){
            title_type_coin.setText(context.getString(R.string.eth));
        }
        else
            title_type_coin.setText(context.getString(R.string.etc));
        if(item.getSettings().getMonitor() == 0){
            id_send_email.setChecked(false);
        }
        else
            id_send_email.setChecked(true);
        String urlURLEditSettings = "";
        String linkedTextEditSetting = "";
        if(item.getType() == Miner.CoinType.ETH){
            urlURLEditSettings = "https://ethermine.org/miners/" + item.getId() + "/settings";
            linkedTextEditSetting = String.format("<a href=\"%s\"> " + "Edit on ethermine.org"  + " </a> ", urlURLEditSettings);
        }
        else if(item.getType() == Miner.CoinType.ETC){
            urlURLEditSettings = "https://etc.ethermine.org/miners/" + item.getId() + "/settings";
            linkedTextEditSetting = String.format("<a href=\"%s\">" + "Edit on etc.ethermine.org" + " </a> ", urlURLEditSettings);
        }
        else {
            urlURLEditSettings = "https://zcash.flypool.org/miners/" + item.getId() + "/settings";
            linkedTextEditSetting = String.format("<a href=\"%s\">" + "Edit on zcash.flypool.org" + "</a> ", urlURLEditSettings);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            edit_miner_settings.setText(Html.fromHtml(linkedTextEditSetting, Html.FROM_HTML_MODE_LEGACY));
        } else {
            edit_miner_settings.setText(Html.fromHtml(linkedTextEditSetting));
        }
        edit_miner_settings.setMovementMethod(LinkMovementMethod.getInstance());
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

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
import android.widget.TextView;

import com.goodproductssoft.minningpool.R;
import com.goodproductssoft.minningpool.models.Payouts;

import java.util.ArrayList;

/**
 * Created by user on 4/19/2018.
 */

public class PayoutAdapter extends ArrayAdapter<Payouts> {
    private ArrayList<Payouts> payoutses;
    TextView your_payouts, duration, txhash, count_coin;
    String type;
    public PayoutAdapter(@NonNull Context context, ArrayList<Payouts> data, String typeCoin) {
        super(context, 0, data);
        this.payoutses = data;
        this.type = typeCoin;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Payouts item = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_payouts, null);
        }
        your_payouts = (TextView)convertView.findViewById(R.id.your_payouts);
        duration = (TextView)convertView.findViewById(R.id.duration);
        txhash = (TextView)convertView.findViewById(R.id.txhash);
        count_coin = (TextView)convertView.findViewById(R.id.count_coin);
        your_payouts.setText(item.getPaidOn());
        duration.setText(item.getDuration());
//        txhash.setText(item.getTxHash());
        count_coin.setText(item.getAmount());
        String urlTxHash = "";
        if(type.equals("ETH")){
            urlTxHash = "https://www.etherchain.org/tx/" + item.getTxHash();
        }
        else if(type.equals("ETC")){
            urlTxHash = "http://gastracker.io/tx/" + item.getTxHash();
        }
        else {
            urlTxHash = "https://explorer.zcha.in/transactions/" + item.getTxHash();
        }
        String linkedText = String.format("<a href=\"%s\">"+ item.getTxHash() + "</a> ", urlTxHash);
        if(item.getTxHash().length() > 20) {
            linkedText = String.format("<a href=\"%s\">" + item.getTxHash().substring(0, 9)
                    + "..." + item.getTxHash().substring(item.getTxHash().length() - 9) + "</a> ", urlTxHash);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            txhash.setText(Html.fromHtml(linkedText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            txhash.setText(Html.fromHtml(linkedText));
        }
        txhash.setMovementMethod(LinkMovementMethod.getInstance());
        return convertView;
    }
}

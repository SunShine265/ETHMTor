package com.goodproductssoft.minningpool.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goodproductssoft.minningpool.models.YourWorker;
import com.goodproductssoft.minningpool.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by user on 4/19/2018.
 */

public class YourWorkerAdapter extends ArrayAdapter<YourWorker> {
    private ArrayList<YourWorker> yourWorkers;
    private TextView your_worker, currents, reported, avg, unit_currents, unit_reported, unit_avg, valid, stale, invalite, last_screen;
    LinearLayout item_your_worker, ln_shares, ln_current, ln_reported, ln_avg, ln_last_screen;
    public YourWorkerAdapter(@NonNull Context context, ArrayList<YourWorker> data) {
        super(context, 0, data);
        this.yourWorkers = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        YourWorker item = getItem(position);
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_worker, null);
        }
        ln_shares = (LinearLayout)convertView.findViewById(R.id.ln_shares);
        ln_current = (LinearLayout)convertView.findViewById(R.id.ln_current);
        ln_reported = (LinearLayout)convertView.findViewById(R.id.ln_reported);
        ln_avg= (LinearLayout)convertView.findViewById(R.id.ln_avg);
        ln_last_screen = (LinearLayout)convertView.findViewById(R.id.ln_last_screen);
        item_your_worker = (LinearLayout) convertView.findViewById(R.id.item_your_worker);
        your_worker = (TextView)convertView.findViewById(R.id.your_payouts);
        currents = (TextView)convertView.findViewById(R.id.currents);
        reported = (TextView)convertView.findViewById(R.id.reported);
        avg = (TextView)convertView.findViewById(R.id.avg);
        unit_reported = (TextView)convertView.findViewById(R.id.unit_reported);
        unit_currents = (TextView)convertView.findViewById(R.id.unit_currents);
        unit_avg = (TextView)convertView.findViewById(R.id.unit_avg);
        valid = (TextView)convertView.findViewById(R.id.valid);
        stale = (TextView)convertView.findViewById(R.id.stale);
        invalite = (TextView)convertView.findViewById(R.id.invalite);
        last_screen = (TextView)convertView.findViewById(R.id.last_screen);
        your_worker.setText(item.getYourWorker());
        avg.setText(ChangeHashrate(item.getAvg()));
        currents.setText(ChangeHashrate(item.getCurrent()));
        reported.setText(ChangeHashrate(item.getReported()));
        unit_avg.setText(ChangeHashrateUnit(item.getAvg()));
        unit_currents.setText(ChangeHashrateUnit(item.getCurrent()));
        unit_reported.setText(ChangeHashrateUnit(item.getReported()));
        valid.setText(String.valueOf(item.getValid()));
        stale.setText(String.valueOf(item.getStale()));
        invalite.setText(String.valueOf(item.getInvalid()));
        last_screen.setText(String.valueOf(item.getLastScreen()));
        if(!item.isValue()){
            your_worker.setBackgroundResource(R.color.invalid);
            ln_shares.setBackgroundResource(R.color.invalid);
            ln_current.setBackgroundResource(R.color.invalid);
            ln_reported.setBackgroundResource(R.color.invalid);
            ln_avg.setBackgroundResource(R.color.invalid);
            ln_last_screen.setBackgroundResource(R.color.invalid);
        }
        else {
            your_worker.setBackgroundResource(R.color.item_listview);
            ln_shares.setBackgroundResource(R.color.item_listview);
            ln_current.setBackgroundResource(R.color.item_listview);
            ln_reported.setBackgroundResource(R.color.item_listview);
            ln_avg.setBackgroundResource(R.color.item_listview);
            ln_last_screen.setBackgroundResource(R.color.item_listview);
        }
        return convertView;
    }

    public String ChangeHashrate(double value){
        String strTempValue = new DecimalFormat("#.##").format(value);
        if(value / 1000 >= 1){
            double lTempHS = value / 1000;
            strTempValue = new DecimalFormat("#.##").format(lTempHS);
            if(lTempHS / 1000 >= 1){
                double lTempKH = lTempHS / 1000;
                strTempValue = new DecimalFormat("#.##").format(lTempKH);
                if(lTempKH / 1000 >= 1){
                    double lTempMH = lTempKH / 1000;
                    strTempValue =  new DecimalFormat("#.##").format(lTempMH);
                    if(lTempMH / 1000 >= 1){
                        double lTempGH = lTempMH / 1000;
                        strTempValue = new DecimalFormat("#.##").format(lTempGH);
                    }
                }
            }
        }
        return strTempValue;
    }

    public String ChangeHashrateUnit(double value){
        String strTempValue = "H/s";
        if(value / 1000 >= 1){
            double lTempHS = value / 1000;
            strTempValue = " KH/s";
            if(lTempHS / 1000 >= 1){
                double lTempKH = lTempHS / 1000;
                strTempValue = " MH/s";
                if(lTempKH / 1000 >= 1){
                    double lTempMH = lTempKH / 1000;
                    strTempValue =  " GH/s";
                    if(lTempMH / 1000 >= 1){
                        strTempValue = " TH/s";
                    }
                }
            }
        }
        return strTempValue;
    }
}

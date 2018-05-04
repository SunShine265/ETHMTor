package com.goodproductssoft.flypoolmonitor.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.goodproductssoft.flypoolmonitor.models.YourWorker;
import com.goodproductssoft.flypoolmonitor.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by user on 4/19/2018.
 */

public class YourWorkerAdapter extends ArrayAdapter<YourWorker> {
    private ArrayList<YourWorker> yourWorkers;
    private TextView your_worker, currents, reported, valid, stale, invalite, last_screen;
    LinearLayout item_your_worker, ln_shares, ln_current, ln_reported, ln_last_screen;
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
        ln_last_screen = (LinearLayout)convertView.findViewById(R.id.ln_last_screen);
        item_your_worker = (LinearLayout) convertView.findViewById(R.id.item_your_worker);
        your_worker = (TextView)convertView.findViewById(R.id.your_worker);
        currents = (TextView)convertView.findViewById(R.id.currents);
        reported = (TextView)convertView.findViewById(R.id.reported);
        valid = (TextView)convertView.findViewById(R.id.valid);
        stale = (TextView)convertView.findViewById(R.id.stale);
        invalite = (TextView)convertView.findViewById(R.id.invalite);
        last_screen = (TextView)convertView.findViewById(R.id.last_screen);
        your_worker.setText(item.getYourWorker());
        currents.setText(ChangeHashrateWithUnit(item.getCurrent()));
        reported.setText(ChangeHashrateWithUnit(item.getReported()));
        valid.setText(String.valueOf(item.getValid()));
        stale.setText(String.valueOf(item.getStale()));
        invalite.setText(String.valueOf(item.getInvalid()));
        last_screen.setText(String.valueOf(item.getLastScreen()));
        if(!item.isValue()){
            your_worker.setBackgroundResource(R.color.invalid);
            ln_shares.setBackgroundResource(R.color.invalid);
            ln_current.setBackgroundResource(R.color.invalid);
            ln_reported.setBackgroundResource(R.color.invalid);
            ln_last_screen.setBackgroundResource(R.color.invalid);
        }
        return convertView;
    }

    public String ChangeHashrateWithUnit(double value){
        String strTempValue = new DecimalFormat("#.##").format(value) + " H/s";
        if(value / 1000 >= 1){
            double lTempHS = value / 1000;
            strTempValue = new DecimalFormat("#.##").format(lTempHS) + " KH/s";
            if(lTempHS / 1000 >= 1){
                double lTempKH = lTempHS / 1000;
                strTempValue = new DecimalFormat("#.##").format(lTempKH) + " MH/s";
                if(lTempKH / 1000 >= 1){
                    double lTempMH = lTempKH / 1000;
                    strTempValue =  new DecimalFormat("#.##").format(lTempMH) + " GH/s";
                    if(lTempMH / 1000 >= 1){
                        double lTempGH = lTempMH / 1000;
                        strTempValue = new DecimalFormat("#.##").format(lTempGH) + " TH/s";
                    }
                }
            }
        }
        return strTempValue;
    }
}

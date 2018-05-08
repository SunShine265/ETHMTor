package com.goodproductssoft.minningpool.activitys;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.goodproductssoft.minningpool.HttpHandler;
import com.goodproductssoft.minningpool.MyPreferences;
import com.goodproductssoft.minningpool.R;
import com.goodproductssoft.minningpool.adapters.PayoutAdapter;
import com.goodproductssoft.minningpool.models.Miner;
import com.goodproductssoft.minningpool.models.Payouts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class FragmentPayouts extends Fragment {
    ListView listView;
    private ArrayList<Payouts> valueList;
    Payouts payouts;
    TextView total_payouts, total_duration, total_eth, last_screen;
    MyPreferences myPreferences;
    Miner miner;
    String typeCoin;
    int checkAccount;
    static String endpointEth = "https://api.ethermine.org";
    static String endpointEtc = "https://api-etc.ethermine.org";

    ProgressDisplay getListener(){
        if(getActivity() != null && getActivity() instanceof ProgressDisplay){
            return ((ProgressDisplay) getActivity());
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payouts, container, false);
        listView = (ListView) view.findViewById(R.id.list);
        total_payouts = (TextView) view.findViewById(R.id.total_payouts);
        total_duration = (TextView) view.findViewById(R.id.total_duration);
        total_eth = (TextView) view.findViewById(R.id.total_eth);
        last_screen = (TextView) view.findViewById(R.id.last_screen);

        myPreferences = new MyPreferences();
        valueList = new ArrayList<>();
        payouts = new Payouts();
        if(getActivity() != null) {
            CheckInitContent(getActivity());
        }
        checkAccount = CheckAccount();
        ChangeLayout();
        return view;
    }

    private int CheckAccount(){
        if(miner != null){
            //etc
            if(miner.getEndpoint().equals(endpointEtc)){
                return -1;
            }
            else return 1;
        }
        return 0;
    }

    private void ChangeLayout(){
        //etc
        if(checkAccount == -1){
            last_screen.setText(getString(R.string.etc));
        }
        else if(checkAccount == 1){
            //eth
            last_screen.setText(getString(R.string.eth));
        }
    }

    public void CheckInitContent(Context a){
        miner = GetMinerIdActive();
        if(miner != null) {
            typeCoin = miner.getType().toString();
            String urlWorker = miner.getEndpoint() + "/miner/" + miner.getId() + "/payouts";
            new FragmentPayouts.GetPayouts().execute(urlWorker);
        }
    }

    private Miner GetMinerIdActive(){
        if(getActivity() != null) {
            ArrayList<Miner> miners = myPreferences.GetIdMiners(getActivity());
            if (miners != null && !miners.isEmpty()) {
                for (int i = 0; i < miners.size(); i++) {
                    if (miners.get(i).isActive()) {
                        return miners.get(i);
                    }
                }
            }
        }
        return null;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();//get your local time zone.
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        sdf.setTimeZone(tz);//set time zone.
        String localTime = sdf.format(new Date(time*1000));
        String strDate="";
        Date date = new Date();
        try {
            date = sdf.parse(localTime);//get local date
            strDate = sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetPayouts extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
//            progressbar.setVisibility(View.VISIBLE);
            if(getListener() != null){
                getListener().showProgress();
            }
        }

        @Override
        protected Void doInBackground(String... url) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url[0]);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONArray payoutsJson = jsonObj.getJSONArray("data");
                    if(payoutsJson != null && payoutsJson.length() > 0) {
                        double tempTotalETH = 0;
                        double tempTotalDuration = 0;
                        // looping through All Contacts
                        for (int i = 0; i < payoutsJson.length(); i++) {
                            Payouts itemPayout = new Payouts();
                            JSONObject value = payoutsJson.getJSONObject(i);
                            double amount;
                            try {
                                amount = value.getDouble("amount") / 1000000000;
                                amount = amount / 1000000000;
                            } catch (JSONException ex) {
                                amount = 0;
                            }
                            tempTotalETH += amount;
                            String stramount = new DecimalFormat("#.###").format(amount);

                            String txHash = value.getString("txHash");

                            long paidOn = value.getLong("paidOn");
                            String strDate = getDate(paidOn);

                            double duration = 0;
                            if (i == payoutsJson.length() - 1) {
                                duration = 0;
                            } else {
                                JSONObject valueNext = payoutsJson.getJSONObject(i + 1);
                                long paidOnNext = valueNext.getLong("paidOn");
                                duration = (paidOn - paidOnNext) / 3600.0;
                            }
                            tempTotalDuration += duration;
                            String strDuration = new DecimalFormat("#.#").format(duration);
                            itemPayout.setAmount(stramount);
                            itemPayout.setTxHash(txHash);
                            itemPayout.setDuration(strDuration);
                            itemPayout.setPaidOn(strDate);
                            valueList.add(itemPayout);
                        }
                        payouts.setTotal(payoutsJson.length());
                        payouts.setTotalETH(tempTotalETH);
                        payouts.setTotalDays(tempTotalDuration / 24f);
                    }
                    else {
                        if(getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "No data",
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        }
                    }
                } catch (final JSONException e) {
                    if(getActivity() != null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Data parsing error",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }
                }
            } else {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Couldn't get Data from server. !",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if(getListener() != null){
                getListener().hideProgress();
            }
            /**
             * Updating parsed JSON data into ListView
             * */
            Activity activity = getActivity();
            if (isAdded() && activity != null) {
                if (valueList.size() > 0) {
                    PayoutAdapter adapter = new PayoutAdapter(activity, valueList, typeCoin);
                    listView.setAdapter(adapter);
//                    ListAdapter adapter = new SimpleAdapter(
//                            getActivity(), valueList,
//                            R.layout.list_item_payouts, new String[]{"payon", "duration", "txHash",
//                            "amount"}, new int[]{R.id.your_worker, R.id.duration,
//                            R.id.txhash, R.id.eth});
//
//                    listView.setAdapter(adapter);
                }
                total_payouts.setText(String.valueOf(payouts.getTotal()));
                total_duration.setText(new DecimalFormat("##.#").format(payouts.getTotalDays()));
                total_eth.setText(new DecimalFormat("#.###").format(payouts.getTotalETH()));
            }
        }
    }

    public interface ProgressDisplay {

        void showProgress();

        void hideProgress();
    }
}

package com.goodproductssoft.minningpool.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.goodproductssoft.minningpool.CustomApp;
import com.goodproductssoft.minningpool.HttpHandler;
import com.goodproductssoft.minningpool.MyPreferences;
import com.goodproductssoft.minningpool.R;
import com.goodproductssoft.minningpool.WebService;
import com.goodproductssoft.minningpool.controls.CustomMarkerView;
import com.goodproductssoft.minningpool.models.Miner;
import com.goodproductssoft.minningpool.models.NetworkStats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 4/12/2018.
 */

public class FragmentMiner extends Fragment  {
    private static String[] urlData;
    private TextView reported_hashrate, current_hashrate, average_hashrate, unpaid_balance,
            active_workers, valid_shares, invalid_shares, stale_shares, last_screen, hour_eth,
            hour_usd, hour_btc, day_eth, day_usd, day_btc, week_eth, week_usd, week_btc, month_eth,
            month_usd, month_btc, eth_btc, eth_usd, chart_hashrate, chart_shares, time_next_payout,
            btc_usd, goal_day, unit_reported, unit_current, unit_average, title_unpaid, title_coin,
            title_coin_btc, title_coin_usd;
    View percent_next_payout, show_chart;
    private CurrentStats curentStats;
    private NetworkStats networkStats;
    private LineChart lineChart;
    private LinearLayout content_miner, chart_miner, view_chart, price_coin, child_content_miner;
    private BarChart barChart;
    ArrayList<Entry> yValueCurrentHashRate, yValueReportHashrate, yValueAverageHashrate, yValueWorker,
            yValueValidShares, yValueInValidShares, yValueStaleShares, yValueCurrentHashRateHighlight,
            yValueWorkerHighlight, yValueReportHashrateHighlight, yValueAverageHashrateHighlight;
    ArrayList<BarEntry> yValuesShares;
    long paidOn;
    double minPayout;
    int checkAccount;
    MyPreferences myPreferences;
    static String endpointEtc = "https://api-etc.ethermine.org";
    Miner miner;
    ArrayList<Miner> miners;
    HashMap<Integer, Long> timeHistory;
    static String NO_DATA="NO DATA";
    Retrofit retrofit;
    HashMap<String, Boolean> responses;

    ProgressDisplay getListener(){
        if(getActivity()!= null && getActivity() instanceof ProgressDisplay){
            return (ProgressDisplay) getActivity();
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_miner, viewGroup, false);
        reported_hashrate = view.findViewById(R.id.reported_hashrate);
        current_hashrate = view.findViewById(R.id.current_hashrate);
        average_hashrate = view.findViewById(R.id.average_hashrate);
        unpaid_balance = view.findViewById(R.id.unpaid_balance);
        active_workers = view.findViewById(R.id.active_workers);
        valid_shares = view.findViewById(R.id.valid_shares);
        invalid_shares = view.findViewById(R.id.invalid_shares);
        stale_shares = view.findViewById(R.id.stale_shares);
        last_screen = view.findViewById(R.id.last_screen);
        hour_eth = view.findViewById(R.id.hour_eth);
        hour_usd = view.findViewById(R.id.hour_usd);
        hour_btc = view.findViewById(R.id.hour_btc);
        day_eth = view.findViewById(R.id.day_eth);
        day_usd = view.findViewById(R.id.day_usd);
        day_btc = view.findViewById(R.id.day_btc);
        week_eth = view.findViewById(R.id.week_eth);
        week_usd = view.findViewById(R.id.week_usd);
        week_btc = view.findViewById(R.id.week_btc);
        month_eth = view.findViewById(R.id.month_eth);
        month_usd = view.findViewById(R.id.month_usd);
        month_btc = view.findViewById(R.id.month_btc);
        eth_btc = view.findViewById(R.id.eth_btc);
        eth_usd = view.findViewById(R.id.eth_usd);
        btc_usd = view.findViewById(R.id.btc_usd);
        lineChart = view.findViewById(R.id.line_chart);
        barChart = view.findViewById(R.id.bar_chart);
        chart_hashrate = view.findViewById(R.id.chart_hashrate);
        chart_shares = view.findViewById(R.id.chart_shares);
        time_next_payout = view.findViewById(R.id.time_next_payout);
        percent_next_payout = view.findViewById(R.id.percent_next_payout);
        goal_day = view.findViewById(R.id.goal_day);
        unit_reported = view.findViewById(R.id.unit_reported);
        unit_current = view.findViewById(R.id.unit_current);
        unit_average = view.findViewById(R.id.unit_average);
        title_unpaid = view.findViewById(R.id.title_unpaid);
        title_coin = view.findViewById(R.id.title_coin);
        title_coin_btc = view.findViewById(R.id.title_coin_btc);
        title_coin_usd = view.findViewById(R.id.title_coin_usd);
        show_chart = view.findViewById(R.id.show_chart);
        content_miner = (LinearLayout) view.findViewById(R.id.content_miner);
        chart_miner = (LinearLayout) view.findViewById(R.id.chart_miner);
        view_chart = (LinearLayout) view.findViewById(R.id.view_chart);
        price_coin = (LinearLayout) view.findViewById(R.id.price_coin);
        child_content_miner = (LinearLayout) view.findViewById(R.id.child_content_miner);

        curentStats = new CurrentStats();
        networkStats = new NetworkStats();
        yValueAverageHashrate = new ArrayList<>();
        yValueCurrentHashRate = new ArrayList<>();
        yValueReportHashrate = new ArrayList<>();
        yValueAverageHashrateHighlight = new ArrayList<>();
        yValueCurrentHashRateHighlight = new ArrayList<>();
        yValueReportHashrateHighlight = new ArrayList<>();
        yValueWorkerHighlight = new ArrayList<>();
        yValueWorker = new ArrayList<>();
        yValueValidShares = new ArrayList<>();
        yValueInValidShares = new ArrayList<>();
        yValueStaleShares = new ArrayList<>();
        yValuesShares = new ArrayList<>();
        timeHistory = new HashMap<>();
        responses = new HashMap();

        myPreferences = MyPreferences.getInstance();
        miners = myPreferences.GetIdMiners();

        chart_hashrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart_hashrate.setBackgroundResource(R.color.background_selected_chart);
                chart_shares.setBackgroundResource(R.color.background_unselected_chart);
                lineChart.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
                if(yValueAverageHashrate.size() > 0 && yValueCurrentHashRate.size() > 0 && yValueReportHashrate.size() > 0){
                    DrawGraphHashRate(getActivity());
                }
            }
        });
        chart_shares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart_hashrate.setBackgroundResource(R.color.background_unselected_chart);
                chart_shares.setBackgroundResource(R.color.background_selected_chart);
                lineChart.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);
                if(yValuesShares.size() > 0){
                    DrawGraphShares();
                }
            }
        });
        miner = GetMinerIdActive();
        checkAccount = CheckAccount();
        ChangeLayout();
        if(miner != null) {
            urlData = new String[4];
//            urlData[0] = miner.getEndpoint() + "/networkStats";
            urlData[0] = miner.getEndpoint() + "/miner/" + miner.getId() + "/currentStats";
            urlData[1] = miner.getEndpoint() + "/miner/" + miner.getId() + "/history";
            urlData[2] = miner.getEndpoint() + "/miner/" + miner.getId() + "/payouts";
            urlData[3] = miner.getEndpoint() + "/miner/" + miner.getId() + "/Settings";

//            new GetAPI().execute(urlData);

            if(getListener() != null){
                getListener().showProgress();
            }
            responses.put("GetCurrentStats", false);
            responses.put("GetHistory", false);
            responses.put("GetPayouts", false);
            responses.put("GetSettings", false);
            responses.put("isNoData", false);
            responses.put("isParsingError", false);
            responses.put("isConnectError", false);
            GetDataCurrentStats();
            GetDataHistorys();
            GetDataPayouts();
            GetDataSettings();
        }
        CheckScreenOrientation(this.getActivity().getResources().getConfiguration().orientation);
        return view;
    }

    private void CheckScreenOrientation(int orientation){
        if(getActivity() != null){
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                ((ViewGroup)price_coin.getParent()).removeView(price_coin);

                float uiScreenMin = Math.min(getResources().getDisplayMetrics().heightPixels, getResources().getDisplayMetrics().widthPixels);
                float uiScreenMinDp = uiScreenMin / getResources().getDisplayMetrics().density;
                float minHeight = 430;

                if(chart_miner.getParent() != null){
                    ((ViewGroup)chart_miner.getParent()).removeView(chart_miner);
                }
                content_miner.addView(chart_miner);
                if(uiScreenMinDp >= minHeight)
                {
                    if(price_coin.getParent() != null){
                        ((ViewGroup)price_coin.getParent()).removeView(price_coin);
                    }
                    child_content_miner.addView(price_coin);
                }
                else {
                    if(price_coin.getParent() != null){
                        ((ViewGroup)price_coin.getParent()).removeView(price_coin);
                    }
                    chart_miner.addView(price_coin);
                }

                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                view_chart.setLayoutParams(p);
                view_chart.setPadding(0,0,0,0);
            }
            else {
                ((ViewGroup)chart_miner.getParent()).removeView(chart_miner);
                view_chart.addView(chart_miner);
                view_chart.setPadding(0, 0, 0, (int) (getResources().getDisplayMetrics().density * 5));
                content_miner.removeView(chart_miner);

                if(price_coin.getParent() != null){
                    ((ViewGroup)price_coin.getParent()).removeView(price_coin);
                }
                child_content_miner.addView(price_coin);

                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                p.weight = 1;
                view_chart.setLayoutParams(p);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        CheckScreenOrientation(newConfig.orientation);
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            ((ViewGroup)chart_miner.getParent()).removeView(chart_miner);
//            content_miner.addView(chart_miner);
//            view_chart.removeView(chart_miner);
//            ((ViewGroup)price_coin.getParent()).removeView(price_coin);
//            chart_miner.addView(price_coin);
//            child_content_miner.removeView(price_coin);
//            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            view_chart.setLayoutParams(p);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            ((ViewGroup)chart_miner.getParent()).removeView(chart_miner);
//            view_chart.addView(chart_miner);
//            content_miner.removeView(chart_miner);
//            ((ViewGroup)price_coin.getParent()).removeView(price_coin);
//            child_content_miner.addView(price_coin);
//            chart_miner.removeView(price_coin);
//            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
//            p.weight = 1;
//            view_chart.setLayoutParams(p);
//        }
    }

    private void GetDataCurrentStats(){
        retrofit = new Retrofit.Builder()
                .baseUrl(miner.getEndpoint())
                .client(CustomApp.getHttpClient())
//                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();

        WebService ws = retrofit.create(WebService.class);
        Call<ResponseBody> result = ws.GetCurrentStats(miner.getId());
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Activity activity = FragmentMiner.this.getActivity();
                try {
                    String jsonStr = response.body().string();
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    if(activity != null && jsonObj.get("data").equals(NO_DATA)){
                        responses.put("isNoData", true);
                    }
                    else {
                        JSONObject data = jsonObj.getJSONObject("data");
                        long reported, current, average, unpaidBalance;
                        double coinsPerMin, usdPerMin, btcPerMin;
                        int workers, lastScreen, validShares, invalidShares, staleShares;
                        try {
                            reported = data.getLong("reportedHashrate");
                        } catch (JSONException e) {
                            reported = 0;
                        }
                        try {
                            current = data.getLong("currentHashrate");
                        } catch (JSONException e) {
                            current = 0;
                        }
                        try {
                            average = data.getLong("averageHashrate");
                        } catch (JSONException e) {
                            average = 0;
                        }
                        try {
                            unpaidBalance = data.getLong("unpaid");
                        } catch (JSONException e) {
                            unpaidBalance = 0;
                        }
                        try {
                            workers = data.getInt("activeWorkers");
                        } catch (JSONException e) {
                            workers = 0;
                        }
                        try {
                            lastScreen = data.getInt("lastSeen");
                        } catch (JSONException e) {
                            lastScreen = 0;
                        }
                        try {
                            validShares = data.getInt("validShares");
                        } catch (JSONException e) {
                            validShares = 0;
                        }
                        try {
                            invalidShares = data.getInt("invalidShares");
                        } catch (JSONException e) {
                            invalidShares = 0;
                        }
                        try {
                            staleShares = data.getInt("staleShares");
                        } catch (JSONException e) {
                            staleShares = 0;
                        }
                        try {
                            coinsPerMin = data.getDouble("coinsPerMin");
                        } catch (JSONException e) {
                            coinsPerMin = 0;
                        }
                        try {
                            usdPerMin = data.getDouble("usdPerMin");
                        } catch (JSONException e) {
                            usdPerMin = 0;
                        }
                        try {
                            btcPerMin = data.getDouble("btcPerMin");
                        } catch (JSONException e) {
                            btcPerMin = 0;
                        }

                        curentStats.setPriceEthbtc(btcPerMin / coinsPerMin);
                        curentStats.setPriceEthUsd(usdPerMin / coinsPerMin);
                        curentStats.setPriceBtcUsd(usdPerMin / btcPerMin);
                        curentStats.setReportedHashrate(reported);
                        curentStats.setCurrentHashrate(current);
                        curentStats.setAverageHashrate(average);
                        curentStats.setUnpaid(unpaidBalance);
                        curentStats.setActiveWorkers(workers);
                        curentStats.setLastSeen(lastScreen);
                        curentStats.setValidShares(validShares);
                        curentStats.setInvalidShares(invalidShares);
                        curentStats.setStaleShares(staleShares);
                        curentStats.setCoinsPerMin(coinsPerMin);
                        curentStats.setBtcPerMin(btcPerMin);
                        curentStats.setUsdPerMin(usdPerMin);
                    }
                } catch (Exception ex) {
                    responses.put("isParsingError", true);
                }

                responses.put("GetCurrentStats", true);
                ShowResult(responses);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responses.put("isConnectError", true);
                responses.put("GetCurrentStats", true);
                ShowResult(responses);
            }
        });
    }

    private void GetDataHistorys(){
        retrofit = new Retrofit.Builder()
                .baseUrl(miner.getEndpoint())
                .client(CustomApp.getHttpClient())
                .build();

        WebService ws = retrofit.create(WebService.class);
        Call<ResponseBody> result = ws.GetHistory(miner.getId());
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Activity activity = FragmentMiner.this.getActivity();
                try {
                    String jsonStr = response.body().string();
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray historys = jsonObj.getJSONArray("data");
                    if(historys != null && historys.length() > 0) {
                        for (int i = 0; i < historys.length(); i++) {
                            int value = i + 1;
                            JSONObject valueJson = historys.getJSONObject(i);
                            double dbReportedHashrate;
                            try {
                                dbReportedHashrate = valueJson.getDouble("reportedHashrate");
                            } catch (JSONException e) {
                                dbReportedHashrate = 0;
                            }

                            double dbCurrentHashrate;
                            try {
                                dbCurrentHashrate = valueJson.getDouble("currentHashrate");
                            } catch (JSONException e) {
                                dbCurrentHashrate = 0;
                            }

                            double dbAverageHashrate;
                            try {
                                dbAverageHashrate = valueJson.getDouble("averageHashrate");
                            } catch (JSONException e) {
                                dbAverageHashrate = 0;
                            }

                            long dbWorker;
                            try {
                                dbWorker = (long) valueJson.getDouble("activeWorkers");
                            } catch (JSONException e) {
                                dbWorker = 0;
                            }
                            double dbValidShare;
                            try {
                                dbValidShare = valueJson.getDouble("validShares");
                            } catch (JSONException e) {
                                dbValidShare = 0;
                            }
                            double dbInValidShare;
                            try {
                                dbInValidShare = valueJson.getDouble("invalidShares");
                            } catch (JSONException e) {
                                dbInValidShare = 0;
                            }
                            double dbStaleShare;
                            try {
                                dbStaleShare = valueJson.getDouble("staleShares");
                            } catch (JSONException e) {
                                dbStaleShare = 0;
                            }

                            long time;
                            try {
                                time = valueJson.getLong("time");
                            } catch (JSONException e) {
                                time = 0;
                            }
                            timeHistory.put(i + 1, time);
                            float totalshares = (float) dbValidShare + (float) dbInValidShare + (float) dbStaleShare;
                            float percentInValid = ((float) dbInValidShare * 100) / totalshares;
                            float percentStale = ((float) dbStaleShare * 100) / totalshares;
                            yValuesShares.add(new BarEntry(i, new float[]{percentStale, percentInValid}));
                            yValueReportHashrate.add(new Entry(time, (float) ChangeHashrate(dbReportedHashrate)));
                            yValueCurrentHashRate.add(new Entry(time, (float) ChangeHashrate(dbCurrentHashrate)));
                            yValueAverageHashrate.add(new Entry(time, (float) ChangeHashrate(dbAverageHashrate)));
                            yValueWorker.add(new Entry(time, dbWorker));
                            yValueReportHashrateHighlight.add(new Entry(time, (float) dbReportedHashrate));
                            yValueCurrentHashRateHighlight.add(new Entry(time, (float) dbCurrentHashrate));
                            yValueAverageHashrateHighlight.add(new Entry(time, (float) dbAverageHashrate));
                            yValueWorkerHighlight.add(new Entry(time, dbWorker));
                        }
                    }
                    else {
                        responses.put("isNoData", true);
                    }
                } catch (Exception ex) {
                    responses.put("isParsingError", true);
                }

                responses.put("GetHistory", true);
                ShowResult(responses);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responses.put("isConnectError", true);
                responses.put("GetHistory", true);
                ShowResult(responses);
            }
        });
    }

    private void GetDataPayouts(){
        retrofit = new Retrofit.Builder()
                .baseUrl(miner.getEndpoint())
                .client(CustomApp.getHttpClient())
                .build();

        WebService ws = retrofit.create(WebService.class);
        Call<ResponseBody> result = ws.GetPayouts(miner.getId());
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Activity activity = FragmentMiner.this.getActivity();
                try {
                    String jsonStr = response.body().string();
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray payoutsArray = jsonObj.getJSONArray("data");
                    if(payoutsArray != null && payoutsArray.length() > 0) {
                        JSONObject valueJson = payoutsArray.getJSONObject(0);
                        paidOn = valueJson.getLong("paidOn");
                    }
                    else {
                        responses.put("isNoData", true);
                    }
                } catch (Exception ex) {
                    responses.put("isParsingError", true);
                }

                responses.put("GetPayouts", true);
                ShowResult(responses);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responses.put("isConnectError", true);
                responses.put("GetPayouts", true);
                ShowResult(responses);
            }
        });
    }

    private void GetDataSettings(){
        retrofit = new Retrofit.Builder()
                .baseUrl(miner.getEndpoint())
                .client(CustomApp.getHttpClient())
                .build();

        WebService ws = retrofit.create(WebService.class);
        Call<ResponseBody> result = ws.GetSettings(miner.getId());
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Activity activity = FragmentMiner.this.getActivity();
                try {
                    String jsonStr = response.body().string();
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    if(jsonObj.get("data").equals(NO_DATA)){
                        responses.put("isNoData", true);
                    }
                    else {
                        JSONObject settings = jsonObj.getJSONObject("data");
                        minPayout = settings.getDouble("minPayout") / 1000000000;
                        minPayout = minPayout / 1000000000;
                    }
                } catch (Exception ex) {
                    responses.put("isParsingError", true);
                }

                responses.put("GetSettings", true);
                ShowResult(responses);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                responses.put("isConnectError", true);
                responses.put("GetSettings", true);
                ShowResult(responses);
            }
        });
    }

    public void ShowResult(HashMap<String, Boolean> responses){
        Activity activity = FragmentMiner.this.getActivity();
        if(activity != null){
            if(responses.get("GetCurrentStats")
                    && responses.get("GetHistory")
                    && responses.get("GetPayouts")
                    && responses.get("GetSettings")){
                if(getListener() != null){
                    getListener().hideProgress();
                }
//                if(responses.get("isConnectError")){
//                    CustomApp.showToast("Couldn't get data from server!");
//                }
                else if(responses.get("isNoData")){
                    String urlTypeCoin = "";
                    if(miner.getType().equals(Miner.CoinType.ETH)){
                        urlTypeCoin = "https://ethpool.org/";
                    }
                    else if(miner.getType().equals(Miner.CoinType.ETC)){
                        urlTypeCoin = "https://etc.ethermine.org";
                    }
                    else
                        urlTypeCoin = "https://zcash.flypool.org/";

                    AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
                    alertDialog.setTitle("No Data");
                    alertDialog.setMessage("Sorry, no data for your wallet. This app is not mining app, " +
                            "only use to monitor for ethermine. Please see " + urlTypeCoin + " to get more details.");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Close",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                    final String finalUrlTypeCoin = urlTypeCoin;
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Go website",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrlTypeCoin));
                                    startActivity(browserIntent);
                                }
                            });
                    alertDialog.show();
                }
                else if(responses.get("isParsingError")){
                    CustomApp.showToast("Data error");
                }

                SetView();
                // Draw line chart
                if(yValueAverageHashrate.size() > 0 && yValueReportHashrate.size() > 0 && yValueCurrentHashRate.size() > 0) {
                    lineChart.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.GONE);
                    DrawGraphHashRate(activity);
                }
            }
        }
    }

    private Miner GetMinerIdActive(){
        if(getActivity()!= null) {
            ArrayList<Miner> miners = myPreferences.GetIdMiners();
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
//                unit_reported.setText(getString(R.string.unit_hashrate_etc));
//                unit_current.setText(getString(R.string.unit_hashrate_etc));
//                unit_average.setText(getString(R.string.unit_hashrate_etc));
                title_unpaid.setText(getString(R.string.etc));
                title_coin.setText(getString(R.string.etc));
                title_coin_btc.setText(R.string.etc_btc);
                title_coin_usd.setText(R.string.etc_usd);
            }
            else if(checkAccount == 1){
                //eth
//                unit_reported.setText(getString(R.string.unit_hashrate_eth));
//                unit_current.setText(getString(R.string.unit_hashrate_eth));
//                unit_average.setText(getString(R.string.unit_hashrate_eth));
                title_unpaid.setText(getString(R.string.eth));
                title_coin.setText(getString(R.string.eth));
                title_coin_btc.setText(R.string.eth_btc);
                title_coin_usd.setText(R.string.eth_usd);
            }
    }

    private void DrawGraphHashRate(Activity activity){
        lineChart.setDragEnabled(true);
        lineChart.getDescription().setEnabled(false);

        Legend legend = lineChart.getLegend();
        legend.setWordWrapEnabled(true);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        lineChart.setScaleEnabled(false);

        YAxis axisRight = lineChart.getAxisRight();
        axisRight.setGranularity(1f);
        axisRight.setDrawGridLines(false);

//        lineChart.getXAxis().setTextColor(Color.TRANSPARENT);
        lineChart.getAxisRight().setTextColor(Color.WHITE);
        lineChart.getLegend().setTextColor(Color.WHITE);

        LineDataSet lineReported = new LineDataSet(yValueReportHashrate, "Reported Hashrate");
        lineReported.setFillAlpha(2);
        lineReported.setColor(getResources().getColor(R.color.chart_reported_hashrate));
        lineReported.setCircleRadius(1);
        lineReported.setCircleColor(getResources().getColor(R.color.chart_reported_hashrate));
        lineReported.setLineWidth(0.4f);
        lineReported.setValueTextColor(Color.TRANSPARENT);

        LineDataSet lineCurrent = new LineDataSet(yValueCurrentHashRate, "Current Hashrate");
        lineCurrent.setFillAlpha(2);
        lineCurrent.setColor(getResources().getColor(R.color.chart_current_hashrate));
        lineCurrent.setCircleRadius(1);
        lineCurrent.setCircleColor(getResources().getColor(R.color.chart_current_hashrate));
        lineCurrent.setLineWidth(0.4f);
        lineCurrent.setValueTextColor(Color.TRANSPARENT);

        LineDataSet lineAverage = new LineDataSet(yValueAverageHashrate, "Average Hashrate");
        lineAverage.setFillAlpha(2);
        lineAverage.setColor(getResources().getColor(R.color.chart_average_hashrate));
        lineAverage.setCircleRadius(1);
        lineAverage.setCircleColor(getResources().getColor(R.color.chart_average_hashrate));
        lineAverage.setLineWidth(0.4f);
        lineAverage.setValueTextColor(Color.TRANSPARENT);

        LineDataSet lineWorker = new LineDataSet(yValueWorker, "Active Workers");
        lineWorker.setMode(LineDataSet.Mode.LINEAR);
        lineWorker.setColor(Color.parseColor("#AA0000"));
        lineWorker.setDrawCircles(false);
        lineWorker.setDrawCircleHole(false);
        lineWorker.setLineWidth(0.6f);
        lineWorker.setAxisDependency(YAxis.AxisDependency.RIGHT);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.colorWhite));
        lineChart.getXAxis().setAxisMinimum(timeHistory.get(1));
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ConvertTimestampToTime((long)value);
            }
        });
        long detalTime = (timeHistory.get(timeHistory.size()) - timeHistory.get(1)) / 8;
        lineChart.getXAxis().setGranularity((float) Math.ceil(detalTime / 3600f) * 3600);
        xAxis.setEnabled(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineReported);
        dataSets.add(lineCurrent);
        dataSets.add(lineAverage);
        dataSets.add(lineWorker);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();
            lineChart.setDrawMarkers(true);
            CustomMarkerView customMarkerView = new CustomMarkerView(activity,
                    R.layout.custom_marker_view_layout, yValueReportHashrateHighlight, yValueCurrentHashRateHighlight,
                    yValueAverageHashrateHighlight, yValueWorkerHighlight);
            lineChart.setMarker(customMarkerView);
    }

    private String ConvertTimestampToTime(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        cal.setTimeInMillis(time * 1000);
        Date dateTime = cal.getTime();
        String date = DateFormat.format("HH:mm", dateTime).toString();
        return date;
    }

    private void DrawGraphShares(){
        barChart.removeAllViews();
        barChart.setDragEnabled(true);
        //barChart.getDescription().setEnabled(false);
        barChart.getDescription().setText("");
        barChart.setScaleEnabled(false);
        barChart.setSelected(false);


//        barChart.getXAxis().setTextColor(Color.TRANSPARENT);
        barChart.getAxisRight().setTextColor(Color.TRANSPARENT);
        barChart.getLegend().setTextColor(Color.WHITE);


        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.colorWhite));
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ConvertTimestampToTime(timeHistory.get((int)value + 1));
            }
        });
        long detalTime = timeHistory.size() / 8;
        xAxis.setGranularity(detalTime);
        xAxis.setEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0);
        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return new DecimalFormat("###.#").format(value) + "%";
            }
        };
        leftAxis.setValueFormatter(xAxisFormatter);


        BarDataSet barShares;
        barShares = new BarDataSet(yValuesShares, "");
        barShares.setDrawIcons(false);
        barShares.setStackLabels(new String[]{"Stale Shares", "Invalid Shares"});
        barShares.setColors(getColors());

        barChart.getDescription().setTextColor(getResources().getColor(R.color.colorWhite));
        BarData data = new BarData(barShares);
        data.setBarWidth(0.6f);
        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.getData().setHighlightEnabled(false);
    }

    private int[] getColors() {
        int[] colors = new int[2];
        colors[0] = getResources().getColor(R.color.color_txt_average_hashrate);
        colors[1] = getResources().getColor(R.color.invalid);
        return colors;
    }

//    private void SetViewBottom(){
//        eth_usd.setText(new DecimalFormat("#.#").format(networkStats.getEthUsd()));
//        eth_btc.setText(new DecimalFormat("#.####").format(networkStats.getEthBtc()));
//        btc_usd.setText(new DecimalFormat("#.#").format(networkStats.getEthUsd()/networkStats.getEthBtc()));
//    }

    private void SetView(){
        reported_hashrate.setText(curentStats.getReportedHashrate());
        current_hashrate.setText(curentStats.getCurrentHashrate());
        average_hashrate.setText(curentStats.getAverageHashrate());
        unpaid_balance.setText(new DecimalFormat("#.#####").format(curentStats.getUnpaid()));
        active_workers.setText(String.valueOf(curentStats.getActiveWorkers()));
        int totalShare = curentStats.getValidShares() +  curentStats.getInvalidShares() + curentStats.getStaleShares();
        float percentValid = totalShare == 0? 0 : (curentStats.getValidShares()*100f)/ totalShare;
        float percentInValid = totalShare == 0? 0 :(curentStats.getInvalidShares()*100f)/ totalShare;
        float percentStale = totalShare == 0? 0 :(curentStats.getStaleShares()*100f)/ totalShare;
        valid_shares.setText(String.valueOf(curentStats.getValidShares() +"(" + new DecimalFormat("###.#").format(percentValid) + "%)"));
        invalid_shares.setText(String.valueOf(curentStats.getInvalidShares() +"(" + new DecimalFormat("###.#").format(percentInValid) + "%)"));
        stale_shares.setText(String.valueOf(curentStats.getStaleShares() +"(" + new DecimalFormat("###.#").format(percentStale) + "%)"));
        last_screen.setText(String.valueOf(curentStats.getLastSeen()));
        hour_eth.setText(new DecimalFormat("#.####").format(curentStats.getCoinsPerHr()));
        hour_btc.setText(new DecimalFormat("#.####").format(curentStats.getBtcPerMin()));
        hour_usd.setText(new DecimalFormat("#.##").format(curentStats.getUsdPerMin()));
        day_eth.setText(new DecimalFormat("#.####").format(curentStats.getCoinsPerHr()*24));
        day_btc.setText(new DecimalFormat("#.####").format(curentStats.getBtcPerMin()*24));
        day_usd.setText(new DecimalFormat("#.##").format(curentStats.getUsdPerMin()*24));
        week_eth.setText(new DecimalFormat("#.####").format(curentStats.getCoinsPerHr()*24*7));
        week_btc.setText(new DecimalFormat("#.####").format(curentStats.getBtcPerMin()*24*7));
        week_usd.setText(new DecimalFormat("#.##").format(curentStats.getUsdPerMin()*24*7));
        month_eth.setText(new DecimalFormat("#.####").format(curentStats.getCoinsPerHr()*24*30));
        month_btc.setText(new DecimalFormat("#.####").format(curentStats.getBtcPerMin()*24*30));
        month_usd.setText(new DecimalFormat("#.####").format(curentStats.getUsdPerMin()*24*30));
        eth_usd.setText(new DecimalFormat("#.#").format(curentStats.getPriceEthUsd()));
        eth_btc.setText(new DecimalFormat("#.####").format(curentStats.getPriceEthbtc()));
        btc_usd.setText(new DecimalFormat("#.#").format(curentStats.getPriceBtcUsd()));
        NextPayout();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetAPI extends AsyncTask<String, Void, Void> {
        boolean isNoData = false;
        boolean isParsingError = false;

        //int flag = -1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            //progressbar.setVisibility(View.VISIBLE);
            if(getListener() != null){
                getListener().showProgress();
            }
        }

        @Override
        protected Void doInBackground(String... url) {
            HttpHandler sh = new HttpHandler();
            final  Activity activity = getActivity();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url[0]);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    if(activity != null && jsonObj.get("data").equals(NO_DATA)){
                        isNoData = true;
                    }
                    else {
                        JSONObject data = jsonObj.getJSONObject("data");
                        long reported, current, average, unpaidBalance;
                        double coinsPerMin, usdPerMin, btcPerMin;
                        int workers, lastScreen, validShares, invalidShares, staleShares;
                        try {
                            reported = data.getLong("reportedHashrate");
                        } catch (JSONException e) {
                            reported = 0;
                        }
                        try {
                            current = data.getLong("currentHashrate");
                        } catch (JSONException e) {
                            current = 0;
                        }
                        try {
                            average = data.getLong("averageHashrate");
                        } catch (JSONException e) {
                            average = 0;
                        }
                        try {
                            unpaidBalance = data.getLong("unpaid");
                        } catch (JSONException e) {
                            unpaidBalance = 0;
                        }
                        try {
                            workers = data.getInt("activeWorkers");
                        } catch (JSONException e) {
                            workers = 0;
                        }
                        try {
                            lastScreen = data.getInt("lastSeen");
                        } catch (JSONException e) {
                            lastScreen = 0;
                        }
                        try {
                            validShares = data.getInt("validShares");
                        } catch (JSONException e) {
                            validShares = 0;
                        }
                        try {
                            invalidShares = data.getInt("invalidShares");
                        } catch (JSONException e) {
                            invalidShares = 0;
                        }
                        try {
                            staleShares = data.getInt("staleShares");
                        } catch (JSONException e) {
                            staleShares = 0;
                        }
                        try {
                            coinsPerMin = data.getDouble("coinsPerMin");
                        } catch (JSONException e) {
                            coinsPerMin = 0;
                        }
                        try {
                            usdPerMin = data.getDouble("usdPerMin");
                        } catch (JSONException e) {
                            usdPerMin = 0;
                        }
                        try {
                            btcPerMin = data.getDouble("btcPerMin");
                        } catch (JSONException e) {
                            btcPerMin = 0;
                        }

                        curentStats.setPriceEthbtc(btcPerMin / coinsPerMin);
                        curentStats.setPriceEthUsd(usdPerMin / coinsPerMin);
                        curentStats.setPriceBtcUsd(usdPerMin / btcPerMin);
                        curentStats.setReportedHashrate(reported);
                        curentStats.setCurrentHashrate(current);
                        curentStats.setAverageHashrate(average);
                        curentStats.setUnpaid(unpaidBalance);
                        curentStats.setActiveWorkers(workers);
                        curentStats.setLastSeen(lastScreen);
                        curentStats.setValidShares(validShares);
                        curentStats.setInvalidShares(invalidShares);
                        curentStats.setStaleShares(staleShares);
                        curentStats.setCoinsPerMin(coinsPerMin);
                        curentStats.setBtcPerMin(btcPerMin);
                        curentStats.setUsdPerMin(usdPerMin);
                    }
                } catch (final JSONException e) {
                    if(activity != null) {
                        isParsingError = true;
                    }
                }
            } else {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomApp.showToast("Couldn't get data from server!");
                        }
                    });
                }
                return null;
            }

            String jsonNetworkHistory = sh.makeServiceCall(url[1]);
            if (jsonNetworkHistory != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonNetworkHistory);
                    JSONArray historys = jsonObj.getJSONArray("data");
                    if(historys != null && historys.length() > 0) {
                        for (int i = 0; i < historys.length(); i++) {
                            int value = i + 1;
                            JSONObject valueJson = historys.getJSONObject(i);
                            double dbReportedHashrate;
                            try {
                                dbReportedHashrate = valueJson.getDouble("reportedHashrate");
                            } catch (JSONException e) {
                                dbReportedHashrate = 0;
                            }
                            //double dbconvertReportedHashrate = (double) dbReportedHashrate / 1000000;

                            double dbCurrentHashrate;
                            try {
                                dbCurrentHashrate = valueJson.getDouble("currentHashrate");
                            } catch (JSONException e) {
                                dbCurrentHashrate = 0;
                            }
                            //double dbconvertCurrentHashrate = (double) dbCurrentHashrate / 1000000;

                            double dbAverageHashrate;
                            try {
                                dbAverageHashrate = valueJson.getDouble("averageHashrate");
                            } catch (JSONException e) {
                                dbAverageHashrate = 0;
                            }
                            //double dbconvertAverageHashrate = (double) dbAverageHashrate / 1000000;

                            long dbWorker;
                            try {
                                dbWorker = (long) valueJson.getDouble("activeWorkers");
                            } catch (JSONException e) {
                                dbWorker = 0;
                            }
                            double dbValidShare;
                            try {
                                dbValidShare = valueJson.getDouble("validShares");
                            } catch (JSONException e) {
                                dbValidShare = 0;
                            }
                            double dbInValidShare;
                            try {
                                dbInValidShare = valueJson.getDouble("invalidShares");
                            } catch (JSONException e) {
                                dbInValidShare = 0;
                            }
                            double dbStaleShare;
                            try {
                                dbStaleShare = valueJson.getDouble("staleShares");
                            } catch (JSONException e) {
                                dbStaleShare = 0;
                            }

                            long time;
                            try {
                                time = valueJson.getLong("time");
                            } catch (JSONException e) {
                                time = 0;
                            }
                            timeHistory.put(i + 1, time);
                            float totalshares = (float) dbValidShare + (float) dbInValidShare + (float) dbStaleShare;
                            //float percentValid = ((float)dbValidShare*100) / totalshares;
                            float percentInValid = ((float) dbInValidShare * 100) / totalshares;
                            float percentStale = ((float) dbStaleShare * 100) / totalshares;
                            yValuesShares.add(new BarEntry(i, new float[]{percentStale, percentInValid}));
//                        yValueValidShares.add(new Entry(i, (float)dbValidShare));
//                        yValueInValidShares.add(new Entry(i, (float)dbInValidShare));
//                        yValueStaleShares.add(new Entry(i, (float)dbStaleShare));
                            yValueReportHashrate.add(new Entry(time, (float) ChangeHashrate(dbReportedHashrate)));
                            yValueCurrentHashRate.add(new Entry(time, (float) ChangeHashrate(dbCurrentHashrate)));
                            yValueAverageHashrate.add(new Entry(time, (float) ChangeHashrate(dbAverageHashrate)));
                            yValueWorker.add(new Entry(time, dbWorker));
                            yValueReportHashrateHighlight.add(new Entry(time, (float) dbReportedHashrate));
                            yValueCurrentHashRateHighlight.add(new Entry(time, (float) dbCurrentHashrate));
                            yValueAverageHashrateHighlight.add(new Entry(time, (float) dbAverageHashrate));
                            yValueWorkerHighlight.add(new Entry(time, dbWorker));
                        }
                    }
                    else {
                        isNoData = true;
                    }
                } catch (final JSONException e) {
                    isParsingError = true;
                }
            } else {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomApp.showToast("Couldn't get data from server!");
                        }
                    });
                }
                return null;
            }

            String jsonPayouts = sh.makeServiceCall(url[2]);
            if (jsonPayouts != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonPayouts);
                    JSONArray payoutsArray = jsonObj.getJSONArray("data");
                    if(payoutsArray != null && payoutsArray.length() > 0) {
                        JSONObject valueJson = payoutsArray.getJSONObject(0);
                        paidOn = valueJson.getLong("paidOn");
                    }
                    else {
                        isNoData = true;
                    }
                } catch (final JSONException e) {
                    isParsingError = true;
                }
            } else {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomApp.showToast("Couldn't get data from server!");
                        }
                    });
                }
                return null;
            }

            String jsonSettings = sh.makeServiceCall(url[3]);
            if (jsonSettings != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonSettings);
                    if(jsonObj.get("data").equals(NO_DATA)){
                        isNoData = true;
                    }
                    else {
                        JSONObject settings = jsonObj.getJSONObject("data");
                        minPayout = settings.getDouble("minPayout") / 1000000000;
                        minPayout = minPayout / 1000000000;
                    }
                } catch (final JSONException e) {
                    isParsingError = true;
                }
            } else {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomApp.showToast("Couldn't get data from server!");
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
            final Activity activity = getActivity();

            if(isNoData){
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomApp.showToast("No data");
                        }
                    });
                }
            }
            else if(isParsingError){
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomApp.showToast("Data error");
                        }
                    });
                }
            }

            if (isAdded() && activity != null) {
                SetView();
                // Draw line chart
                if(yValueAverageHashrate.size() > 0 && yValueReportHashrate.size() > 0 && yValueCurrentHashRate.size() > 0) {
                    lineChart.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.GONE);
                    DrawGraphHashRate(activity);
                }
            }
//            progressbar.setVisibility(View.GONE);
            if(getListener() != null){
                getListener().hideProgress();
            }
        }
    }

    public String ChangeHashrateUnit(long value){
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

    public double ChangeHashrate(double value){
        double dbTempValue = value;
        if(value / 1000 >= 1){
            double lTempHS = value / 1000;
            dbTempValue = lTempHS;
            if(lTempHS / 1000 >= 1){
                double lTempKH = lTempHS / 1000;
                dbTempValue = lTempKH;
                if(lTempKH / 1000 >= 1){
                    double lTempMH = lTempKH / 1000;
                    dbTempValue = lTempMH;
                    if(lTempMH / 1000 >= 1){
                        double lTempGH = lTempMH / 1000;
                        dbTempValue = lTempGH;
                    }
                }
            }
        }
        return dbTempValue;
    }

    private String GetDate(long time) {
        try {
            Date date = new java.util.Date(time*1000L);
            Calendar cal = Calendar.getInstance();
            TimeZone tz = cal.getTimeZone();//get your local time zone.
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM, hh:mm");
            sdf.setTimeZone(tz);//set time zone.
            String formattedDate = sdf.format(date);
            return formattedDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void NextPayout(){
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis() / 1000;
        double coinPerDay = curentStats.getCoinsPerHr()*24;
        double estimateDay = minPayout/coinPerDay;
        double resultSeconds = ((float)minPayout - (float)curentStats.getUnpaid()) / (float)curentStats.getCoinsPerHr()*3600;
        long dayGoal = (long)resultSeconds + now;

        if((dayGoal - paidOn) >  7 * 24 * 60 * 60){
            dayGoal = paidOn + 7 * 24 * 60 * 60 + 60 * 4;
            resultSeconds = dayGoal - now;
        }

        String strGoal = GetDate(dayGoal);
        int minute, hour, day;
        day = (int)resultSeconds / 86400;
        hour = ((int)resultSeconds % 86400) / 3600;
        minute = ((int)resultSeconds - (day*86400) - (hour * 3600))/60;
        String strNextPayout = "";
        if(day != 0 ){
            strNextPayout = day + "d ";
        }
        if(hour != 0){
            strNextPayout += hour + "h ";
        }
        if(minute != 0){
            strNextPayout += minute + "m";
        }
        double secondsEstimate = MinNumber(estimateDay, 7)*86400;
        double percent = 100-((resultSeconds*100)/secondsEstimate);
        float f = (float)percent/100;
//        ViewGroup.LayoutParams lp = percent_next_payout.getLayoutParams();
//        lp.height = ((View)percent_next_payout.getParent()).getHeight();
//        lp.width = (int) (((View)percent_next_payout.getParent()).getWidth() * f);
//        percent_next_payout.setLayoutParams(lp);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = f;
        percent_next_payout.setLayoutParams(lp);
        final double finalResultSeconds = resultSeconds;
        final String finalStrGoal = strGoal;
        final String finalStrNextPayout = strNextPayout;
        if(finalResultSeconds >= 0 && curentStats.getCoinsPerHr() > 0) {
            goal_day.setText(finalStrGoal);
            time_next_payout.setText(finalStrNextPayout);
        }
        else {
            goal_day.setText("");
            time_next_payout.setText("");
        }
        percent_next_payout.setVisibility(View.VISIBLE);
    }

    private double MinNumber(double numFirst, double numSecond){
        if( numFirst > numSecond){
            return numSecond;
        }
        else
            return numFirst;
    }

    class CurrentStats{

        int lastSeen, validShares,
                invalidShares, staleShares, activeWorkers;
        long unpaid, reportedHashrate, currentHashrate, averageHashrate;
        double coinsPerMin, usdPerMin, btcPerMin;
        double priceEthBtc, priceEthUsd, priceBtcUsd;

        public double getPriceEthbtc() {
            return priceEthBtc;
        }

        public void setPriceEthbtc(double priceEthbtc) {
            this.priceEthBtc = priceEthbtc;
        }

        public double getPriceEthUsd() {
            return priceEthUsd;
        }

        public void setPriceEthUsd(double priceEthUsd) {
            this.priceEthUsd = priceEthUsd;
        }

        public double getPriceBtcUsd() {
            return priceBtcUsd;
        }

        public void setPriceBtcUsd(double priceBtcUsd) {
            this.priceBtcUsd = priceBtcUsd;
        }

        public long getLastSeen() {
            if(lastSeen == 0){
                return 0;
            }
            Calendar calendar = Calendar.getInstance();
            long now = calendar.getTimeInMillis() / 1000;
            return (now - lastSeen)/60;
        }

        public void setLastSeen(int lastSeen) {
            this.lastSeen = lastSeen;
        }

        public String getReportedHashrate() {
            String strReportedHashrate = ChangeHashrateUnit(reportedHashrate);
            return strReportedHashrate;
        }

        public void setReportedHashrate(long reportedHashrate) {
            this.reportedHashrate = reportedHashrate;
        }

        public String getCurrentHashrate() {
            String strCurrentHashrate = ChangeHashrateUnit(currentHashrate);
            return strCurrentHashrate;
        }

        public void setCurrentHashrate(long currentHashrate) {
            this.currentHashrate = currentHashrate;
        }

        public String getAverageHashrate() {
            String strAverageHashrate = ChangeHashrateUnit(averageHashrate);
            return strAverageHashrate;
        }

        public void setAverageHashrate(long averageHashrate) {
            this.averageHashrate = averageHashrate;
        }

        public int getValidShares() {
            return validShares;
        }

        public void setValidShares(int validShares) {
            this.validShares = validShares;
        }

        public int getInvalidShares() {
            return invalidShares;
        }

        public void setInvalidShares(int invalidShares) {
            this.invalidShares = invalidShares;
        }

        public int getStaleShares() {
            return staleShares;
        }

        public void setStaleShares(int staleShares) {
            this.staleShares = staleShares;
        }

        public int getActiveWorkers() {
            return activeWorkers;
        }

        public void setActiveWorkers(int activeWorkers) {
            this.activeWorkers = activeWorkers;
        }

        public double getUnpaid() {
            double dbconvertUnpaidBalance = (double) unpaid / 100000000;
            dbconvertUnpaidBalance = dbconvertUnpaidBalance/ 100000000;
            dbconvertUnpaidBalance = dbconvertUnpaidBalance / 100;
//            String strAverageHashrate = new DecimalFormat("#.#####").format(dbconvertUnpaidBalance);
            return dbconvertUnpaidBalance;
        }

        public void setUnpaid(long unpaid) {
            this.unpaid = unpaid;
        }

        public double getCoinsPerHr() {
            double hour = coinsPerMin * 60;
            return hour;
        }

        public void setCoinsPerMin(double coinsPerMin) {
            this.coinsPerMin = coinsPerMin;
        }

        public double getUsdPerMin() {
            double hour = usdPerMin * 60;
            return hour;
        }

        public void setUsdPerMin(double usdPerMin) {
            this.usdPerMin = usdPerMin;
        }

        public double getBtcPerMin() {
            double hour = btcPerMin * 60;
            return hour;
        }

        public void setBtcPerMin(double btcPerMin) {
            this.btcPerMin = btcPerMin;
        }
    }

    public interface ProgressDisplay {

        void showProgress();

        void hideProgress();

        void TabMinerSelected();
    }
}

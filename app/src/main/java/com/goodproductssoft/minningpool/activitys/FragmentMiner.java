package com.goodproductssoft.minningpool.activitys;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.goodproductssoft.minningpool.HttpHandler;
import com.goodproductssoft.minningpool.MyPreferences;
import com.goodproductssoft.minningpool.R;
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
            title_coin_btc, title_coin_usd, id_miner_first, id_miner_second;
    LinearLayout percent_next_payout, show_chart;
    private CurrentStats curentStats;
    private NetworkStats networkStats;
    private LineChart lineChart;
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

    ProgressDisplay getListener(){
        if(getActivity()!= null && getActivity() instanceof ProgressDisplay){
            return (ProgressDisplay) getActivity();
        }
        return null;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_miner, viewGroup, false);
        reported_hashrate = (TextView) view.findViewById(R.id.reported_hashrate);
        current_hashrate = (TextView) view.findViewById(R.id.current_hashrate);
        average_hashrate = (TextView) view.findViewById(R.id.average_hashrate);
        unpaid_balance = (TextView) view.findViewById(R.id.unpaid_balance);
        active_workers = (TextView) view.findViewById(R.id.active_workers);
        valid_shares = (TextView) view.findViewById(R.id.valid_shares);
        invalid_shares = (TextView) view.findViewById(R.id.invalid_shares);
        stale_shares = (TextView) view.findViewById(R.id.stale_shares);
        last_screen = (TextView) view.findViewById(R.id.last_screen);
        hour_eth = (TextView) view.findViewById(R.id.hour_eth);
        hour_usd = (TextView) view.findViewById(R.id.hour_usd);
        hour_btc = (TextView) view.findViewById(R.id.hour_btc);
        day_eth = (TextView) view.findViewById(R.id.day_eth);
        day_usd = (TextView) view.findViewById(R.id.day_usd);
        day_btc = (TextView) view.findViewById(R.id.day_btc);
        week_eth = (TextView) view.findViewById(R.id.week_eth);
        week_usd = (TextView) view.findViewById(R.id.week_usd);
        week_btc = (TextView) view.findViewById(R.id.week_btc);
        month_eth = (TextView) view.findViewById(R.id.month_eth);
        month_usd = (TextView) view.findViewById(R.id.month_usd);
        month_btc = (TextView) view.findViewById(R.id.month_btc);
        eth_btc = (TextView) view.findViewById(R.id.eth_btc);
        eth_usd = (TextView) view.findViewById(R.id.eth_usd);
        btc_usd = (TextView) view.findViewById(R.id.btc_usd);
        lineChart = (LineChart) view.findViewById(R.id.line_chart);
        barChart = (BarChart) view.findViewById(R.id.bar_chart);
        chart_hashrate = (TextView) view.findViewById(R.id.chart_hashrate);
        chart_shares = (TextView) view.findViewById(R.id.chart_shares);
        time_next_payout = (TextView) view.findViewById(R.id.time_next_payout);
        percent_next_payout = (LinearLayout) view.findViewById(R.id.percent_next_payout);
        goal_day = (TextView) view.findViewById(R.id.goal_day);
        unit_reported = (TextView) view.findViewById(R.id.unit_reported);
        unit_current = (TextView) view.findViewById(R.id.unit_current);
        unit_average = (TextView)view.findViewById(R.id.unit_average);
        title_unpaid = (TextView) view.findViewById(R.id.title_unpaid);
        title_coin = (TextView)view.findViewById(R.id.title_coin);
        title_coin_btc = (TextView) view.findViewById(R.id.title_coin_btc);
        title_coin_usd = (TextView) view.findViewById(R.id.title_coin_usd);
        id_miner_first = (TextView)view.findViewById(R.id.id_miner_first);
        id_miner_second = (TextView) view.findViewById(R.id.id_miner_second);
        show_chart = (LinearLayout) view.findViewById(R.id.show_chart);

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

        myPreferences = new MyPreferences();
        if(getActivity()!= null) {
            miners = myPreferences.GetIdMiners(getActivity());
        }

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
            new GetAPI().execute(urlData);
//            String urlNetWorkStats = miner.getEndpoint() + "/networkStats";
//            new GetNetWorkStats().execute(urlNetWorkStats);
        }
        return view;
    }

    private Miner GetMinerIdActive(){
        if(getActivity()!= null) {
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
                return ConvertTimestampToTime((long)timeHistory.get((int)value + 1));
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
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity,
                                        "No data",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
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
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity,
                                        "Data parsing error",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }
                }
            } else {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,
                                    "Couldn't get data from server!",
                                    Toast.LENGTH_LONG)
                                    .show();
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
                        if(activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity,
                                            "No data",
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        }
                    }
                } catch (final JSONException e) {
                    if(activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity,
                                        "Data parsing error",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }
                }
            } else {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,
                                    "Couldn't get data from server!",
                                    Toast.LENGTH_LONG)
                                    .show();
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
                        if(activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity,
                                            "No data",
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        }
                    }
                } catch (final JSONException e) {
                    if(activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity,
                                        "Data parsing error",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }
                }
            } else {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,
                                    "Couldn't get data from server!",
                                    Toast.LENGTH_LONG)
                                    .show();
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
                        if(activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity,
                                            "No data",
                                            Toast.LENGTH_LONG)
                                            .show();
                                }
                            });
                        }
                    }
                    else {
                        JSONObject settings = jsonObj.getJSONObject("data");
                        minPayout = settings.getDouble("minPayout") / 1000000000;
                        minPayout = minPayout / 1000000000;
                    }
                } catch (final JSONException e) {
                    if(activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity,
                                        "Data parsing error",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }
                }
            } else {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,
                                    "Couldn't get data from server!",
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
            Activity activity = getActivity();
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
            dayGoal = paidOn + 7 * 24 * 60 * 60 + 60 * 2;
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
        if(resultSeconds >= 0) {
            goal_day.setText(strGoal);
            time_next_payout.setText(strNextPayout);
        }
        else {
            goal_day.setText("");
            time_next_payout.setText("");
        }
        double secondsEstimate = MinNumber(estimateDay, 7)*86400;
        double percent = 100-((resultSeconds*100)/secondsEstimate);
        float f = (float)percent/100;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = f;
        percent_next_payout.setLayoutParams(lp);
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
//            double dbconvertReportedHashrate = (double) reportedHashrate / 1000000;
//            if(checkAccount == -1){
//                dbconvertReportedHashrate /= 1000;
//            }
//            String strReportedHashrate = new DecimalFormat("#.##").format(dbconvertReportedHashrate);
            String strReportedHashrate = ChangeHashrateUnit(reportedHashrate);
            return strReportedHashrate;
        }

        public void setReportedHashrate(long reportedHashrate) {
            this.reportedHashrate = reportedHashrate;
        }

        public String getCurrentHashrate() {
//            double dbconvertCurrentHashrate = (double) currentHashrate / 1000000;
//            if(checkAccount == -1){
//                dbconvertCurrentHashrate /= 1000;
//            }
//            String strCurrentHashrate = new DecimalFormat("#.##").format(dbconvertCurrentHashrate);
            String strCurrentHashrate = ChangeHashrateUnit(currentHashrate);
            return strCurrentHashrate;
        }

        public void setCurrentHashrate(long currentHashrate) {
            this.currentHashrate = currentHashrate;
        }

        public String getAverageHashrate() {
//            double dbconvertAverageHashrate = (double) averageHashrate / 1000000;
//            if(checkAccount == -1){
//                dbconvertAverageHashrate /= 1000;
//            }
//            String strAverageHashrate = new DecimalFormat("#.##").format(dbconvertAverageHashrate);
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

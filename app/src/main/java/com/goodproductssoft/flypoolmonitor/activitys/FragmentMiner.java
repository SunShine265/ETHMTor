package com.goodproductssoft.flypoolmonitor.activitys;

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
import com.goodproductssoft.flypoolmonitor.HttpHandler;
import com.goodproductssoft.flypoolmonitor.MyPreferences;
import com.goodproductssoft.flypoolmonitor.R;
import com.goodproductssoft.flypoolmonitor.controls.CustomMarkerView;
import com.goodproductssoft.flypoolmonitor.models.Miner;
import com.goodproductssoft.flypoolmonitor.models.NetworkStats;

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
    private TextView immature_hashrate, current_hashrate, average_hashrate, unpaid_balance,
            active_workers, valid_shares, invalid_shares, stale_shares, last_screen, hour_eth,
            hour_usd, hour_btc, day_eth, day_usd, day_btc, week_eth, week_usd, week_btc, month_eth,
            month_usd, month_btc, eth_btc, eth_usd, chart_hashrate, chart_shares, time_next_payout,
            btc_usd, goal_day, title_unpaid, title_coin,
            title_coin_btc, title_coin_usd;
    LinearLayout percent_next_payout;
    private LineChart lineChart;
    private BarChart bar_chart;

    private CurrentStats curentStats;
    private NetworkStats networkStats;
    ArrayList<Entry> yValueCurrentHashRate, yValueReportHashrate, yValueAverageHashrate,
            yValueValidShares, yValueInValidShares, yValueStaleShares, yValueCurrentHashrateHighlight,
            yValueAverageHashrateHighlight;
    ArrayList<BarEntry> yValuesShares;
    long paidOn;
    double minPayout;
    MyPreferences myPreferences;
    Miner miner;
    HashMap<Integer, Long> timeHistory;

    ProgressDisplay getListener(){
        if(getActivity() instanceof ProgressDisplay){
            return (ProgressDisplay) getActivity();
        }
        return null;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_miner, viewGroup, false);
//        button_mining_pool = (Button)view.findViewById(R.id.button_mining_pool);
//        edt_id_miner = (EditText) view.findViewById(R.id.edt_id_miner);
        immature_hashrate = (TextView) view.findViewById(R.id.immature_hashrate);
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
        bar_chart = (BarChart) view.findViewById(R.id.bar_chart);
        chart_hashrate = (TextView) view.findViewById(R.id.chart_hashrate);
        chart_shares = (TextView) view.findViewById(R.id.chart_shares);
        time_next_payout = (TextView) view.findViewById(R.id.time_next_payout);
        percent_next_payout = (LinearLayout) view.findViewById(R.id.percent_next_payout);
        goal_day = (TextView) view.findViewById(R.id.goal_day);
//        unit_reported = (TextView) view.findViewById(R.id.unit_reported);
//        unit_current = (TextView) view.findViewById(R.id.unit_current);
//        unit_average = (TextView)view.findViewById(R.id.unit_average);
        title_unpaid = (TextView) view.findViewById(R.id.title_unpaid);
        title_coin = (TextView)view.findViewById(R.id.title_coin);
        title_coin_btc = (TextView) view.findViewById(R.id.title_coin_btc);
        title_coin_usd = (TextView) view.findViewById(R.id.title_coin_usd);

        curentStats = new CurrentStats();
        networkStats = new NetworkStats();
        yValueAverageHashrate = new ArrayList<>();
        yValueCurrentHashRate = new ArrayList<>();
        yValueReportHashrate = new ArrayList<>();
        yValueValidShares = new ArrayList<>();
        yValueInValidShares = new ArrayList<>();
        yValueStaleShares = new ArrayList<>();
        yValuesShares = new ArrayList<>();
        timeHistory = new HashMap<>();
        yValueCurrentHashrateHighlight = new ArrayList<>();
        yValueAverageHashrateHighlight = new ArrayList<>();

        myPreferences = new MyPreferences();
        chart_hashrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart_hashrate.setBackgroundResource(R.color.background_selected_chart);
                chart_shares.setBackgroundResource(R.color.background_unselected_chart);
                lineChart.setVisibility(View.VISIBLE);
                bar_chart.setVisibility(View.GONE);
                if(yValueAverageHashrate.size() > 0 && yValueCurrentHashRate.size() > 0 && yValueReportHashrate.size() > 0){
                    DrawGraphHashRate();
                }
            }
        });
        chart_shares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chart_hashrate.setBackgroundResource(R.color.background_unselected_chart);
                chart_shares.setBackgroundResource(R.color.background_selected_chart);
                lineChart.setVisibility(View.GONE);
                bar_chart.setVisibility(View.VISIBLE);
                if(yValuesShares.size() > 0){
                    DrawGraphShares();
                }
            }
        });
        miner = GetMinerIdActive();
        if(miner != null) {
            urlData = new String[5];
            urlData[0] = miner.getEndpoint() + "/networkStats";
            urlData[1] = miner.getEndpoint() + "/miner/" + miner.getId() + "/currentStats";
            urlData[2] = miner.getEndpoint() + "/miner/" + miner.getId() + "/history";
            urlData[3] = miner.getEndpoint() + "/miner/" + miner.getId() + "/payouts";
            urlData[4] = miner.getEndpoint() + "/miner/" + miner.getId() + "/Settings";
            new GetAPI().execute(urlData);
        }
        return view;
    }

    private Miner GetMinerIdActive(){
        ArrayList<Miner> miners = myPreferences.GetIdMiners(getActivity());
        if(miners != null && !miners.isEmpty()) {
            for (int i = 0; i < miners.size(); i++) {
                if (miners.get(i).isActive()) {
                    return miners.get(i);
                }
            }
        }
        return null;
    }

    private void DrawGraphHashRate(){
        lineChart.removeAllViews();
        lineChart.setDragEnabled(true);
        lineChart.getDescription().setEnabled(false);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        lineChart.setScaleEnabled(false);

        YAxis axisRight = lineChart.getAxisRight();
        axisRight.setGranularity(1f);
        axisRight.setDrawGridLines(false);

//        lineChart.getXAxis().setTextColor(Color.TRANSPARENT);
        //lineChart.getXAxis().setAxisMaximum(72);
        lineChart.getAxisRight().setTextColor(Color.WHITE);
        lineChart.getLegend().setTextColor(Color.WHITE);

        LineDataSet lineCurrent = new LineDataSet(yValueCurrentHashRate, "Current Hashrate");
        lineCurrent.setFillAlpha(2);
        lineCurrent.setColor(getResources().getColor(R.color.color_txt_current_hashrate));
        lineCurrent.setCircleRadius(1);
        lineCurrent.setCircleColor(getResources().getColor(R.color.color_txt_current_hashrate));
        lineCurrent.setLineWidth(0.3f);
        lineCurrent.setValueTextColor(Color.TRANSPARENT);

        LineDataSet lineAverage = new LineDataSet(yValueAverageHashrate, "Average Hashrate");
        lineAverage.setFillAlpha(2);
        lineAverage.setColor(getResources().getColor(R.color.color_txt_average_hashrate));
        lineAverage.setCircleRadius(1);
        lineAverage.setCircleColor(getResources().getColor(R.color.color_txt_average_hashrate));
        lineAverage.setLineWidth(0.3f);
        lineAverage.setValueTextColor(Color.TRANSPARENT);

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
//        dataSets.add(lineReported);
        dataSets.add(lineCurrent);
        dataSets.add(lineAverage);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);
//        lineChart.getData().setHighlightEnabled(false);
        lineChart.invalidate();

        if(getActivity() != null) {
            lineChart.setDrawMarkers(true);
            CustomMarkerView customMarkerView = new CustomMarkerView(getActivity(),
                    R.layout.custom_marker_view_layout, yValueCurrentHashrateHighlight,
                    yValueAverageHashrateHighlight);
            lineChart.setMarker(customMarkerView);
        }
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
        bar_chart.removeAllViews();
        bar_chart.setDragEnabled(true);
        bar_chart.getDescription().setEnabled(false);
        bar_chart.setScaleEnabled(false);
        bar_chart.setSelected(false);

        YAxis leftAxis = bar_chart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        bar_chart.getXAxis().setTextColor(Color.TRANSPARENT);
        bar_chart.getAxisRight().setTextColor(Color.TRANSPARENT);
        bar_chart.getLegend().setTextColor(Color.WHITE);

        BarDataSet barShares;
        barShares = new BarDataSet(yValuesShares, "");
        barShares.setDrawIcons(false);
        barShares.setStackLabels(new String[]{"Valid Shares", "Invalid Shares", "Stale Shares"});
        barShares.setColors(getColors());

        IAxisValueFormatter xAxisFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return new DecimalFormat("###.#").format(value) + "%";
            }
        };
        leftAxis.setValueFormatter(xAxisFormatter);


        XAxis xAxis = bar_chart.getXAxis();
        xAxis.setTextColor(getResources().getColor(R.color.colorWhite));
//        barChart.getXAxis().setAxisMinimum(timeHistory.get(1));
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return ConvertTimestampToTime((long)timeHistory.get((int)value + 1));
            }
        });
        long detalTime = timeHistory.size() / 8;
        bar_chart.getXAxis().setGranularity(detalTime);
        xAxis.setEnabled(true);
        bar_chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        BarData data = new BarData(barShares);
        bar_chart.setData(data);
        bar_chart.setFitBars(true);
        bar_chart.getData().setHighlightEnabled(false);
        bar_chart.invalidate();
    }

    private int[] getColors() {
        int[] colors = new int[2];
        colors[0] = getResources().getColor(R.color.invalid);
        colors[1] = getResources().getColor(R.color.color_txt_average_hashrate);
        return colors;
    }

//    private void DrawGraphShares(){
//        lineChart.removeAllViews();
//        lineChart.setDragEnabled(true);
//        lineChart.getDescription().setEnabled(false);
//
//        YAxis leftAxis = lineChart.getAxisLeft();
//        leftAxis.setTextColor(Color.WHITE);
//        lineChart.setScaleEnabled(false);
//
//        lineChart.getXAxis().setTextColor(Color.TRANSPARENT);
////        lineChart.getXAxis().setAxisMaximum(72);
//        lineChart.getAxisRight().setTextColor(Color.WHITE);
//        lineChart.getLegend().setTextColor(Color.WHITE);
//
//        LineDataSet lineReported = new LineDataSet(yValueValidShares, "Valid Shares");
//        lineReported.setFillAlpha(2);
//        lineReported.setColor(Color.GREEN);
//        lineReported.setCircleRadius(1);
//        lineReported.setCircleColor(Color.GREEN);
//        lineReported.setLineWidth(0.3f);
//        lineReported.setValueTextColor(Color.TRANSPARENT);
//
//        LineDataSet lineCurrent = new LineDataSet(yValueInValidShares, "InValid Shares");
//        lineCurrent.setFillAlpha(2);
//        lineCurrent.setColor(getResources().getColor(R.color.invalid));
//        lineCurrent.setCircleRadius(1);
//        lineCurrent.setCircleColor(getResources().getColor(R.color.invalid));
//        lineCurrent.setLineWidth(0.3f);
//        lineCurrent.setValueTextColor(Color.TRANSPARENT);
//
//        LineDataSet lineAverage = new LineDataSet(yValueStaleShares, "Stale Shares");
//        lineAverage.setFillAlpha(2);
//        lineAverage.setColor(getResources().getColor(R.color.color_txt_average_hashrate));
//        lineAverage.setCircleRadius(1);
//        lineAverage.setCircleColor(getResources().getColor(R.color.color_txt_average_hashrate));
//        lineAverage.setLineWidth(0.3f);
//        lineAverage.setValueTextColor(Color.TRANSPARENT);
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(lineReported);
//        dataSets.add(lineCurrent);
//        dataSets.add(lineAverage);
//        LineData data = new LineData(dataSets);
//        lineChart.setData(data);
//        lineChart.getData().setHighlightEnabled(false);
//        lineChart.invalidate();
//    }

    private void SetView(){
        eth_usd.setText(new DecimalFormat("#.#").format(networkStats.getEthUsd()));
        eth_btc.setText(new DecimalFormat("#.####").format(networkStats.getEthBtc()));
        btc_usd.setText(new DecimalFormat("#.#").format(networkStats.getEthUsd()/networkStats.getEthBtc()));
        immature_hashrate.setText(curentStats.getReportedHashrate());
        current_hashrate.setText(curentStats.getCurrentHashrate());
        average_hashrate.setText(curentStats.getAverageHashrate());
        unpaid_balance.setText(new DecimalFormat("#.#####").format(curentStats.getUnpaid()));
        active_workers.setText(String.valueOf(curentStats.getActiveWorkers()));
//        valid_shares.setText(String.valueOf(curentStats.getValidShares()));
//        invalid_shares.setText(String.valueOf(curentStats.getInvalidShares()));
//        stale_shares.setText(String.valueOf(curentStats.getStaleShares()));
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
        month_btc.setText(new DecimalFormat("#.##").format(curentStats.getBtcPerMin()*24*30));
        month_usd.setText(new DecimalFormat("#.####").format(curentStats.getUsdPerMin()*24*30));
        NextPayout();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetAPI extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            if(getListener() != null){
                getListener().showProgress();
            }
        }

        @Override
        protected Void doInBackground(String... url) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url[1]);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONObject data = jsonObj.getJSONObject("data");
                    long reported, current, average, unpaidBalance;
                    double coinsPerMin, usdPerMin, btcPerMin;
                    int workers, lastScreen, validShares, invalidShares, staleShares;
                    try {
                        reported = data.getLong("unconfirmed");
                    }catch (JSONException e){
                        reported = 0;
                    }
                    try{
                        current = data.getLong("currentHashrate");
                    }catch (JSONException e){
                        current = 0;
                    }
                    try{
                        average = data.getLong("averageHashrate");
                    }catch (JSONException e){
                        average =0;
                    }
                    try{
                        unpaidBalance = data.getLong("unpaid");
                    }catch (JSONException e){
                        unpaidBalance = 0;
                    }
                    try{
                        workers = data.getInt("activeWorkers");
                    }catch (JSONException e){
                        workers = 0;
                    }
                    try{
                        lastScreen = data.getInt("lastSeen");
                    }catch (JSONException e){
                        lastScreen = 0;
                    }
                    try{
                        validShares = data.getInt("validShares");
                    }catch (JSONException e){
                        validShares = 0;
                    }
                    try{
                        invalidShares = data.getInt("invalidShares");
                    }catch (JSONException e){
                        invalidShares = 0;
                    }
                    try{
                        staleShares = data.getInt("staleShares");
                    }catch (JSONException e){
                        staleShares = 0;
                    }
                    try{
                        coinsPerMin = data.getDouble("coinsPerMin");
                    }catch (JSONException e){
                        coinsPerMin = 0;
                    }
                    try{
                        usdPerMin = data.getDouble("usdPerMin");
                    }catch (JSONException e){
                        usdPerMin = 0;
                    }
                    try{
                        btcPerMin = data.getDouble("btcPerMin");
                    }catch (JSONException e){
                        btcPerMin = 0;
                    }

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

                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FragmentMiner.this.getActivity(),
                                    "Data parsing error: ",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FragmentMiner.this.getActivity(),
                                "Couldn't get data from server!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
                return null;
            }
            String jsonNetworkStats = sh.makeServiceCall(url[0]);
            if (jsonNetworkStats != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonNetworkStats);
                    JSONObject data = jsonObj.getJSONObject("data");
                    double ethUsd = data.getDouble("usd");
                    double ethBtc = data.getDouble("btc");
                    networkStats.setEthBtc(ethBtc);
                    networkStats.setEthUsd(ethUsd);
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FragmentMiner.this.getActivity(),
                                    "Data parsing error",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FragmentMiner.this.getActivity(),
                                "Couldn't get data from server!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
                return null;
            }

            String jsonNetworkHistory = sh.makeServiceCall(url[2]);
            if (jsonNetworkHistory != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonNetworkHistory);
                    JSONArray historys = jsonObj.getJSONArray("data");
                    for(int i = 0; i < historys.length(); i++){
                        int value = i + 1;
                        JSONObject valueJson = historys.getJSONObject(i);
                        double dbReportedHashrate;
                        try{
                            dbReportedHashrate = valueJson.getDouble("reportedHashrate");
                        }catch (JSONException e){
                            dbReportedHashrate = 0;
                        }
//                        double dbconvertReportedHashrate = (double) dbReportedHashrate / 1000000;

                        double dbCurrentHashrate;
                        try{
                            dbCurrentHashrate = valueJson.getDouble("currentHashrate");
                        }catch (JSONException e){
                            dbCurrentHashrate = 0;
                        }
//                        double dbconvertCurrentHashrate = (double) dbCurrentHashrate / 1000000;

                        double dbAverageHashrate;
                        try{
                            dbAverageHashrate = valueJson.getDouble("averageHashrate");
                        }catch (JSONException e){
                            dbAverageHashrate = 0;
                        }
//                        double dbconvertAverageHashrate = (double) dbAverageHashrate / 1000000;

                        double dbValidShare;
                        try{
                            dbValidShare = valueJson.getDouble("validShares");
                        }catch (JSONException e){
                            dbValidShare = 0;
                        }
                        double dbInValidShare;
                        try{
                            dbInValidShare = valueJson.getDouble("invalidShares");
                        }catch (JSONException e){
                            dbInValidShare = 0;
                        }
                        double dbStaleShare;
                        try{
                            dbStaleShare = valueJson.getDouble("staleShares");
                        }catch (JSONException e){
                            dbStaleShare = 0;
                        }
                        long time;
                        try{
                            time = valueJson.getLong("time");
                        }catch (JSONException e){
                            time =0;
                        }
                        timeHistory.put(i+1, time);

//                        yValueValidShares.add(new Entry(i, (float)dbValidShare));
//                        yValueInValidShares.add(new Entry(i, (float)dbInValidShare));
//                        yValueStaleShares.add(new Entry(i, (float)dbStaleShare));
                        float totalshares = (float)dbValidShare + (float)dbInValidShare + (float)dbStaleShare;
                        //float percentValid = ((float)dbValidShare*100) / totalshares;
                        float percentInValid = ((float)dbInValidShare*100) / totalshares;
                        float percentStale = ((float)dbStaleShare*100) / totalshares;
                        yValuesShares.add(new BarEntry(i, new float[]{percentInValid, percentStale}));
                        yValueReportHashrate.add(new Entry(time, (float) ChangeHashrate(dbReportedHashrate)));
                        yValueCurrentHashRate.add(new Entry(time, (float) ChangeHashrate(dbCurrentHashrate)));
                        yValueAverageHashrate.add(new Entry(time, (float) ChangeHashrate(dbAverageHashrate)));
                        yValueCurrentHashrateHighlight.add(new Entry(time, (float)dbCurrentHashrate));
                        yValueAverageHashrateHighlight.add(new Entry(time, (float)dbAverageHashrate));
                    }
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FragmentMiner.this.getActivity(),
                                    "Data parsing error",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FragmentMiner.this.getActivity(),
                                "Couldn't get data from server!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
                return null;
            }

            String jsonPayouts = sh.makeServiceCall(url[3]);
            if (jsonPayouts != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonPayouts);
                    JSONArray payoutsArray = jsonObj.getJSONArray("data");
                    JSONObject valueJson = payoutsArray.getJSONObject(0);
                    paidOn = valueJson.getLong("paidOn");
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FragmentMiner.this.getActivity(),
                                    "Data parsing error",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FragmentMiner.this.getActivity(),
                                "Couldn't get data from server!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
                return null;
            }

            String jsonSettings = sh.makeServiceCall(url[4]);
            if (jsonSettings != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonSettings);
                    JSONObject settings = jsonObj.getJSONObject("data");
                    minPayout = settings.getDouble("minPayout") / 100000000;
                } catch (final JSONException e) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(FragmentMiner.this.getActivity(),
                                    "Data parsing error",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FragmentMiner.this.getActivity(),
                                "Couldn't get data from server!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
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
                    bar_chart.setVisibility(View.GONE);
                    DrawGraphHashRate();
                }
            }
            if(getListener() != null){
                getListener().hideProgress();
            }
        }
    }

    private String getDate(long time) {
        try {
            Date date = new Date(time*1000L);
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

    private void NextPayout(){
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis() / 1000;
        double coinPerDay = curentStats.getCoinsPerHr()*24;
        double estimateDay = minPayout/coinPerDay;
//        double dayProgress = (paidOn + MinNumber(estimateDay, 7) * 86400 );
        double resultSeconds = ((float)minPayout - (float)curentStats.getUnpaid()) / (float)curentStats.getCoinsPerHr()*3600;
        long dayGoal = (long)resultSeconds + now;
        if((dayGoal - paidOn) >  7 * 24 * 60 * 60){
            dayGoal = paidOn + 7 * 24 * 60 * 60 + 60 * 2;
            resultSeconds = dayGoal - now;
        }
        String strGoal = getDate(dayGoal);
        goal_day.setText(strGoal);
//        double resultSeconds = abs(dayProgress - now);
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
        time_next_payout.setText(strNextPayout);
        double secondsEntimate = MinNumber(estimateDay, 7)*86400;
        double percent = 100-((resultSeconds*100)/secondsEntimate);
        float f = (float)percent/100;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = f;
        percent_next_payout.setLayoutParams(lp);
    }

    private double MinNumber(double numFirst, double numSecond){
        if( numFirst > numSecond){
            return numSecond;
        }
        return numFirst;
    }

    class CurrentStats{

        int lastSeen, validShares,
                invalidShares, staleShares, activeWorkers;
        long unpaid, reportedHashrate, currentHashrate, averageHashrate;
        double coinsPerMin, usdPerMin, btcPerMin;

        public long getLastSeen() {
            Calendar calendar = Calendar.getInstance();
            long now = calendar.getTimeInMillis() / 1000;
            return (now - lastSeen)/60;
        }

        public void setLastSeen(int lastSeen) {
            this.lastSeen = lastSeen;
        }

        public String getReportedHashrate() {
//            double dbconvertReportedHashrate = (double) reportedHashrate / 100000000;
//
//            String strReportedHashrate = new DecimalFormat("#.########").format(dbconvertReportedHashrate);
            String strReportedHashrate = ChangeHashrateUnit(reportedHashrate);
            return strReportedHashrate;
        }

        public void setReportedHashrate(long reportedHashrate) {
            this.reportedHashrate = reportedHashrate;
        }

        public String getCurrentHashrate() {
//            double dbconvertCurrentHashrate = (double) currentHashrate / 1000;
//
//            String strCurrentHashrate = new DecimalFormat("#.##").format(dbconvertCurrentHashrate);
            String strCurrentHashrate = ChangeHashrateUnit(currentHashrate);
            return strCurrentHashrate;
        }

        public void setCurrentHashrate(long currentHashrate) {
            this.currentHashrate = currentHashrate;
        }

        public String getAverageHashrate() {
//            double dbconvertAverageHashrate = (double) averageHashrate / 1000;
//
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
    }
}

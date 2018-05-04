//package com.goodproductssoft.flypoolmonitor.adapters;
//
//import android.app.Activity;
//import android.graphics.Color;
//import android.os.AsyncTask;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.github.mikephil.charting.charts.LineChart;
//import com.github.mikephil.charting.components.YAxis;
//import com.github.mikephil.charting.data.Entry;
//import com.github.mikephil.charting.data.LineData;
//import com.github.mikephil.charting.data.LineDataSet;
//import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
//import com.goodproductssoft.flypoolmonitor.HttpHandler;
//import com.goodproductssoft.flypoolmonitor.MyPreferences;
//import com.goodproductssoft.flypoolmonitor.R;
//import com.goodproductssoft.flypoolmonitor.models.Miner;
//import com.goodproductssoft.flypoolmonitor.models.NetworkStats;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.TimeZone;
//
//import static java.lang.Math.abs;
//
///**
// * Created by user on 4/23/2018.
// */
//
//public class MinerItemAdapter extends ArrayAdapter<Miner> {
//    ArrayList<Miner> miners;
//    Activity activity;
//    private TextView immature_hashrate, current_hashrate, average_hashrate, unpaid_balance,
//            active_workers, valid_shares, invalid_shares, stale_shares, last_screen, hour_eth,
//            hour_usd, hour_btc, day_eth, day_usd, day_btc, week_eth, week_usd, week_btc, month_eth,
//            month_usd, month_btc, eth_btc, eth_usd, chart_hashrate, chart_shares, time_next_payout,
//            btc_usd, goal_day, unit_reported, unit_current, unit_average, title_unpaid, title_coin,
//            title_coin_btc, title_coin_usd;
//    LinearLayout percent_next_payout;
//    private LineChart lineChart;
//
//    private CurrentStats curentStats;
//    private NetworkStats networkStats;
//    ArrayList<Entry> yValueCurrentHashRate, yValueReportHashrate, yValueAverageHashrate,
//            yValueValidShares, yValueInValidShares, yValueStaleShares;
//    long paidOn;
//    double minPayout;
//    MyPreferences myPreferences;
//    private static String[] urlData;
//
//    public MinerItemAdapter(@NonNull Activity activity, ArrayList<Miner> minerArrayAdapter) {
//        super(activity, 0, minerArrayAdapter);
//        this.activity = activity;
//        this.miners = minerArrayAdapter;
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        Miner miner = getItem(position);
//        if(convertView != null){
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_miner, parent, false);
//        }
//        immature_hashrate = (TextView) convertView.findViewById(R.id.immature_hashrate);
//        current_hashrate = (TextView) convertView.findViewById(R.id.current_hashrate);
//        average_hashrate = (TextView) convertView.findViewById(R.id.average_hashrate);
//        unpaid_balance = (TextView) convertView.findViewById(R.id.unpaid_balance);
//        active_workers = (TextView) convertView.findViewById(R.id.active_workers);
//        valid_shares = (TextView) convertView.findViewById(R.id.valid_shares);
//        invalid_shares = (TextView) convertView.findViewById(R.id.invalid_shares);
//        stale_shares = (TextView) convertView.findViewById(R.id.stale_shares);
//        last_screen = (TextView) convertView.findViewById(R.id.last_screen);
//        hour_eth = (TextView) convertView.findViewById(R.id.hour_eth);
//        hour_usd = (TextView) convertView.findViewById(R.id.hour_usd);
//        hour_btc = (TextView) convertView.findViewById(R.id.hour_btc);
//        day_eth = (TextView) convertView.findViewById(R.id.day_eth);
//        day_usd = (TextView) convertView.findViewById(R.id.day_usd);
//        day_btc = (TextView) convertView.findViewById(R.id.day_btc);
//        week_eth = (TextView) convertView.findViewById(R.id.week_eth);
//        week_usd = (TextView) convertView.findViewById(R.id.week_usd);
//        week_btc = (TextView) convertView.findViewById(R.id.week_btc);
//        month_eth = (TextView) convertView.findViewById(R.id.month_eth);
//        month_usd = (TextView) convertView.findViewById(R.id.month_usd);
//        month_btc = (TextView) convertView.findViewById(R.id.month_btc);
//        eth_btc = (TextView) convertView.findViewById(R.id.eth_btc);
//        eth_usd = (TextView) convertView.findViewById(R.id.eth_usd);
//        btc_usd = (TextView) convertView.findViewById(R.id.btc_usd);
//        lineChart = (LineChart) convertView.findViewById(R.id.line_chart);
//        chart_hashrate = (TextView) convertView.findViewById(R.id.chart_hashrate);
//        chart_shares = (TextView) convertView.findViewById(R.id.chart_shares);
//        time_next_payout = (TextView) convertView.findViewById(R.id.time_next_payout);
//        percent_next_payout = (LinearLayout) convertView.findViewById(R.id.percent_next_payout);
//        goal_day = (TextView) convertView.findViewById(R.id.goal_day);
//        unit_reported = (TextView) convertView.findViewById(R.id.unit_reported);
//        unit_current = (TextView) convertView.findViewById(R.id.unit_current);
//        unit_average = (TextView)convertView.findViewById(R.id.unit_average);
//        title_unpaid = (TextView) convertView.findViewById(R.id.title_unpaid);
//        title_coin = (TextView)convertView.findViewById(R.id.title_coin);
//        title_coin_btc = (TextView) convertView.findViewById(R.id.title_coin_btc);
//        title_coin_usd = (TextView) convertView.findViewById(R.id.title_coin_usd);
//
//        curentStats = new CurrentStats();
//        networkStats = new NetworkStats();
//        yValueAverageHashrate = new ArrayList<>();
//        yValueCurrentHashRate = new ArrayList<>();
//        yValueReportHashrate = new ArrayList<>();
//        yValueValidShares = new ArrayList<>();
//        yValueInValidShares = new ArrayList<>();
//        yValueStaleShares = new ArrayList<>();
//        myPreferences = new MyPreferences();
//        chart_hashrate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chart_hashrate.setBackgroundResource(R.color.background_selected_chart);
//                chart_shares.setBackgroundResource(R.color.background_unselected_chart);
//                if(yValueAverageHashrate.size() > 0 && yValueCurrentHashRate.size() > 0 && yValueReportHashrate.size() > 0){
//                    DrawGraphHashRate();
//                }
//            }
//        });
//        chart_shares.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chart_hashrate.setBackgroundResource(R.color.background_unselected_chart);
//                chart_shares.setBackgroundResource(R.color.background_selected_chart);
//                if(yValueValidShares.size() > 0 && yValueInValidShares.size() > 0 && yValueStaleShares.size() > 0){
//                    DrawGraphShares();
//                }
//            }
//        });
//        if(miner != null) {
//            urlData = new String[5];
//            urlData[0] = miner.getEndpoint() + "/networkStats";
//            urlData[1] = miner.getEndpoint() + "/miner/" + miner.getId() + "/currentStats";
//            urlData[2] = miner.getEndpoint() + "/miner/" + miner.getId() + "/history";
//            urlData[3] = miner.getEndpoint() + "/miner/" + miner.getId() + "/payouts";
//            urlData[4] = miner.getEndpoint() + "/miner/" + miner.getId() + "/Settings";
//            new GetAPI().execute(urlData);
//        }
//        return convertView;
//    }
//
//    private void DrawGraphHashRate(){
//        lineChart.setDragEnabled(true);
//        lineChart.getDescription().setEnabled(false);
//
//        YAxis leftAxis = lineChart.getAxisLeft();
//        //lineChart.setVisibleYRangeMaximum(150, YAxis.AxisDependency.LEFT);
//        leftAxis.setTextColor(Color.WHITE);
//        lineChart.setScaleEnabled(false);
//
//
//        lineChart.getXAxis().setTextColor(Color.TRANSPARENT);
//        //lineChart.getXAxis().setAxisMaximum(72);
//        lineChart.getAxisRight().setTextColor(Color.WHITE);
//        lineChart.getLegend().setTextColor(Color.WHITE);
//
//        LineDataSet lineCurrent = new LineDataSet(yValueCurrentHashRate, "Current Hashrate");
//        lineCurrent.setFillAlpha(2);
//        lineCurrent.setColor(activity.getResources().getColor(R.color.color_txt_current_hashrate));
//        lineCurrent.setCircleRadius(1);
//        lineCurrent.setCircleColor(activity.getResources().getColor(R.color.color_txt_current_hashrate));
//        lineCurrent.setLineWidth(0.3f);
//        lineCurrent.setValueTextColor(Color.TRANSPARENT);
//
//        LineDataSet lineAverage = new LineDataSet(yValueAverageHashrate, "Average Hashrate");
//        lineAverage.setFillAlpha(2);
//        lineAverage.setColor(activity.getResources().getColor(R.color.color_txt_average_hashrate));
//        lineAverage.setCircleRadius(1);
//        lineAverage.setCircleColor(activity.getResources().getColor(R.color.color_txt_average_hashrate));
//        lineAverage.setLineWidth(0.3f);
//        lineAverage.setValueTextColor(Color.TRANSPARENT);
//
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
////        dataSets.add(lineReported);
//        dataSets.add(lineCurrent);
//        dataSets.add(lineAverage);
//        LineData data = new LineData(dataSets);
//        lineChart.setData(data);
//        lineChart.getData().setHighlightEnabled(false);
//        lineChart.invalidate();
//    }
//
//    private void DrawGraphShares(){
//        lineChart.removeAllViews();
//        lineChart.setDragEnabled(true);
//        lineChart.getDescription().setEnabled(false);
//
//        YAxis leftAxis = lineChart.getAxisLeft();
//        leftAxis.setTextColor(Color.WHITE);
//
//        lineChart.getXAxis().setTextColor(Color.TRANSPARENT);
//        lineChart.getXAxis().setAxisMaximum(72);
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
//        lineCurrent.setColor(activity.getResources().getColor(R.color.invalid));
//        lineCurrent.setCircleRadius(1);
//        lineCurrent.setCircleColor(activity.getResources().getColor(R.color.invalid));
//        lineCurrent.setLineWidth(0.3f);
//        lineCurrent.setValueTextColor(Color.TRANSPARENT);
//
//        LineDataSet lineAverage = new LineDataSet(yValueStaleShares, "Stale Shares");
//        lineAverage.setFillAlpha(2);
//        lineAverage.setColor(activity.getResources().getColor(R.color.color_txt_average_hashrate));
//        lineAverage.setCircleRadius(1);
//        lineAverage.setCircleColor(activity.getResources().getColor(R.color.color_txt_average_hashrate));
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
//
//    private void SetView(){
//        eth_usd.setText(new DecimalFormat("#.#").format(networkStats.getEthUsd()));
//        eth_btc.setText(new DecimalFormat("#.####").format(networkStats.getEthBtc()));
//        btc_usd.setText(new DecimalFormat("#.#").format(networkStats.getEthUsd()/networkStats.getEthBtc()));
//        immature_hashrate.setText(curentStats.getReportedHashrate());
//        current_hashrate.setText(curentStats.getCurrentHashrate());
//        average_hashrate.setText(curentStats.getAverageHashrate());
//        unpaid_balance.setText(curentStats.getUnpaid());
//        active_workers.setText(String.valueOf(curentStats.getActiveWorkers()));
//        valid_shares.setText(String.valueOf(curentStats.getValidShares()));
//        invalid_shares.setText(String.valueOf(curentStats.getInvalidShares()));
//        stale_shares.setText(String.valueOf(curentStats.getStaleShares()));
//        last_screen.setText(String.valueOf(curentStats.getLastSeen()));
//        hour_eth.setText(new DecimalFormat("#.####").format(curentStats.getCoinsPerMin()));
//        hour_btc.setText(new DecimalFormat("#.####").format(curentStats.getBtcPerMin()));
//        hour_usd.setText(new DecimalFormat("#.####").format(curentStats.getUsdPerMin()));
//        day_eth.setText(new DecimalFormat("#.####").format(curentStats.getCoinsPerMin()*24));
//        day_btc.setText(new DecimalFormat("#.####").format(curentStats.getBtcPerMin()*24));
//        day_usd.setText(new DecimalFormat("#.####").format(curentStats.getUsdPerMin()*24));
//        week_eth.setText(new DecimalFormat("#.####").format(curentStats.getCoinsPerMin()*24*7));
//        week_btc.setText(new DecimalFormat("#.####").format(curentStats.getBtcPerMin()*24*7));
//        week_usd.setText(new DecimalFormat("#.####").format(curentStats.getUsdPerMin()*24*7));
//        month_eth.setText(new DecimalFormat("#.####").format(curentStats.getCoinsPerMin()*24*30));
//        month_btc.setText(new DecimalFormat("#.####").format(curentStats.getBtcPerMin()*24*30));
//        month_usd.setText(new DecimalFormat("#.####").format(curentStats.getUsdPerMin()*24*30));
//        NextPayout();
//    }
//
//    private class GetAPI extends AsyncTask<String, Void, Void> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // Showing progress dialog
////            if(getListener() != null){
////                getListener().showProgress();
////            }
//        }
//
//        @Override
//        protected Void doInBackground(String... url) {
//            HttpHandler sh = new HttpHandler();
//
//            // Making a request to url and getting response
//            String jsonStr = sh.makeServiceCall(url[1]);
//
//            if (jsonStr != null) {
//                try {
//                    JSONObject jsonObj = new JSONObject(jsonStr);
//
//                    // Getting JSON Array node
//                    JSONObject data = jsonObj.getJSONObject("data");
//                    long reported, current, average, unpaidBalance;
//                    double coinsPerMin, usdPerMin, btcPerMin;
//                    int workers, lastScreen, validShares, invalidShares, staleShares;
//                    try {
//                        reported = data.getLong("unconfirmed");
//                    }catch (Exception e){
//                        reported = 0;
//                    }
//                    try{
//                        current = data.getLong("currentHashrate");
//                    }catch (Exception e){
//                        current = 0;
//                    }
//                    try{
//                        average = data.getLong("averageHashrate");
//                    }catch (Exception e){
//                        average =0;
//                    }
//                    try{
//                        unpaidBalance = data.getLong("unpaid");
//                    }catch (Exception e){
//                        unpaidBalance = 0;
//                    }
//                    try{
//                        workers = data.getInt("activeWorkers");
//                    }catch (Exception e){
//                        workers = 0;
//                    }
//                    try{
//                        lastScreen = data.getInt("lastSeen");
//                    }catch (Exception e){
//                        lastScreen = 0;
//                    }
//                    try{
//                        validShares = data.getInt("validShares");
//                    }catch (Exception e){
//                        validShares = 0;
//                    }
//                    try{
//                        invalidShares = data.getInt("invalidShares");
//                    }catch (Exception e){
//                        invalidShares = 0;
//                    }
//                    try{
//                        staleShares = data.getInt("staleShares");
//                    }catch (Exception e){
//                        staleShares = 0;
//                    }
//                    try{
//                        coinsPerMin = data.getDouble("coinsPerMin");
//                    }catch (Exception e){
//                        coinsPerMin = 0;
//                    }
//                    try{
//                        usdPerMin = data.getDouble("usdPerMin");
//                    }catch (Exception e){
//                        usdPerMin = 0;
//                    }
//                    try{
//                        btcPerMin = data.getDouble("btcPerMin");
//                    }catch (Exception e){
//                        btcPerMin = 0;
//                    }
//
//                    curentStats.setReportedHashrate(reported);
//                    curentStats.setCurrentHashrate(current);
//                    curentStats.setAverageHashrate(average);
//                    curentStats.setUnpaid(unpaidBalance);
//                    curentStats.setActiveWorkers(workers);
//                    curentStats.setLastSeen(lastScreen);
//                    curentStats.setValidShares(validShares);
//                    curentStats.setInvalidShares(invalidShares);
//                    curentStats.setStaleShares(staleShares);
//                    curentStats.setCoinsPerMin(coinsPerMin);
//                    curentStats.setBtcPerMin(btcPerMin);
//                    curentStats.setUsdPerMin(usdPerMin);
//
//                } catch (final JSONException e) {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(activity,
//                                    "Data parsing error: ",
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                        }
//                    });
//
//                }
//            } else {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity,
//                                "Couldn't get data from server!",
//                                Toast.LENGTH_LONG)
//                                .show();
//                    }
//                });
//                return null;
//            }
//            String jsonNetworkStats = sh.makeServiceCall(url[0]);
//            if (jsonNetworkStats != null) {
//                try {
//                    JSONObject jsonObj = new JSONObject(jsonNetworkStats);
//                    JSONObject data = jsonObj.getJSONObject("data");
//                    double ethUsd = data.getDouble("usd");
//                    double ethBtc = data.getDouble("btc");
//                    networkStats.setEthBtc(ethBtc);
//                    networkStats.setEthUsd(ethUsd);
//                } catch (final JSONException e) {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(activity,
//                                    "Data parsing error",
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                        }
//                    });
//                }
//            } else {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity,
//                                "Couldn't get data from server!",
//                                Toast.LENGTH_LONG)
//                                .show();
//                    }
//                });
//                return null;
//            }
//
//            String jsonNetworkHistory = sh.makeServiceCall(url[2]);
//            if (jsonNetworkHistory != null) {
//                try {
//                    JSONObject jsonObj = new JSONObject(jsonNetworkHistory);
//                    JSONArray historys = jsonObj.getJSONArray("data");
//                    for(int i = 0; i < historys.length(); i++){
//                        int value = i + 1;
//                        JSONObject valueJson = historys.getJSONObject(i);
//                        double dbReportedHashrate = valueJson.getDouble("reportedHashrate");
//                        double dbconvertReportedHashrate = (double) dbReportedHashrate / 1000000;
//
//                        double dbCurrentHashrate = valueJson.getDouble("currentHashrate");
//                        double dbconvertCurrentHashrate = (double) dbCurrentHashrate / 1000000;
//
//                        double dbAverageHashrate = valueJson.getDouble("averageHashrate");
//                        double dbconvertAverageHashrate = (double) dbAverageHashrate / 1000000;
//
//                        double dbValidShare = valueJson.getDouble("validShares");
//                        double dbInValidShare = valueJson.getDouble("invalidShares");
//                        double dbStaleShare = valueJson.getDouble("staleShares");
//
//                        yValueValidShares.add(new Entry(i, (float)dbValidShare));
//                        yValueInValidShares.add(new Entry(i, (float)dbInValidShare));
//                        yValueStaleShares.add(new Entry(i, (float)dbStaleShare));
//                        yValueReportHashrate.add(new Entry(i, (float) dbconvertReportedHashrate));
//                        yValueCurrentHashRate.add(new Entry(i, (float) dbconvertCurrentHashrate));
//                        yValueAverageHashrate.add(new Entry(i, (float) dbconvertAverageHashrate));
//                    }
//                } catch (final JSONException e) {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(activity,
//                                    "Data parsing error",
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                        }
//                    });
//                }
//            } else {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity,
//                                "Couldn't get data from server!",
//                                Toast.LENGTH_LONG)
//                                .show();
//                    }
//                });
//                return null;
//            }
//
//            String jsonPayouts = sh.makeServiceCall(url[3]);
//            if (jsonPayouts != null) {
//                try {
//                    JSONObject jsonObj = new JSONObject(jsonPayouts);
//                    JSONArray payoutsArray = jsonObj.getJSONArray("data");
//                    JSONObject valueJson = payoutsArray.getJSONObject(0);
//                    paidOn = valueJson.getLong("paidOn");
//                } catch (final JSONException e) {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(activity,
//                                    "Data parsing error",
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                        }
//                    });
//                }
//            } else {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity,
//                                "Couldn't get data from server!",
//                                Toast.LENGTH_LONG)
//                                .show();
//                    }
//                });
//                return null;
//            }
//
//            String jsonSettings = sh.makeServiceCall(url[4]);
//            if (jsonSettings != null) {
//                try {
//                    JSONObject jsonObj = new JSONObject(jsonSettings);
//                    JSONObject settings = jsonObj.getJSONObject("data");
//                    minPayout = settings.getDouble("minPayout") / 100000000;
//                } catch (final JSONException e) {
//                    activity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(activity,
//                                    "Data parsing error",
//                                    Toast.LENGTH_LONG)
//                                    .show();
//                        }
//                    });
//                }
//            } else {
//                activity.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(activity,
//                                "Couldn't get data from server!",
//                                Toast.LENGTH_LONG)
//                                .show();
//                    }
//                });
//                return null;
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
////            Activity activity = getActivity();
////            if (isAdded() && activity != null) {
////                SetView();
////                // Draw line chart
////                if(yValueAverageHashrate.size() > 0 && yValueReportHashrate.size() > 0 && yValueCurrentHashRate.size() > 0) {
////                    DrawGraphHashRate();
////                }
////            }
//            SetView();
//            // Draw line chart
//            if(yValueAverageHashrate.size() > 0 && yValueReportHashrate.size() > 0 && yValueCurrentHashRate.size() > 0) {
//                DrawGraphHashRate();
//            }
////            if(getListener() != null){
////                getListener().hideProgress();
////            }
//        }
//    }
//
//    private String getDate(long time) {
//        try {
//            Date date = new Date(time*1000L);
//            Calendar cal = Calendar.getInstance();
//            TimeZone tz = cal.getTimeZone();//get your local time zone.
//            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM, hh:mm");
//            sdf.setTimeZone(tz);//set time zone.
//            String formattedDate = sdf.format(date);
//            return formattedDate;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return "";
//    }
//
//    private void NextPayout(){
//        Calendar calendar = Calendar.getInstance();
//        long now = calendar.getTimeInMillis() / 1000;
//        double coinPerDay = curentStats.getCoinsPerMin()*24;
//        double estimateDay = minPayout/coinPerDay;
//        double dayProgress = (paidOn + MinNumber(estimateDay, 7) * 86400 );
//        long dayGoal = (long)dayProgress;
//        String strGoal = getDate(dayGoal);
//        goal_day.setText(strGoal);
//        double resultSeconds = abs(dayProgress - now);
//        int minute, hour, day;
//        day = (int)resultSeconds / 86400;
//        hour = ((int)resultSeconds % 86400) / 3600;
//        minute = ((int)resultSeconds - (day*86400) - (hour * 3600))/60;
//        String strNextPayout = "";
//        if(day != 0 ){
//            strNextPayout = day + "d ";
//        }
//        if(hour != 0){
//            strNextPayout += hour + "h ";
//        }
//        if(minute != 0){
//            strNextPayout += minute + "m";
//        }
//        time_next_payout.setText(strNextPayout);
//        double secondsEntimate = MinNumber(estimateDay, 7)*86400;
//        double percent = 100-((resultSeconds*100)/secondsEntimate);
//        float f = (float)percent/100;
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
//        lp.weight = f;
//        percent_next_payout.setLayoutParams(lp);
//    }
//
//    private double MinNumber(double numFirst, double numSecond){
//        if( numFirst > numSecond){
//            return numSecond;
//        }
//        return numFirst;
//    }
//
//    class CurrentStats{
//
//        int lastSeen, validShares,
//                invalidShares, staleShares, activeWorkers;
//        long unpaid, reportedHashrate, currentHashrate, averageHashrate;
//        double coinsPerMin, usdPerMin, btcPerMin;
//
//        public long getLastSeen() {
//            Calendar calendar = Calendar.getInstance();
//            long now = calendar.getTimeInMillis() / 1000;
//            return (now - lastSeen)/60;
//        }
//
//        public void setLastSeen(int lastSeen) {
//            this.lastSeen = lastSeen;
//        }
//
//        public String getReportedHashrate() {
//            double dbconvertReportedHashrate = (double) reportedHashrate / 100000000;
//
//            String strReportedHashrate = new DecimalFormat("#.########").format(dbconvertReportedHashrate);
//            return strReportedHashrate;
//        }
//
//        public void setReportedHashrate(long reportedHashrate) {
//            this.reportedHashrate = reportedHashrate;
//        }
//
//        public String getCurrentHashrate() {
//            double dbconvertCurrentHashrate = (double) currentHashrate / 1000;
//
//            String strCurrentHashrate = new DecimalFormat("#.##").format(dbconvertCurrentHashrate);
//            return strCurrentHashrate;
//        }
//
//        public void setCurrentHashrate(long currentHashrate) {
//            this.currentHashrate = currentHashrate;
//        }
//
//        public String getAverageHashrate() {
//            double dbconvertAverageHashrate = (double) averageHashrate / 1000;
//
//            String strAverageHashrate = new DecimalFormat("#.##").format(dbconvertAverageHashrate);
//            return strAverageHashrate;
//        }
//
//        public void setAverageHashrate(long averageHashrate) {
//            this.averageHashrate = averageHashrate;
//        }
//
//        public int getValidShares() {
//            return validShares;
//        }
//
//        public void setValidShares(int validShares) {
//            this.validShares = validShares;
//        }
//
//        public int getInvalidShares() {
//            return invalidShares;
//        }
//
//        public void setInvalidShares(int invalidShares) {
//            this.invalidShares = invalidShares;
//        }
//
//        public int getStaleShares() {
//            return staleShares;
//        }
//
//        public void setStaleShares(int staleShares) {
//            this.staleShares = staleShares;
//        }
//
//        public int getActiveWorkers() {
//            return activeWorkers;
//        }
//
//        public void setActiveWorkers(int activeWorkers) {
//            this.activeWorkers = activeWorkers;
//        }
//
//        public String getUnpaid() {
//            double dbconvertUnpaidBalance = (double) unpaid / 100000000;
//            String strAverageHashrate = new DecimalFormat("#.#####").format(dbconvertUnpaidBalance);
//            return strAverageHashrate;
//        }
//
//        public void setUnpaid(long unpaid) {
//            this.unpaid = unpaid;
//        }
//
//        public double getCoinsPerMin() {
//            double hour = coinsPerMin * 60;
//            return hour;
//        }
//
//        public void setCoinsPerMin(double coinsPerMin) {
//            this.coinsPerMin = coinsPerMin;
//        }
//
//        public double getUsdPerMin() {
//            double hour = usdPerMin * 60;
//            return hour;
//        }
//
//        public void setUsdPerMin(double usdPerMin) {
//            this.usdPerMin = usdPerMin;
//        }
//
//        public double getBtcPerMin() {
//            double hour = btcPerMin * 60;
//            return hour;
//        }
//
//        public void setBtcPerMin(double btcPerMin) {
//            this.btcPerMin = btcPerMin;
//        }
//    }
//}

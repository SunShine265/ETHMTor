package com.goodproductssoft.minningpool.activities;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goodproductssoft.minningpool.CustomApp;
import com.goodproductssoft.minningpool.HttpHandler;
import com.goodproductssoft.minningpool.MyPreferences;
import com.goodproductssoft.minningpool.R;
import com.goodproductssoft.minningpool.WebService;
import com.goodproductssoft.minningpool.adapters.YourWorkerAdapter;
import com.goodproductssoft.minningpool.models.Miner;
import com.goodproductssoft.minningpool.models.TotalYourWorker;
import com.goodproductssoft.minningpool.models.YourWorker;
import com.goodproductssoft.minningpool.models.YourWorkerNotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by user on 4/12/2018.
 */

public class FragmentWorker extends Fragment {
    private ArrayList<YourWorker> valueList;
    private ListView lv;
    private TextView totalYourWorker1, totalYourWorker2, total_current, total_reported, total_avg, unit_total_current, unit_total_reported, unit_total_avg,
            total_valid, total_stale, total_invalid;
    private TotalYourWorker yourWorkerTotal;
    RelativeLayout progressbar;
    SharedPreferences pref;
    int checkAccount;
    MyPreferences myPreferences;
    Miner miner;
    static String endpointEth = "https://api.ethermine.org";
    static String endpointEtc = "https://api-etc.ethermine.org";

    ProgressDisplay getListener(){
        if(getActivity() != null && getActivity() instanceof ProgressDisplay){
            return ((ProgressDisplay) getActivity() );
        }
        return null;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_worker, viewGroup, false);
        lv = view.findViewById(R.id.list);
        totalYourWorker1 = view.findViewById(R.id.total1);
        totalYourWorker2 = view.findViewById(R.id.total2);
        total_current = view.findViewById(R.id.total_current);
        total_reported = view.findViewById(R.id.total_reported);
        total_avg = view.findViewById(R.id.total_avg);
        unit_total_current = view.findViewById(R.id.unit_total_current);
        unit_total_reported = view.findViewById(R.id.unit_total_reported);
        unit_total_avg = view.findViewById(R.id.unit_total_avg);
        total_valid = view.findViewById(R.id.total_valid);
        total_stale = view.findViewById(R.id.total_stale);
        total_invalid = view.findViewById(R.id.total_invalid);
        progressbar = view.findViewById(R.id.progressbar);

        myPreferences = MyPreferences.getInstance();
        valueList = new ArrayList<>();
        yourWorkerTotal = new TotalYourWorker();
        if(getActivity() != null) {
            CheckInitContent(getActivity());
        }
        checkAccount = CheckAccount();
        return view;
    }

    public void CheckInitContent(Context a){
        miner = GetMinerIdActive();
        if(miner != null) {
            String urlWorker = miner.getEndpoint() + "/miner/" + miner.getId() + "/workers";
            GetDataWorkers(miner.getId());
        }
    }

    private Miner GetMinerIdActive(){
        if(getActivity() != null) {
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

    private void GetDataWorkers(String id){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(miner.getEndpoint())
                .client(CustomApp.getHttpClient())
                .build();
        WebService ws = retrofit.create(WebService.class);
        Call<ResponseBody> result = ws.GetWorkers(id);
        final Activity activity = getActivity();
        if(getListener() != null){
            getListener().showProgress();
        }
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonStr = response.body().string();
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    ArrayList<String> listWorker = new ArrayList<>();

                    // Getting JSON Array node
                    JSONArray workers = jsonObj.getJSONArray("data");
                    if(workers != null && workers.length() > 0) {
                        ArrayList<YourWorkerNotify> yourWorkerNotifys = new ArrayList<>();
                        // looping through All Contacts
                        int countCurrentWorker = 0, countActiveWorker = 0;
                        for (int i = 0; i < workers.length(); i++) {
                            JSONObject value = workers.getJSONObject(i);
                            YourWorker itemYourWorker = new YourWorker();
                            YourWorkerNotify yourWorkerNotify = new YourWorkerNotify();
                            String worker = value.getString("worker");
                            countCurrentWorker++;
                            yourWorkerTotal.setYourCurrentWorker(countCurrentWorker);
                            itemYourWorker.setYourWorker(worker);
                            listWorker.add(worker);
                            yourWorkerNotify.setNameYourWorker(worker);
                            yourWorkerNotify.setIdMiner(miner.getId());

                            double currentHashrate;
                            try {
                                currentHashrate = value.getDouble("currentHashrate");
                            } catch (Exception e) {
                                currentHashrate = 0;
                            }
                            double dbconvertTotalCurrentHashrate = currentHashrate + yourWorkerTotal.getCurrent();
                            yourWorkerTotal.setCurrent(dbconvertTotalCurrentHashrate);
                            itemYourWorker.setCurrent(currentHashrate);

                            double averageHashrate;
                            try {
                                averageHashrate = value.getDouble("averageHashrate");
                            } catch (Exception e) {
                                averageHashrate = 0;
                            }
                            double dbconvertTotalAvgHashrate = averageHashrate + yourWorkerTotal.getAvg();
                            yourWorkerTotal.setAvg(dbconvertTotalAvgHashrate);
                            itemYourWorker.setAvg(averageHashrate);

                            double reportedHashrate;
                            try {
                                reportedHashrate = value.getDouble("reportedHashrate");
                            } catch (Exception e) {
                                reportedHashrate = 0;
                            }
                            if (reportedHashrate == 0) {
                                itemYourWorker.setValue(false);
                            } else {
                                itemYourWorker.setValue(true);
                                countActiveWorker++;
                            }
                            yourWorkerTotal.setYourWorkerActive(countActiveWorker);
                            yourWorkerNotify.setReportHashrate(reportedHashrate);
                            double dbconvertTotalReportedHashrate = reportedHashrate + yourWorkerTotal.getReported();
                            yourWorkerTotal.setReported(dbconvertTotalReportedHashrate);
                            itemYourWorker.setReported(reportedHashrate);

                            int validShares;
                            try {
                                validShares = value.getInt("validShares");
                            } catch (JSONException e) {
                                validShares = 0;
                            }
                            yourWorkerTotal.setValid(yourWorkerTotal.getValid() + validShares);
                            itemYourWorker.setValid(validShares);

                            int staleShares;
                            try {
                                staleShares = value.getInt("staleShares");
                            } catch (JSONException e) {
                                staleShares = 0;
                            }
                            yourWorkerTotal.setStale(yourWorkerTotal.getStale() + staleShares);
                            itemYourWorker.setStale(staleShares);

                            int invalidShares;
                            try {
                                invalidShares = value.getInt("invalidShares");
                            } catch (JSONException e) {
                                invalidShares = 0;
                            }
                            yourWorkerTotal.setInvalid(yourWorkerTotal.getInvalid() + invalidShares);
                            itemYourWorker.setInvalid(invalidShares);

                            long lastSeen;
                            try {
                                lastSeen = value.getLong("lastSeen");
                            } catch (JSONException e) {
                                lastSeen = 0;
                            }
                            String strLastTime = "";
                            if (lastSeen != 0) {
                                Calendar calendar = Calendar.getInstance();
                                long now = calendar.getTimeInMillis() / 1000;
                                strLastTime = String.valueOf((now - lastSeen) / 60);
                            }
                            itemYourWorker.setLastScreen(strLastTime);
                            valueList.add(itemYourWorker);
                            yourWorkerNotifys.add(yourWorkerNotify);
                        }
                        miner.setIdMinerBackup(miner.getId());
                        miner.setWorkersBackup(yourWorkerNotifys);
                        if (activity != null) {
                            myPreferences.UpdateMiner(miner);
                        }
                        if (activity != null) {
                            if (valueList.size() > 0) {
                                YourWorkerAdapter adapter = new YourWorkerAdapter(activity, valueList);
                                lv.setAdapter(adapter);
                            }
                            totalYourWorker1.setText(String.valueOf(yourWorkerTotal.getYourWorkerActive()));
                            totalYourWorker2.setText(String.valueOf(yourWorkerTotal.getYourCurrentWorker()));

                            total_current.setText(ChangeHashrate(yourWorkerTotal.getCurrent()));
                            total_reported.setText(ChangeHashrate(yourWorkerTotal.getReported()));
                            total_avg.setText(ChangeHashrate(yourWorkerTotal.getAvg()));

                            unit_total_current.setText(ChangeHashrateUnit(yourWorkerTotal.getCurrent()));
                            unit_total_reported.setText(ChangeHashrateUnit(yourWorkerTotal.getReported()));
                            unit_total_avg.setText(ChangeHashrateUnit(yourWorkerTotal.getAvg()));

                            total_valid.setText(String.valueOf(yourWorkerTotal.getValid()));
                            total_stale.setText(String.valueOf(yourWorkerTotal.getStale()));
                            total_invalid.setText(String.valueOf(yourWorkerTotal.getInvalid()));
                        }
                    }
                    else{
                        if(activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomApp.showToast("No data");
                                }
                            });
                        }
                    }
                } catch (Exception ex){
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomApp.showToast("Data error!");
                            }
                        });
                    }
                }
                if(getListener() != null){
                    getListener().hideProgress();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomApp.showToast("Couldn't get data from server!");
                        }
                    });
                }

                if(getListener() != null){
                    getListener().hideProgress();
                }
            }
        });
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetWorker extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(getListener() != null){
                getListener().showProgress();
            }
        }

        @Override
        protected Void doInBackground(String... url) {
            HttpHandler sh = new HttpHandler();
            final Activity activity = getActivity();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url[0]);
            ArrayList<String> listWorker = new ArrayList<>();
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray workers = jsonObj.getJSONArray("data");
                    if(workers != null && workers.length() > 0) {
                        ArrayList<YourWorkerNotify> yourWorkerNotifys = new ArrayList<>();
                        // looping through All Contacts
                        int countCurrentWorker = 0, countActiveWorker = 0;
                        for (int i = 0; i < workers.length(); i++) {
                            JSONObject value = workers.getJSONObject(i);
                            YourWorker itemYourWorker = new YourWorker();
                            YourWorkerNotify yourWorkerNotify = new YourWorkerNotify();
                            String worker = value.getString("worker");
                            countCurrentWorker++;
                            yourWorkerTotal.setYourCurrentWorker(countCurrentWorker);
                            itemYourWorker.setYourWorker(worker);
                            listWorker.add(worker);
                            yourWorkerNotify.setNameYourWorker(worker);
                            yourWorkerNotify.setIdMiner(miner.getId());

                            double currentHashrate;
                            try {
                                currentHashrate = value.getDouble("currentHashrate");
                            } catch (Exception e) {
                                currentHashrate = 0;
                            }
//                        String strCurrentHashrate = new DecimalFormat("#.#").format(currentHashrate/1000000);
                            double dbconvertTotalCurrentHashrate = currentHashrate + yourWorkerTotal.getCurrent();
                            yourWorkerTotal.setCurrent(dbconvertTotalCurrentHashrate);
                            itemYourWorker.setCurrent(currentHashrate);

                            double averageHashrate;
                            try {
                                averageHashrate = value.getDouble("averageHashrate");
                            } catch (Exception e) {
                                averageHashrate = 0;
                            }
//                        String strAvgHashrate = new DecimalFormat("#.#").format(averageHashrate/1000000);
                            double dbconvertTotalAvgHashrate = averageHashrate + yourWorkerTotal.getAvg();
                            yourWorkerTotal.setAvg(dbconvertTotalAvgHashrate);
                            itemYourWorker.setAvg(averageHashrate);

                            double reportedHashrate;
                            try {
                                reportedHashrate = value.getDouble("reportedHashrate");
                            } catch (Exception e) {
                                reportedHashrate = 0;
                            }
                            if (reportedHashrate == 0) {
                                itemYourWorker.setValue(false);
                            } else {
                                itemYourWorker.setValue(true);
                                countActiveWorker++;
                            }
                            yourWorkerTotal.setYourWorkerActive(countActiveWorker);
                            yourWorkerNotify.setReportHashrate(reportedHashrate);
//                        String strReportedHashrate = new DecimalFormat("#.#").format(reportedHashrate/1000000);
                            double dbconvertTotalReportedHashrate = reportedHashrate + yourWorkerTotal.getReported();
                            yourWorkerTotal.setReported(dbconvertTotalReportedHashrate);
                            itemYourWorker.setReported(reportedHashrate);

                            int validShares;
                            try {
                                validShares = value.getInt("validShares");
                            } catch (JSONException e) {
                                validShares = 0;
                            }
                            yourWorkerTotal.setValid(yourWorkerTotal.getValid() + validShares);
                            itemYourWorker.setValid(validShares);

                            int staleShares;
                            try {
                                staleShares = value.getInt("staleShares");
                            } catch (JSONException e) {
                                staleShares = 0;
                            }
                            yourWorkerTotal.setStale(yourWorkerTotal.getStale() + staleShares);
                            itemYourWorker.setStale(staleShares);

                            int invalidShares;
                            try {
                                invalidShares = value.getInt("invalidShares");
                            } catch (JSONException e) {
                                invalidShares = 0;
                            }
                            yourWorkerTotal.setInvalid(yourWorkerTotal.getInvalid() + invalidShares);
                            itemYourWorker.setInvalid(invalidShares);

                            long lastSeen;
                            try {
                                lastSeen = value.getLong("lastSeen");
                            } catch (JSONException e) {
                                lastSeen = 0;
                            }
                            String strLastTime = "";
                            if (lastSeen != 0) {
                                Calendar calendar = Calendar.getInstance();
                                long now = calendar.getTimeInMillis() / 1000;
                                strLastTime = String.valueOf((now - lastSeen) / 60);
                            }
                            itemYourWorker.setLastScreen(strLastTime);
                            valueList.add(itemYourWorker);
                            yourWorkerNotifys.add(yourWorkerNotify);
                        }
                        miner.setIdMinerBackup(miner.getId());
                        miner.setWorkersBackup(yourWorkerNotifys);
                        if (activity != null) {
                            myPreferences.UpdateMiner(miner);
                        }
                    }
                    else{
                        if(activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomApp.showToast("No data");
                                }
                            });
                        }
                    }
                } catch (final JSONException e) {
                    if(activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomApp.showToast("Data parsing error");
                            }
                        });
                    }
                }
            } else {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomApp.showToast("Couldn't get data from server.");
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
            // Dismiss the progress dialog
//            progressbar.setVisibility(View.GONE);
            if(getListener() != null){
                getListener().hideProgress();
            }
            /**
             * Updating parsed JSON data into ListView
             * */
            Activity activity = getActivity();
            if (isAdded() && activity != null) {
                if (valueList.size() > 0) {
                    YourWorkerAdapter adapter = new YourWorkerAdapter(activity, valueList);
                    lv.setAdapter(adapter);
                }
                totalYourWorker1.setText(String.valueOf(yourWorkerTotal.getYourWorkerActive()));
                totalYourWorker2.setText(String.valueOf(yourWorkerTotal.getYourCurrentWorker()));

                total_current.setText(ChangeHashrate(yourWorkerTotal.getCurrent()));
                total_reported.setText(ChangeHashrate(yourWorkerTotal.getReported()));
                total_avg.setText(ChangeHashrate(yourWorkerTotal.getAvg()));

                unit_total_current.setText(ChangeHashrateUnit(yourWorkerTotal.getCurrent()));
                unit_total_reported.setText(ChangeHashrateUnit(yourWorkerTotal.getReported()));
                unit_total_avg.setText(ChangeHashrateUnit(yourWorkerTotal.getAvg()));

                total_valid.setText(String.valueOf(yourWorkerTotal.getValid()));
                total_stale.setText(String.valueOf(yourWorkerTotal.getStale()));
                total_invalid.setText(String.valueOf(yourWorkerTotal.getInvalid()));
            }
        }

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

    public interface ProgressDisplay {

        void showProgress();

        void hideProgress();
    }
}

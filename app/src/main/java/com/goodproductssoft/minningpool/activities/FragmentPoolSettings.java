package com.goodproductssoft.minningpool.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.goodproductssoft.minningpool.CustomApp;
import com.goodproductssoft.minningpool.HttpHandler;
import com.goodproductssoft.minningpool.MyPreferences;
import com.goodproductssoft.minningpool.R;
import com.goodproductssoft.minningpool.WebService;
import com.goodproductssoft.minningpool.adapters.IdMinerAdapter;
import com.goodproductssoft.minningpool.models.IdSuggestsion;
import com.goodproductssoft.minningpool.models.Miner;
import com.goodproductssoft.minningpool.models.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class FragmentPoolSettings extends Fragment {
    EditText edt_id_miner;
    Button button_mining_pool;
    MyPreferences myPreferences;
    Miner miner;
    Settings dataSettings;
    ListView list_id_miner;
    LinearLayout list_suggestions, container_id_suggestions;
    RadioButton account_eth, account_etc;
    RadioGroup rg_account;
    ArrayList<Miner> miners;
    IdSuggestsion idSuggestsion;
    ArrayList<IdSuggestsion> idSuggestsions;
    TextView id_miner, title_coin;
    Retrofit retrofit;

    IProgressDisplay getListener(){
        if(getActivity() != null && getActivity() instanceof IProgressDisplay) {
            return ((IProgressDisplay) getActivity());
        }
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pool_settings, container, false);
        button_mining_pool = view.findViewById(R.id.button_mining_pool);
        edt_id_miner = view.findViewById(R.id.edt_id_miner);
        list_id_miner = view.findViewById(R.id.list_id_miner);
        account_etc = view.findViewById(R.id.account_etc);
        account_eth = view.findViewById(R.id.account_eth);
        rg_account = view.findViewById(R.id.rg_account);
        list_suggestions = view.findViewById(R.id.list_suggestions);
        container_id_suggestions = view.findViewById(R.id.container_id_suggestions);

        edt_id_miner.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        dataSettings = new Settings();
        myPreferences = MyPreferences.getInstance();
        miners = myPreferences.GetIdMiners();
        button_mining_pool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMiner();
            }
        });
        if(getActivity() != null) {
            idSuggestsions = myPreferences.GetIdSuggestsions();
            list_suggestions.removeAllViews();
            if(idSuggestsions != null && idSuggestsions.size() > 0) {
                container_id_suggestions.setVisibility(View.VISIBLE);
                for (int i = idSuggestsions.size() - 1; i >= 0; i--) {
                    View idSuggestsionsView = inflater.inflate(R.layout.item_id_suggestsion, container, false);
                    TextView id_miner = idSuggestsionsView.findViewById(R.id.id_miner);
                    TextView title_coin = idSuggestsionsView.findViewById(R.id.title_coin);
                    id_miner.setText(idSuggestsions.get(i).getId());
                    title_coin.setText(idSuggestsions.get(i).getType().toString() + " - ");
                    list_suggestions.addView(idSuggestsionsView);
                    final IdSuggestsion finalIdSuggestsion = idSuggestsions.get(i);
                    idSuggestsionsView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(finalIdSuggestsion.getType().toString().equals("ETH")){
                                account_eth.setChecked(true);
                            }
                            else {
                                account_etc.setChecked(true);
                            }
                            edt_id_miner.setText(finalIdSuggestsion.getId().toString());
                            AddMiner();
                        }
                    });
                }
            }
        }
        //set radiobutton account
        if(getActivity() != null) {
            CheckInitContent(getActivity());
        }
        InitRadioButton();
        FragmentPoolSettings.this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        return view;
    }

    public void AddMiner() {
        miner = new Miner();
        if (account_etc.isChecked()) {
            miner.setType(Miner.CoinType.ETC);
        } else {
            miner.setType(Miner.CoinType.ETH);
        }
        final String strIDMiner = edt_id_miner.getText().toString()
                .replace("https://www.etherchain.org/account/", "")
                .replace("http://gastracker.io/addr/", "")
                .replace("https://explorer.zcha.in/accounts/", "").trim();
        if (!strIDMiner.isEmpty()) {
            String urlData = miner.getEndpoint() + "/miner/" + strIDMiner + "/settings";
//            final  AsyncTask<String, Void, Boolean> asyncTask = new GetSettings().execute(urlData);
            GetPoolSettings(strIDMiner, new OnGetPoolSettingsListenner() {
                @Override
                public void onFinished(final boolean isCheckSuccess) {
                    final Activity activity = getActivity();

                    if (isCheckSuccess && activity != null) {
                        // add only one key
                        miner.setId(strIDMiner);
                        miner.setActive(true);
                        miner.setNotification(true);

                        if (getListener() != null) {
                            getListener().TabMinerSelected();
                        }
                        InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(edt_id_miner.getWindowToken(), 0);

                        Miner previousMiner = GetMinerIdActive();
                        ResetActiveIdMiner();
                        myPreferences.AddIdMiner(miner);
                        try {
                            if (previousMiner != null
                                    && (!previousMiner.getId().equals(miner.getId())
                                    || !previousMiner.getType().equals(miner.getType()))) {
                                idSuggestsion = new IdSuggestsion();
                                idSuggestsion.setType(previousMiner.getType());
                                idSuggestsion.setId(previousMiner.getId());
                                ArrayList<IdSuggestsion> listIdSuggestsions = myPreferences.GetIdSuggestsions();
                                if (listIdSuggestsions == null) {
                                    listIdSuggestsions = new ArrayList<IdSuggestsion>();
                                }

                                for (IdSuggestsion listIdSuggestsion : new ArrayList<>(listIdSuggestsions)) {
                                    if (listIdSuggestsion.getId().equals(miner.getId())
                                            && listIdSuggestsion.getType().equals(miner.getType())) {
                                        listIdSuggestsions.remove(listIdSuggestsion);
                                    }
                                    if (listIdSuggestsion.getId().equals(idSuggestsion.getId())
                                            && listIdSuggestsion.getType().equals(idSuggestsion.getType())) {
                                        listIdSuggestsions.remove(listIdSuggestsion);
                                    }
                                }

                                if (!idSuggestsion.getId().equals(miner.getId())
                                        || !idSuggestsion.getType().equals(miner.getType())) {
                                    if (listIdSuggestsions != null && listIdSuggestsions.size() > 2) {
                                        listIdSuggestsions.remove(0);
                                    }

                                    listIdSuggestsions.add(idSuggestsion);
                                }
                                myPreferences.SaveIdSuggestsions(listIdSuggestsions);
                            }
                        } catch (Exception ex) {
                            CustomApp.showToast("Sorry, can't store recently wallet address!");
                        }

                        if(getListener() != null){
                            getListener().UnlockItemMenu();
                        }
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        Fragment fragment = new FragmentMiner();
                        fragmentTransaction.replace(R.id.fragment_content, fragment);
                        fragmentTransaction.commitAllowingStateLoss();
                    }
                }
            });
        }
    }

    public void CheckInitContent(Activity a){
        miner = GetMinerIdActive();
        if(a != null && miner != null) {
            String urlWorker = miner.getEndpoint() + "/miner/" + miner.getId() + "/settings";
//            new FragmentPoolSettings.GetSettings().execute(urlWorker);
            GetPoolSettings(miner.getId(), null);
        }
    }

    private void InitRadioButton(){
        if(miners != null && !miners.isEmpty()) {
            for (int i = 0; i < miners.size(); i++) {
                if (miners.get(i).isActive()) {
                    if (miners.get(i).getType() == Miner.CoinType.ETH) {
                        account_eth.setChecked(true);
                    } else
                        account_etc.setChecked(true);
                }
            }
        }
        else {
            account_eth.setChecked(true);
        }
    }

    private boolean CheckExitIDMiner(String id, String endpoint){
        if(miners != null && !miners.isEmpty()) {
            for (int i = 0; i < miners.size(); i++) {
                if (miners.get(i).getId().equals(id) && miners.get(i).getEndpoint().equals(endpoint)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void SetViewIdMiner(Activity activity){
        if(activity != null && miners != null && !miners.isEmpty()){
            IdMinerAdapter adapter = new IdMinerAdapter(activity, miners);
            list_id_miner.setAdapter(adapter);
        }
    }

    private void ResetActiveIdMiner(){
        if(miners != null && !miners.isEmpty()) {
            for (int i = 0; i < miners.size(); i++) {
                miners.get(i).setActive(false);
            }
        }
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

    interface OnGetPoolSettingsListenner{
        void onFinished(boolean isCheckSuccess);
    }
    private void GetPoolSettings(String id, final OnGetPoolSettingsListenner onGetPoolSettingsListenner){
        retrofit = new Retrofit.Builder()
                .baseUrl(miner.getEndpoint())
                .client(CustomApp.getHttpClient())
                .build();
        WebService ws = retrofit.create(WebService.class);
        Call<ResponseBody> result = ws.GetPoolSetings(id);
        final Activity activity = getActivity();
        if(getListener() != null){
            getListener().showProgress();
        }
        result.enqueue(new Callback<ResponseBody>() {
            private boolean isCheckSuccess = false;

            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String jsonStr = response.body().string();
                    dataSettings = new Settings();
                    if (activity != null) {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        if (jsonObj != null && jsonObj.get("status").equals("OK")) {
                            // Getting JSON Array node
                            JSONObject settings = jsonObj.getJSONObject("data");
                            String email = settings.getString("email");
                            double minPayout = settings.getDouble("minPayout") / 1000000000;
                            minPayout = minPayout / 1000000000;
                            String ip = settings.getString("ip");
                            int monitor = settings.getInt("monitor");
                            dataSettings.setEmail(email);
                            dataSettings.setIp(ip);
                            dataSettings.setPayout(minPayout);
                            dataSettings.setMonitor(monitor);
                            miner.setSettings(dataSettings);
                            myPreferences.UpdateMiner(miner);
                            isCheckSuccess = true;
                        } else {
                            final String err = jsonObj.get("error").toString();
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CustomApp.showToast(err);
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception ex){
                    if (activity != null) {
                        CustomApp.showToast("Couldn't get data from server!");
                    }
                }

                if(activity != null) {
                    SetViewIdMiner(activity);
                }

                if(onGetPoolSettingsListenner != null){
                    onGetPoolSettingsListenner.onFinished(isCheckSuccess);
                }

                if(getListener() != null){
                    getListener().hideProgress();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (activity != null) {
                    CustomApp.showToast("Couldn't get data from server!");
                }

                if(onGetPoolSettingsListenner != null){
                    onGetPoolSettingsListenner.onFinished(isCheckSuccess);
                }

                if(getListener() != null){
                    getListener().hideProgress();
                }
            }
        });
    }

    private class GetSettings extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(getListener() != null){
                getListener().showProgress();
            }
        }

        @Override
        protected Boolean doInBackground(String... url) {
            HttpHandler sh = new HttpHandler();
            final Activity activity = getActivity();
            try {
                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url[0]);
                dataSettings = new Settings();
                if (jsonStr != null && activity != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);
                        if(jsonObj != null && jsonObj.get("status").equals("OK")) {
                            // Getting JSON Array node
                            JSONObject settings = jsonObj.getJSONObject("data");
                            String email = settings.getString("email");
                            double minPayout = settings.getDouble("minPayout") / 1000000000;
                            minPayout = minPayout / 1000000000;
                            String ip = settings.getString("ip");
                            int monitor = settings.getInt("monitor");
                            dataSettings.setEmail(email);
                            dataSettings.setIp(ip);
                            dataSettings.setPayout(minPayout);
                            dataSettings.setMonitor(monitor);
                            miner.setSettings(dataSettings);
                            myPreferences.UpdateMiner( miner);
                            return true;
                        }
                        else {
                            final String err = jsonObj.get("error").toString();
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CustomApp.showToast(err);
                                    }
                                });
                            }
                        }
                    } catch (final JSONException e ) {
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CustomApp.showToast("Data parsing error");
                                }
                            });
                        }
                    }
                } else {
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CustomApp.showToast("Couldn't get data from server!");
                            }
                        });
                    }
                }
            } catch (Exception ex){
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CustomApp.showToast("Couldn't get data from server!");
                        }
                    });
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if(result != null && result){
//                SetViewIdMiner();
            }
            edt_id_miner.setText("");
            if(getListener() != null){
                getListener().hideProgress();
            }

        }
    }

    public interface IProgressDisplay {

        void showProgress();

        void hideProgress();

        void TabMinerSelected();

        void UnlockItemMenu();
    }
}

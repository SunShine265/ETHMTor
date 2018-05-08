package com.goodproductssoft.minningpool.activitys;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.concurrent.ExecutionException;

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
        button_mining_pool = (Button)view.findViewById(R.id.button_mining_pool);
        edt_id_miner = (EditText) view.findViewById(R.id.edt_id_miner);
        list_id_miner = (ListView) view.findViewById(R.id.list_id_miner);
        account_etc = (RadioButton) view.findViewById(R.id.account_etc);
        account_eth = (RadioButton) view.findViewById(R.id.account_eth);
        rg_account = (RadioGroup) view.findViewById(R.id.rg_account);
        list_suggestions = (LinearLayout) view.findViewById(R.id.list_suggestions);
        container_id_suggestions = (LinearLayout) view.findViewById(R.id.container_id_suggestions);

//        dataSettings = new Settings();
        myPreferences = new MyPreferences();
        if(getActivity() != null) {
            miners = myPreferences.GetIdMiners(getActivity());
        }
        button_mining_pool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddMiner();
            }
        });
        if(getActivity() != null) {
            idSuggestsions = myPreferences.GetIdSuggestsions(getActivity());
            list_suggestions.removeAllViews();
            if(idSuggestsions != null && idSuggestsions.size() > 0) {
                container_id_suggestions.setVisibility(View.VISIBLE);
                for (int i = idSuggestsions.size() - 1; i >= 0; i--) {
                    View idSuggestsionsView = inflater.inflate(R.layout.item_id_suggestsion, container, false);
                    TextView id_miner = (TextView) idSuggestsionsView.findViewById(R.id.id_miner);
                    TextView title_coin = (TextView) idSuggestsionsView.findViewById(R.id.title_coin);
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
            final  AsyncTask<String, Void, Boolean> asyncTask = new GetSettings().execute(urlData);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final Activity activity = getActivity();
                    try {
                        final Boolean result = asyncTask.get();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (result != null && result && activity != null) {
                                    // add only one key
                                    miner.setId(strIDMiner);
                                    miner.setActive(true);
                                    miner.setNotification(true);

                                    if (getListener() != null) {
                                        getListener().TabMinerSelected();
                                    }

                                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    Fragment fragment = new FragmentMiner();
                                    fragmentTransaction.replace(R.id.fragment_content, fragment);
                                    fragmentTransaction.commit();
                                    Miner previousMiner = GetMinerIdActive();
                                    ResetActiveIdMiner();
                                    myPreferences.AddIdMiner(activity, miner);
                                    try {
                                        if (previousMiner != null
                                                && (!previousMiner.getId().equals(miner.getId())
                                                || !previousMiner.getType().equals(miner.getType()))) {
                                            idSuggestsion = new IdSuggestsion();
                                            idSuggestsion.setType(previousMiner.getType());
                                            idSuggestsion.setId(previousMiner.getId());
                                            ArrayList<IdSuggestsion> listIdSuggestsions = myPreferences.GetIdSuggestsions(activity);
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
                                            myPreferences.SaveIdSuggestsions(activity, listIdSuggestsions);
                                        }
                                    } catch (Exception ex) {
                                        Toast.makeText(activity, "Sorry, can't store recently wallet address!", Toast.LENGTH_LONG).show();
                                    }

                                    InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                    im.hideSoftInputFromWindow(edt_id_miner.getWindowToken(), 0);
                                }
                            }
                        });
                    } catch (InterruptedException ex) {
                        Toast.makeText(activity, "Sorry, can't add Id!", Toast.LENGTH_LONG).show();
                    } catch (ExecutionException ex) {
                        Toast.makeText(activity, "Sorry, can't add Id!", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }
    }

    public void CheckInitContent(Context a){
        miner = GetMinerIdActive();
        if(miner != null) {
            String urlWorker = miner.getEndpoint() + "/miner/" + miner.getId() + "/settings";
            new FragmentPoolSettings.GetSettings().execute(urlWorker);
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

    private void SetViewIdMiner(){
        if(miners != null && !miners.isEmpty()){
            IdMinerAdapter adapter = new IdMinerAdapter(getActivity(), miners);
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

    private void GetPoolSettings(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .build();
        WebService ws = retrofit.create(WebService.class);
        Call<ResponseBody> result = ws.getPoolSetings(miner.getId());
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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
                if (jsonStr != null) {
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
                            myPreferences.UpdateMiner(getActivity(), miner);
                            return true;
                        }
                        else {
                            final String err = jsonObj.get("error").toString();
                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                err,
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });
                            }
                        }
                    } catch (final JSONException e ) {
                        if (getActivity() != null) {
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
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity.getApplicationContext(),
                                        "Couldn't get data from server!",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                    }
                }
            } catch (Exception ex){
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity.getApplicationContext(),
                                    "Couldn't get data from server!",
                                    Toast.LENGTH_LONG)
                                    .show();
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
                SetViewIdMiner();
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
    }
}

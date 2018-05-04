package com.goodproductssoft.flypoolmonitor.activitys;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.goodproductssoft.flypoolmonitor.HttpHandler;
import com.goodproductssoft.flypoolmonitor.MyPreferences;
import com.goodproductssoft.flypoolmonitor.R;
import com.goodproductssoft.flypoolmonitor.adapters.IdMinerAdapter;
import com.goodproductssoft.flypoolmonitor.models.Miner;
import com.goodproductssoft.flypoolmonitor.models.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class FragmentPoolSettings extends Fragment {
    EditText edt_id_miner;
    Button button_mining_pool;
    MyPreferences myPreferences;
    Miner miner = null;
    Settings dataSettings;
    ListView list_id_miner;
    static String endpoint = "https://api-zcash.flypool.org";
    ArrayList<Miner> miners;

    IProgressDisplay getListener(){
        if(getActivity() instanceof IProgressDisplay) {
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

//        dataSettings = new Settings();
        myPreferences = new MyPreferences();
        miners = myPreferences.GetIdMiners(getActivity());
//        miner = new Miner();
        button_mining_pool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miner = new Miner();
                String strIDMiner = edt_id_miner.getText().toString();
                if (!strIDMiner.isEmpty()) {
                    try {
                        String urlData = endpoint + "/miner/" + edt_id_miner.getText().toString() + "/settings";
                        Boolean result = new GetSettings().execute(urlData).get();
                        if (result != null && result) {
//                            email.setText(dataSettings.getEmail());
//                            ip.setText(dataSettings.getIp());
//                            payout.setText(String.valueOf(dataSettings.getPayout()));
                            if(!CheckExitIDMiner(strIDMiner, endpoint)) {
                                // add only one key
                                miner.setEndpoint(endpoint);
                                miner.setId(strIDMiner);
                                ResetActiveIdMiner();
                                miner.setActive(true);
                                miner.setNotification(true);
                                myPreferences.AddIdMiner(getActivity(), miner);
                            }
//                            SetViewIdMiner();
                            if(getListener() != null){
                                getListener().TabMinerSelected();
                            }
                            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                            Fragment fragment = new FragmentMiner();
                            fragmentTransaction.replace(R.id.fragment_content, fragment);
                            fragmentTransaction.commit();
                        }
//                    }
                    } catch (Exception ex) {
                    }
                }
            }
        });
        CheckInitContent(getActivity());
        return view;
    }

    public void CheckInitContent(Context a){
        miner = GetMinerIdActive();
        if(miner != null) {
//            SetViewIdMiner();
            String urlWorker = miner.getEndpoint() + "/miner/" + miner.getId() + "/settings";
            new FragmentPoolSettings.GetSettings().execute(urlWorker);
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

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        Miner minerActive = GetMinerIdActive();
//        if(minerActive != null) {
//            SetViewIdMiner();
//            try {
//                    String urlData = minerActive.getEndpoint() + "/miner/" + minerActive.getId() + "/settings";
//                    Boolean result = new GetSettings().execute(urlData).get();
//                    if (result != null && result) {
//                        email.setText(dataSettings.getEmail());
//                        ip.setText(dataSettings.getIp());
//                        payout.setText(String.valueOf(dataSettings.getPayout()));
//                    }
//            } catch (Exception ex) {
//            }
//        }
//    }

    private class GetSettings extends AsyncTask<String, Boolean, Boolean> {

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

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url[0]);
            dataSettings = new Settings();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    // Getting JSON Array node
                    JSONObject settings = jsonObj.getJSONObject("data");
                    String email = settings.getString("email");
                    double minPayout = settings.getDouble("minPayout") / 100000000;
                    String ip = settings.getString("ip");
                    dataSettings.setEmail(email);
                    dataSettings.setIp(ip);
                    dataSettings.setPayout(minPayout);
                    miner.setSettings(dataSettings);
                    myPreferences.UpdateMiner(getContext(), miner);
                    return true;
                } catch (final JSONException e) {
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
            } else {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Couldn't get data from server!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (result != null && result) {
                SetViewIdMiner();
//                email.setText(dataSettings.getEmail());
//                ip.setText(dataSettings.getIp());
//                payout.setText(String.valueOf(dataSettings.getPayout()));
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

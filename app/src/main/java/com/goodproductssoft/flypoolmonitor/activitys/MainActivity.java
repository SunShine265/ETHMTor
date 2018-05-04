package com.goodproductssoft.flypoolmonitor.activitys;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.goodproductssoft.flypoolmonitor.MyPreferences;
import com.goodproductssoft.flypoolmonitor.OnBroadcastService;
import com.goodproductssoft.flypoolmonitor.R;
import com.goodproductssoft.flypoolmonitor.models.Miner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FragmentMiner.ProgressDisplay,
        FragmentWorker.ProgressDisplay, FragmentPayouts.ProgressDisplay, FragmentPoolSettings.IProgressDisplay {
    //SharedPreferences pref;
    ImageView btnWorker, btnMiner, btnPayouts, btnSettings;
    RelativeLayout progressbar;
    MyPreferences myPreferences;
    ArrayList<Miner> miners;
    LinearLayout tab_settings, tab_payouts, tab_workers, tab_miner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnWorker = (ImageView) findViewById(R.id.btn_worker);
        btnMiner = (ImageView)findViewById(R.id.btn_miner);
        btnSettings = (ImageView)findViewById(R.id.btn_pool_settings);
        btnPayouts = (ImageView) findViewById(R.id.btn_payouts);
        progressbar = (RelativeLayout) findViewById(R.id.progressbar);
        tab_settings = (LinearLayout) findViewById(R.id.tab_settings);
        tab_payouts = (LinearLayout) findViewById(R.id.tab_payouts);
        tab_workers = (LinearLayout) findViewById(R.id.tab_workers);
        tab_miner = (LinearLayout) findViewById(R.id.tab_miner);

        myPreferences = new MyPreferences();
        miners = myPreferences.GetIdMiners(MainActivity.this);
        Miner minerActive = GetMinerIdActive();
        if(minerActive != null ) {
            TabMinerSelected();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment fragment = new FragmentMiner();
            fragmentTransaction.replace(R.id.fragment_content, fragment);
            fragmentTransaction.commit();
        } else {
            TabSettingsSelected();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment fragment = new FragmentPoolSettings();
            fragmentTransaction.replace(R.id.fragment_content, fragment);
            fragmentTransaction.commit();
        }
        final View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View view) {
                //Fragment fragment = null;
                if(view == findViewById(R.id.btn_worker)){
                    TabWorkersSelected();
                    FragmentWorker fragment = new FragmentWorker();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_content, fragment);
                    transaction.commit();
                } else if(view == findViewById(R.id.btn_miner)){
                    TabMinerSelected();
                    FragmentMiner fragment = new FragmentMiner();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_content, fragment);
                    transaction.commit();
                }
                else if(view == findViewById(R.id.btn_payouts)){
                    TabPayoutsSelected();
                    FragmentPayouts fragment = new FragmentPayouts();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_content, fragment);
                    transaction.commit();
                }
                else {
                    TabSettingsSelected();
                    FragmentPoolSettings fragment = new FragmentPoolSettings();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_content, fragment);
                    transaction.commit();
                }
            }
        };

        btnWorker.setOnClickListener(listener);
        btnMiner.setOnClickListener(listener);
        btnSettings.setOnClickListener(listener);
        btnPayouts.setOnClickListener(listener);

        int time_for_repeate = 1000 * 120;
        AlarmManager processTimer = (AlarmManager)getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, OnBroadcastService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1012,  intent, PendingIntent.FLAG_UPDATE_CURRENT);
        processTimer.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),time_for_repeate, pendingIntent);
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
    private void TabSettingsSelected(){
        tab_settings.setBackgroundResource(R.color.background_selected);
        tab_payouts.setBackgroundResource(R.color.background_header);
        tab_workers.setBackgroundResource(R.color.background_header);
        tab_miner.setBackgroundResource(R.color.background_header);
    }

    private void TabPayoutsSelected(){
        tab_settings.setBackgroundResource(R.color.background_header);
        tab_payouts.setBackgroundResource(R.color.background_selected);
        tab_workers.setBackgroundResource(R.color.background_header);
        tab_miner.setBackgroundResource(R.color.background_header);
    }

    private void TabWorkersSelected(){
        tab_settings.setBackgroundResource(R.color.background_header);
        tab_payouts.setBackgroundResource(R.color.background_header);
        tab_workers.setBackgroundResource(R.color.background_selected);
        tab_miner.setBackgroundResource(R.color.background_header);
    }

    @Override
    public void TabMinerSelected(){
        tab_settings.setBackgroundResource(R.color.background_header);
        tab_payouts.setBackgroundResource(R.color.background_header);
        tab_workers.setBackgroundResource(R.color.background_header);
        tab_miner.setBackgroundResource(R.color.background_selected);
    }

    @Override
    public void showProgress(){
        progressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress(){
        progressbar.setVisibility(View.GONE);
    }
}

package com.goodproductssoft.minningpool.activitys;

import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.goodproductssoft.minningpool.MyPreferences;
import com.goodproductssoft.minningpool.OnBroadcastService;
import com.goodproductssoft.minningpool.R;
import com.goodproductssoft.minningpool.models.Miner;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements FragmentMiner.ProgressDisplay,
        FragmentWorker.ProgressDisplay, FragmentPayouts.ProgressDisplay, FragmentPoolSettings.IProgressDisplay {
    //SharedPreferences pref;
    MyPreferences myPreferences;
    ArrayList<Miner> miners;
    ImageView btnWorker, btnMiner, btnPayouts, btnSettings;
    RelativeLayout progressbar;
    LinearLayout tab_settings, tab_payouts, tab_workers, tab_miner;

    private AdView mAdView;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd rewardedVideoAd;

    final static String ADMOB_APP_ID = "ca-app-pub-1827062885697339~6560780679";
    private static final String ADMOB_AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-1827062885697339/8801120573";
    private static final String ADMOB_AD_UNIT_ID_REWARDEDVIDEO = "ca-app-pub-1827062885697339/8995372320";
    public final static int MAX_SHOW_ADS_REMAIN_TIMES = 30;
    public final static int MIN_SHOW_ADS_REMAIN_TIMES = 20;
    public final static boolean IS_SHOW_ADS = false;

    Handler UIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, ADMOB_APP_ID);

        UIHandler = new Handler();

        btnWorker = (ImageView) findViewById(R.id.btn_worker);
        btnMiner = (ImageView)findViewById(R.id.btn_miner);
        btnSettings = (ImageView)findViewById(R.id.btn_pool_settings);
        btnPayouts = (ImageView) findViewById(R.id.btn_payouts);
        progressbar = (RelativeLayout) findViewById(R.id.progressbar);
        tab_settings = (LinearLayout) findViewById(R.id.tab_settings);
        tab_payouts = (LinearLayout) findViewById(R.id.tab_payouts);
        tab_workers = (LinearLayout) findViewById(R.id.tab_workers);
        tab_miner = (LinearLayout) findViewById(R.id.tab_miner);
        mAdView = findViewById(R.id.adView);

        myPreferences = new MyPreferences();
        miners = myPreferences.GetIdMiners(MainActivity.this);
//        pref = MainActivity.this.getSharedPreferences("MyPref", 0);
//        String idMiner = pref.getString("ID_MINER", "");
        Miner minerActive = GetMinerIdActive();

        if(minerActive != null) {
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

        AlarmManager processTimer = (AlarmManager)getSystemService(ALARM_SERVICE);
        //Intent cancelIntent = new Intent(MainActivity.this, OnBroadcastService.class);
        //PendingIntent pendingCancelIntent = PendingIntent.getBroadcast(MainActivity.this, 1012,  cancelIntent, PendingIntent.FLAG_NO_CREATE);
        //if(pendingCancelIntent == null) {
            int time_for_repeate = 1000 * 120;
            Intent intent = new Intent(MainActivity.this, OnBroadcastService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1012, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            processTimer.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), time_for_repeate, pendingIntent);
        //}
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(IS_SHOW_ADS) {
            if (mAdView.getVisibility() == View.GONE) {
                SetBannerAds();
            }
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);

        if(IS_SHOW_ADS) {
            if (fragment instanceof FragmentMiner
                    || fragment instanceof FragmentPayouts
                    || fragment instanceof FragmentWorker
                    || fragment instanceof FragmentPoolSettings) {
                ShowAds();
            }
        }
    }

    @Override
    public void onAttachFragment(android.support.v4.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        if(IS_SHOW_ADS) {
//        if(fragment instanceof FragmentMiner
//                || fragment instanceof FragmentPayouts
//                || fragment instanceof FragmentWorker
//                || fragment instanceof FragmentPoolSettings) {
            ShowAds();
//        }
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

    private void SetBannerAds(){
        mAdView.setVisibility(View.GONE);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mAdView.setVisibility(View.VISIBLE);
                // Code to be executed when an ad finishes loading.
                //Toast.makeText(MainActivity.this, "finished loading", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                mAdView.setVisibility(View.GONE);
                if(errorCode == 3){
                    ShowAds();
                }
                // Code to be executed when an ad request fails.
                //Toast.makeText(MainActivity.this, "failed loading", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }

    private boolean lockedAds = false;
    private void ShowAds() {
        MyPreferences myPreferences = new MyPreferences();
        long remainTimes = myPreferences.getShowAdsRemainTimes(this);
        if (remainTimes > 0) {
            myPreferences.setShowAdsRemainTimes(this, remainTimes - 1);
        } else if(!lockedAds) {
            lockedAds = true;
            UIHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        GetIntersitialAds();
                    }
                    catch (Exception ex){
                    }
                    try {
                        UIHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                lockedAds = false;
                            }
                        }, 5000);                    }
                    catch (Exception ex){
                        lockedAds = false;
                    }
                }
            }, 4000);
        }
    }

    /**
     * Add Intersitial Ads
     * */
    private void GetIntersitialAds(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(ADMOB_AD_UNIT_ID_INTERSTITIAL);

        // Set an AdListener.
        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdLoaded() {
                if (mInterstitialAd.isLoaded()) {
                    MyPreferences myPreferences = new MyPreferences();
                    myPreferences.setShowAdsRemainTimes(MainActivity.this, new Random().nextInt(MAX_SHOW_ADS_REMAIN_TIMES - MIN_SHOW_ADS_REMAIN_TIMES + 1) + MIN_SHOW_ADS_REMAIN_TIMES);
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (i == 3) {
                    GetRewardedVideoAds();
                }
            }

            @Override
            public void onAdClosed() {
            }
        });

        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    /**
     * ADd RewardedVideo Ads
     * */
    private void GetRewardedVideoAds() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener()
        {
            @Override
            public void onRewardedVideoAdLeftApplication() {
            }

            @Override
            public void onRewardedVideoAdClosed() {
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int errorCode) {
            }

            @Override
            public void onRewardedVideoAdLoaded() {
                showRewardedVideo();
            }

            @Override
            public void onRewardedVideoAdOpened() {
            }

            @Override
            public void onRewarded(RewardItem reward) {
            }

            @Override
            public void onRewardedVideoStarted() {
            }

            @Override
            public void onRewardedVideoCompleted() {
            }
        });

        loadRewardedVideoAd();
        showRewardedVideo();
    }

    private void loadRewardedVideoAd() {
        if (!rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(ADMOB_AD_UNIT_ID_REWARDEDVIDEO, new AdRequest.Builder().build());
        }
    }

    private void showRewardedVideo() {
        if (rewardedVideoAd.isLoaded()) {
            MyPreferences myPreferences = new MyPreferences();
            myPreferences.setShowAdsRemainTimes(MainActivity.this, new Random().nextInt(MAX_SHOW_ADS_REMAIN_TIMES - MIN_SHOW_ADS_REMAIN_TIMES + 1) + MIN_SHOW_ADS_REMAIN_TIMES);
            rewardedVideoAd.show();
        }
    }
}

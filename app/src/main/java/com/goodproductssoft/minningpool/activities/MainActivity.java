package com.goodproductssoft.minningpool.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applovin.sdk.AppLovinSdk;
import com.goodproductssoft.minningpool.CustomApp;
import com.goodproductssoft.minningpool.MyPreferences;
import com.goodproductssoft.minningpool.OnBroadcastService;
import com.goodproductssoft.minningpool.R;
import com.goodproductssoft.minningpool.models.Miner;
import com.goodproductssoft.minningpool.util.IabHelper;
import com.goodproductssoft.minningpool.util.IabResult;
import com.goodproductssoft.minningpool.util.Inventory;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailabilityLight;
import com.mopub.mobileads.dfp.adapters.MoPubAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.google.android.gms.ads.AdSize.BANNER;
import static com.google.android.gms.ads.AdSize.FULL_BANNER;
import static com.google.android.gms.ads.AdSize.LARGE_BANNER;
import static com.google.android.gms.ads.AdSize.LEADERBOARD;
import static com.google.android.gms.ads.AdSize.SMART_BANNER;

//import com.mopub.mobileads.MoPubErrorCode;
//import com.mopub.mobileads.MoPubView;

public class MainActivity extends AppCompatActivity implements FragmentMiner.ProgressDisplay,
        FragmentWorker.ProgressDisplay, FragmentPayouts.ProgressDisplay, FragmentPoolSettings.IProgressDisplay{
    //SharedPreferences pref;
    ArrayList<Miner> miners;
    ImageView btnWorker, btnMiner, btnPayouts, btnSettings, icon_app;
    TextView title_app;
    RelativeLayout progressbar;
    LinearLayout tab_settings, tab_payouts, tab_workers, tab_miner, id_ads_app;

    private AdView mAdView;
//    private MoPubView mAdView;
    private InterstitialAd mInterstitialAd;
    private RewardedVideoAd rewardedVideoAd;
//    com.facebook.ads.AdView adViewFacebook;

    private static final String ADMOB_AD_UNIT_ID_BANNER = "ca-app-pub-1827062885697339/3931937276";
    private static final String ADMOB_AD_UNIT_ID_LEADERBOARD = "ca-app-pub-1827062885697339/5704398884";
    private static final String ADMOB_AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-1827062885697339/8801120573";
    private static final String ADMOB_AD_UNIT_ID_REWARDEDVIDEO = "ca-app-pub-1827062885697339/8995372320";

    public final static int MAX_SHOW_ADS_REMAIN_TIMES = 19;
    public final static int MIN_SHOW_ADS_REMAIN_TIMES = 15;
    public final static boolean IS_SHOW_ADS = true;
    public final static int SHOW_RATE_REMAIN_TIMES = 20;

    Handler UIHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UIHandler = new Handler();

        try {
            MobileAds.initialize(this, CustomApp.ADMOB_APP_ID);
            AppLovinSdk.initializeSdk(this);
        } catch (Exception ex){ }

        setContentView(R.layout.activity_main);

//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String hashKey = new String(Base64.encode(md.digest(), 0));
//                Log.i("printHashKey", "printHashKey() Hash Key: " + hashKey);
//            }
//        } catch (Exception e) {
//            Log.e("printHashKey", "printHashKey()", e);
//        }

        final IabHelper mHelper = CustomApp.getInstance().createIaHelper();
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
                                   public void onIabSetupFinished(IabResult result) {
                                       if (result != null && result.isSuccess()) {
                                           try {
                                               ArrayList<String> items = new ArrayList<>();
                                               items.add(CustomApp.ADS_ITEM_SKU);
                                               mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                                                           @Override
                                                           public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                                                       try {
                                                           if (inventory.hasPurchase(CustomApp.ADS_ITEM_SKU)) {
//                                                   //Remove purchase for test devices
//                                                   Purchase premiumPurchase = inventory.getPurchase(CustomApp.ADS_ITEM_SKU);
//                                                   mHelper.consumeAsync(premiumPurchase, new IabHelper.OnConsumeFinishedListener() {
//                                                       @Override
//                                                       public void onConsumeFinished(Purchase purchase, IabResult result) {
//                                                       }
//                                                   });
                                                               MyPreferences myPreferences = MyPreferences.getInstance();
                                                               myPreferences.setRemoveAds(true);
                                                               hideBannerAds();
                                                           } else {
                                                               MyPreferences myPreferences = MyPreferences.getInstance();
                                                               myPreferences.setRemoveAds(false);
                                                           }
                                                       }
                                                       catch (Exception ex){}
                                                           }
                                               });
                                           }
                                           catch (Exception ex){
                                           }
                                       }
                                   }
                               });




        btnWorker = findViewById(R.id.btn_worker);
        btnMiner = findViewById(R.id.btn_miner);
        btnSettings = findViewById(R.id.btn_pool_settings);
        btnPayouts = findViewById(R.id.btn_payouts);
        progressbar = findViewById(R.id.progressbar);
        tab_settings = findViewById(R.id.tab_settings);
        tab_payouts = findViewById(R.id.tab_payouts);
        tab_workers = findViewById(R.id.tab_workers);
        tab_miner = findViewById(R.id.tab_miner);
        id_ads_app = findViewById(R.id.id_ads_app);
        icon_app = findViewById(R.id.icon_app);
        title_app = findViewById(R.id.title_app);

        miners = MyPreferences.getInstance().GetIdMiners();
        Miner minerActive = GetMinerIdActive();

        if(minerActive != null) {
            TabMinerSelected();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment fragment = new FragmentMiner();
            fragmentTransaction.replace(R.id.fragment_content, fragment);
            fragmentTransaction.commitAllowingStateLoss();

            UnlockItemMenu();
        } else {
            TabSettingsSelected();
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            Fragment fragment = new FragmentPoolSettings();
            fragmentTransaction.replace(R.id.fragment_content, fragment);
            fragmentTransaction.commitAllowingStateLoss();
            LockItemMenu();
        }

        id_ads_app.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    title_app.setTextColor(getResources().getColor(R.color.color_press_primary));
                    icon_app.setImageResource(R.drawable.menu_press);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    title_app.setTextColor(getResources().getColor(R.color.colorWhite));
                    icon_app.setImageResource(R.drawable.menu);

                    try {
                        //Close keyBoard in transition
                        InputMethodManager inputManager = (InputMethodManager) MainActivity.this.getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    catch (Exception ex){}

                    TabAdsAppSelected();
                    FragmentAdsApp fragment = new FragmentAdsApp();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_content, fragment);
                    transaction.commitAllowingStateLoss();

                }
                return true;
            }
        });
        final View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View view) {
                //Fragment fragment = null;

                try {
                    //Close keyBoard in transition
                    InputMethodManager inputManager = (InputMethodManager) MainActivity.this.getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                }
                catch (Exception ex){}

                if(view == findViewById(R.id.btn_worker)){
                    TabWorkersSelected();
                    FragmentWorker fragment = new FragmentWorker();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_content, fragment);
                    transaction.commitAllowingStateLoss();
                } else if(view == findViewById(R.id.btn_miner)){
                    TabMinerSelected();
                    FragmentMiner fragment = new FragmentMiner();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_content, fragment);
                    transaction.commitAllowingStateLoss();
                }
                else if(view == findViewById(R.id.btn_payouts)){
                    TabPayoutsSelected();
                    FragmentPayouts fragment = new FragmentPayouts();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_content, fragment);
                    transaction.commitAllowingStateLoss();
                }
                else {
                    TabSettingsSelected();
                    FragmentPoolSettings fragment = new FragmentPoolSettings();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_content, fragment);
                    transaction.commitAllowingStateLoss();
                }
            }
        };

        btnWorker.setOnClickListener(listener);
        btnMiner.setOnClickListener(listener);
        btnSettings.setOnClickListener(listener);
        btnPayouts.setOnClickListener(listener);

        AlarmManager processTimer = (AlarmManager)getSystemService(ALARM_SERVICE);
        int time_for_repeate = 1000 * 120;
        Intent intent = new Intent(MainActivity.this, OnBroadcastService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 1012, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        processTimer.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), time_for_repeate, pendingIntent);

//        checkGooglePlayServices();

        setViewMode();
        showAds();
    }

//    private void setMopubBannerAds() {
//        MoPubView moPubView = new MoPubView(this);
//        moPubView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        moPubView.setAdUnitId("98edab2ef5564532a1060d529a23345b"); // Enter your Ad Unit ID from www.mopub.com
////        moPubView.setTesting(true);
//        moPubView.setBannerAdListener(new MoPubView.BannerAdListener() {
//            @Override
//            public void onBannerLoaded(MoPubView banner) {
//
//            }
//
//            @Override
//            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
//
//            }
//
//            @Override
//            public void onBannerClicked(MoPubView banner) {
//
//            }
//
//            @Override
//            public void onBannerExpanded(MoPubView banner) {
//
//            }
//
//            @Override
//            public void onBannerCollapsed(MoPubView banner) {
//
//            }
//        });
//        ((ViewGroup)findViewById(R.id.adView)).removeAllViews();
//        ((ViewGroup)findViewById(R.id.adView)).addView(moPubView);
//        moPubView.loadAd();
//    }

    @Override
    public void onAttachFragment(android.support.v4.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        if(IS_SHOW_ADS) {
//        if(fragment instanceof FragmentMiner
//                || fragment instanceof FragmentPayouts
//                || fragment instanceof FragmentWorker
//                || fragment instanceof FragmentPoolSettings) {
            showAds();
//        }
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof FragmentMiner
                || fragment instanceof FragmentPayouts
                || fragment instanceof FragmentWorker
                || fragment instanceof FragmentPoolSettings) {
            try {
                MyPreferences myPreferences = MyPreferences.getInstance();
                long remainTimes = Math.min(SHOW_RATE_REMAIN_TIMES, myPreferences.getShowRateRemainTimes());
                if (remainTimes > 0) {
                    remainTimes--;
                    myPreferences.setShowRateRemainTimes(remainTimes);
                    if(remainTimes == 0) {
                        long remainAdsTimes = myPreferences.getShowAdsRemainTimes();
                        myPreferences.setShowAdsRemainTimes(Math.max(remainAdsTimes, 6));

                        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                        alertDialog.setTitle("Rate us!");
                        alertDialog.setMessage("Could you please rate us 5 stars? Thank you.");
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Not now ",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes, rate now",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                        MyPreferences.getInstance().setRateUsToStars(true);
                                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                                        try {
                                            startActivity(goToMarket);
                                        } catch (Exception e) {
                                            startActivity(new Intent(Intent.ACTION_VIEW,
                                                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                                        }
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            } catch (Exception ex) {}

            if (IS_SHOW_ADS) {
                showAds();
            }
        }
    }

    private void LockItemMenu(){
        btnPayouts.setEnabled(false);
        btnWorker.setEnabled(false);
        btnMiner.setEnabled(false);
        id_ads_app.setEnabled(false);
        btnMiner.setAlpha(0.4f);
        btnWorker.setAlpha(0.4f);
        btnPayouts.setAlpha(0.4f);
        id_ads_app.setAlpha(0.4f);
    }

    @Override
    public void UnlockItemMenu(){
        btnPayouts.setEnabled(true);
        btnWorker.setEnabled(true);
        btnMiner.setEnabled(true);
        id_ads_app.setEnabled(true);

        btnMiner.setAlpha(1f);
        btnWorker.setAlpha(1f);
        btnPayouts.setAlpha(1f);
        id_ads_app.setAlpha(1f);
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

    private void TabAdsAppSelected(){
        id_ads_app.setBackgroundResource(R.color.background_selected);
        tab_settings.setBackgroundResource(R.color.background_header);
        tab_payouts.setBackgroundResource(R.color.background_header);
        tab_workers.setBackgroundResource(R.color.background_header);
        tab_miner.setBackgroundResource(R.color.background_header);
    }

    private void TabSettingsSelected(){
        id_ads_app.setBackgroundResource(R.color.background_header);
        tab_settings.setBackgroundResource(R.color.background_selected);
        tab_payouts.setBackgroundResource(R.color.background_header);
        tab_workers.setBackgroundResource(R.color.background_header);
        tab_miner.setBackgroundResource(R.color.background_header);
    }

    private void TabPayoutsSelected(){
        id_ads_app.setBackgroundResource(R.color.background_header);
        tab_settings.setBackgroundResource(R.color.background_header);
        tab_payouts.setBackgroundResource(R.color.background_selected);
        tab_workers.setBackgroundResource(R.color.background_header);
        tab_miner.setBackgroundResource(R.color.background_header);
    }

    private void TabWorkersSelected(){
        id_ads_app.setBackgroundResource(R.color.background_header);
        tab_settings.setBackgroundResource(R.color.background_header);
        tab_payouts.setBackgroundResource(R.color.background_header);
        tab_workers.setBackgroundResource(R.color.background_selected);
        tab_miner.setBackgroundResource(R.color.background_header);
    }

    @Override
    public void TabMinerSelected(){
        id_ads_app.setBackgroundResource(R.color.background_header);
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

    private boolean isPaused = false;
    @Override
    protected void onResume() {
        super.onResume();
        try {
            isPaused = false;
            if (IS_SHOW_ADS && !MyPreferences.getInstance().getRemoveAds()) {
                if (findViewById(R.id.adView).getHeight() == 0 ||
                        timeBanner == null || timeBanner.getTime() + 1000 * 3600 < Calendar.getInstance().getTime().getTime()
                    //||  mAdView == null || mAdView.getVisibility() == View.GONE || mAdView.getHeight() == 0
                        ) {
                    showBannerAds();
                }
            }
        }
        catch (Exception ex){
        }
    }

    @Override
    protected void onPause() {
        isPaused = true;
        super.onPause();
    }

    @Override
    protected void onStop() {
        isPaused = true;
        super.onStop();
        try {
//            if (mAdView != null && mAdView.getParent() != null) {
//                mAdView.setVisibility(View.GONE);
//                ((ViewGroup)(mAdView.getParent())).removeView(mAdView);
//                mAdView.destroy();
//            }
        }
        catch (Exception e){}
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(IS_SHOW_ADS && !MyPreferences.getInstance().getRemoveAds()){
            ViewGroup adViews = (ViewGroup)findViewById(R.id.adView);
            try {
                for (int i = 0; i < adViews.getChildCount(); i++) {
                    AdView adView = (AdView) adViews.getChildAt(i);
                    adView.setVisibility(View.GONE);
//                    adView.destroy();
                }
            } catch (Exception ex){}
            adViews.removeAllViews();
            showBannerAds();
        }
    }

    public void hideBannerAds() {
        try {
            ViewGroup adViews = (ViewGroup)findViewById(R.id.adView);
            adViews.removeAllViews();
        } catch (Exception ex){}
        if(mAdView != null) {
            mAdView.setVisibility(View.GONE);
        }
    }

    private void showBannerAds() {
        showBannerAds(SMART_BANNER);
    }

    private void showBannerAds(final AdSize adSize) {
        showBannerAds(adSize, false);
    }

    private Date timeBanner = Calendar.getInstance().getTime();
    private long refreshTimes = 40 * 1000;

    private void showBannerAds(final AdSize adSize, boolean useBannerId){
        //TODO: Ads primary
        try {
//            if (mAdView != null) {
//                mAdView.destroy();
//                mAdView.setVisibility(View.GONE);
//                mAdView = null;
//            }
        }
        catch (Exception ex){}
        timeBanner = Calendar.getInstance().getTime();

        mAdView = new AdView(this);
        final AdView adViewFinal = mAdView;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(adSize == BANNER || adSize == LEADERBOARD? adSize.getWidthInPixels(this) : ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        adViewFinal.setLayoutParams(layoutParams);
        ((ViewGroup)findViewById(R.id.adView)).addView(adViewFinal);
        adViewFinal.setVisibility(View.GONE);
        adViewFinal.setAdSize(adSize);

        float uiScreenWidth = getResources().getDisplayMetrics().widthPixels;
        final float uiScreenWidthDp = uiScreenWidth / getResources().getDisplayMetrics().density;
        float uiScreenHeight = getResources().getDisplayMetrics().heightPixels;
        final float uiScreenHeightDp = uiScreenHeight / getResources().getDisplayMetrics().density;

        if (useBannerId){
            adViewFinal.setAdUnitId(ADMOB_AD_UNIT_ID_BANNER);
        }
        else {
            if(adSize == SMART_BANNER) {
                if (uiScreenWidthDp >= 730 && uiScreenHeightDp > 720) {
                    adViewFinal.setAdUnitId(ADMOB_AD_UNIT_ID_LEADERBOARD);
                } else {
                    adViewFinal.setAdUnitId(ADMOB_AD_UNIT_ID_BANNER);
                }
            } else if (adSize == LEADERBOARD) {
                adViewFinal.setAdUnitId(ADMOB_AD_UNIT_ID_LEADERBOARD);
            } else {
                adViewFinal.setAdUnitId(ADMOB_AD_UNIT_ID_BANNER);
            }
        }


//
        //TODO: bundle for AppLovin banners
        Bundle bundleAppLovin = new Bundle();

//        TODO: bundle for Mopub
        Bundle bundleMopub = new MoPubAdapter.BundleBuilder()
                .build();
//
//        //TODO: bundle for Ads facebook
////        Bundle extras = new FacebookAdapter.FacebookExtrasBundleBuilder()
////                .setNativeAdChoicesIconExpandable(false)
////                .build();
//
        AdRequest adRequest = new AdRequest.Builder()
                //.addNetworkExtrasBundle(ApplovinAdapter.class, bundleAppLovin)
                //.addNetworkExtrasBundle(MoPubAdapter.class, bundleMopub)
                .build();

        final AdSize[] bannerSizeLoading = {adSize};

        adViewFinal.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                bannerSizeLoading[0] = null;
                if (!MyPreferences.getInstance().getRemoveAds()) {
                    ViewGroup adViews = (ViewGroup)findViewById(R.id.adView);
                    boolean isOutSize = false;
                    try{
                        isOutSize = adViews.getWidth() + 100 < adViewFinal.getAdSize().getWidthInPixels(MainActivity.this);
                    } catch (Exception ex){ }

                    if(isOutSize){
                        try {
                            adViewFinal.setVisibility(View.GONE);
                            adViews.removeView(adViewFinal);
                        } catch (Exception ex){ }
                        Log.d("Ads" , "banner outsize: " +
                                (adViewFinal.getMediationAdapterClassName() != null? adViewFinal.getMediationAdapterClassName() + " " : "")
                                + adViewFinal.getAdSize().toString());
                    }
                    else {
                        try {
                            ArrayList<View> removeViews = new ArrayList<>();
                            boolean isAdded = false;
                            for (int i = 0; i < adViews.getChildCount(); i++) {
                                try {
                                    AdView adView = (AdView) adViews.getChildAt(i);
                                    if (adViewFinal != adView) {
                                        adView.setVisibility(View.GONE);
                                        removeViews.add(adView);
//                                adView.destroy();
                                    } else {
                                        isAdded = true;
                                    }
                                } catch (Exception exRemove){
                                }
                            }
                            if(!isAdded) {
                                adViews.addView(adViewFinal);
                            }
                            for (View removeView: removeViews) {
                                adViews.removeView(removeView);
                            }
                        } catch (Exception ex){}

                        adViewFinal.setVisibility(View.VISIBLE);
                        adViewFinal.bringToFront();

                        Log.d("Ads" , "banner show: " +
                                (adViewFinal.getMediationAdapterClassName() != null? adViewFinal.getMediationAdapterClassName() + " " : "")
                                + adViewFinal.getAdSize().toString());
                    }
                } else {
                    adViewFinal.setVisibility(View.GONE);
                    ((ViewGroup)findViewById(R.id.adView)).removeAllViews();
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                try {
                    if (adViewFinal.getParent() != null) {
                        AdSize currentBannerSizeLoading = bannerSizeLoading[0];
                        try {
                            if((adViewFinal.getVisibility() == View.VISIBLE && adViewFinal.getHeight() > 0)) {
                                currentBannerSizeLoading = null;
                            }
                            else if(((ViewGroup) findViewById(R.id.adView)).getChildCount() > 1 &&
                                    (adViewFinal.getVisibility() == View.GONE || adViewFinal.getHeight() == 0)) {
                                adViewFinal.setVisibility(View.GONE);
                                ((ViewGroup) findViewById(R.id.adView)).removeView(adViewFinal);
//                                adViewFinal.destroy();
                            }
                        } catch (Exception ex) {
                        }

                        if(isPaused) {
                            timeBanner = null;
                            return;
                        }

                        if (errorCode == AdRequest.ERROR_CODE_NO_FILL) {
                            if (currentBannerSizeLoading == null) {
                                showBannerAds(SMART_BANNER);
                            } else if (currentBannerSizeLoading == SMART_BANNER) {
                                if (ADMOB_AD_UNIT_ID_LEADERBOARD.equals(adViewFinal.getAdUnitId())) {
                                    showBannerAds(SMART_BANNER, true);
                                } else {
                                    if (uiScreenWidthDp >= 730) {
                                        showBannerAds(LEADERBOARD);
                                    } else {
                                        showBannerAds(BANNER);
                                    }
                                }
                            } else if (currentBannerSizeLoading == LEADERBOARD) {
                                showBannerAds(BANNER);
                            } else if (currentBannerSizeLoading == BANNER) {
                                if (uiScreenWidthDp >= 470) {
                                    showBannerAds(FULL_BANNER);
                                } else {
                                    if (uiScreenHeightDp >= 680) {
                                        showBannerAds(LARGE_BANNER);
                                    } else {
                                        UIHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                refreshBannerAds(adViewFinal);
                                            }
                                        }, refreshTimes);
                                    }
                                }
                            } else if (currentBannerSizeLoading == FULL_BANNER) {
                                if (uiScreenHeightDp >= 680) {
                                    showBannerAds(LARGE_BANNER);
                                } else {
                                    UIHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            refreshBannerAds(adViewFinal);
                                        }
                                    }, refreshTimes);
                                }
                            } else {
                                UIHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        refreshBannerAds(adViewFinal);
                                    }
                                }, refreshTimes);
                            }
                        } else {
                            UIHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    refreshBannerAds(adViewFinal);
                                }
                            }, refreshTimes);
                        }
                    } else {
                        UIHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refreshBannerAds(adViewFinal);
                            }
                        }, refreshTimes);
                    }
                }
                catch(Exception ex) {
                    UIHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshBannerAds(adViewFinal);
                        }
                    }, refreshTimes);
                }
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
//                if(adViewFinal != null){
//                    adViewFinal.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the user is about to return
                // to the app after tapping on an ad.
//                if(adViewFinal != null){
//                    adViewFinal.setVisibility(View.GONE);
//                }
            }
        });
        adViewFinal.loadAd(adRequest);
    }

    public void refreshBannerAds(AdView adView){
        try {
            if(isPaused) {
                timeBanner = null;
                return;
            }

            boolean isShowing = findViewById(R.id.adView).getHeight() > 0;
            if (IS_SHOW_ADS && !MyPreferences.getInstance().getRemoveAds()) {
                if(!isShowing) {
                    ((ViewGroup)findViewById(R.id.adView)).removeAllViews();
                    showBannerAds();
                }
                else if(adView != null && adView.getParent() != null
//                    && adView.getVisibility() == View.GONE
//                    && !adView.isLoading()
//                    && adView == mAdView)
                        ) {
                    showBannerAds();
                }

            }
        }
        catch (Exception ex){}
    }

    public boolean checkGooglePlayServices() {
        GoogleApiAvailabilityLight googleApiAvailability = GoogleApiAvailabilityLight.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
//                Dialog dialog = googleApiAvailability.getErrorDialog(activity, status, 1);
//                dialog.setTitle("This is a test title");
//                dialog.show();
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FragmentManager fragmentManager = getFragmentManager();
        if(fragmentManager != null) {
            Fragment fragment = fragmentManager.findFragmentById(R.id.fragment_content);
            if (fragment instanceof FragmentAdsApp) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private boolean lockedAds = false;
    private void showAds() {
        try {
            MyPreferences myPreferences = MyPreferences.getInstance();
            if (!myPreferences.getRemoveAds()) {
                long remainTimes = myPreferences.getShowAdsRemainTimes();
                if (remainTimes > 0) {
                    myPreferences.setShowAdsRemainTimes(remainTimes - 1);
                } else if (!lockedAds && UIHandler != null) {
                    lockedAds = true;
                    long remainAdsTimes = myPreferences.getShowRateRemainTimes();
                    if (remainAdsTimes > 0) {
                        myPreferences.setShowRateRemainTimes(Math.max(remainAdsTimes, 6));
                    }

                    UIHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getIntersitialAds();
                                UIHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        lockedAds = false;
                                    }
                                }, 10000);
                            } catch (Exception ex) {
                                lockedAds = false;
                            }
                        }
                    }, 2000);
                }
            }
        }
        catch (Exception ex){
            lockedAds = false;
        }
    }

    /**
     * Add Intersitial Ads
     * */
    public void getIntersitialAds(){
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
                    MyPreferences myPreferences = MyPreferences.getInstance();
                    myPreferences.setShowAdsRemainTimes(new Random().nextInt(MAX_SHOW_ADS_REMAIN_TIMES - MIN_SHOW_ADS_REMAIN_TIMES + 1) + MIN_SHOW_ADS_REMAIN_TIMES );
                    mInterstitialAd.show();
                }
            }

            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                if (i == 3) {
                    getRewardedVideoAds();
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
    private void getRewardedVideoAds() {
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
            MyPreferences myPreferences = MyPreferences.getInstance();
            myPreferences.setShowAdsRemainTimes(new Random().nextInt(MAX_SHOW_ADS_REMAIN_TIMES - MIN_SHOW_ADS_REMAIN_TIMES + 1) + MIN_SHOW_ADS_REMAIN_TIMES );
            rewardedVideoAd.show();
        }
    }

    private void setViewMode(){
        int mode = MyPreferences.getInstance().getViewModes();
        switch (mode){
            case 0:
                MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                break;
            case 1:
                MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case 2:
                MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
    }

//    private void setBannerAdsFacebook(){
//        adViewFacebook = new com.facebook.ads.AdView(this, "1885266801771264_1885266888437922", com.facebook.ads.AdSize.BANNER_HEIGHT_50);
//        ((ViewGroup)findViewById(R.id.adView)).removeAllViews();
//        ((ViewGroup)findViewById(R.id.adView)).addView(adViewFacebook);
//        adViewFacebook.setAdListener(new AbstractAdListener() {
//            @Override
//            public void onError(Ad ad, AdError adError) {
//                super.onError(ad, adError);
//                // Ad error callback
//                Toast.makeText(MainActivity.this, "Error: " + adError.getErrorMessage(), Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onAdLoaded(Ad ad) {
//                super.onAdLoaded(ad);
//            }
//
//            @Override
//            public void onAdClicked(Ad ad) {
//                super.onAdClicked(ad);
//            }
//
//            @Override
//            public void onLoggingImpression(Ad ad) {
//                super.onLoggingImpression(ad);
//            }
//        });
////        AdSettings.addTestDevice("HASHED ID");
//        adViewFacebook.loadAd();
//    }
}

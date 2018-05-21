package com.goodproductssoft.minningpool.activities;

import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.goodproductssoft.minningpool.CustomApp;
import com.goodproductssoft.minningpool.MyPreferences;
import com.goodproductssoft.minningpool.R;
import com.goodproductssoft.minningpool.util.IabHelper;
import com.goodproductssoft.minningpool.util.IabResult;
import com.goodproductssoft.minningpool.util.Inventory;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdsApp extends Fragment {
    LinearLayout remove_app, more_apps, feedback, donate, rate_us, view_modes;
    IabHelper mHelper;
    RadioButton both_landscape_portrait, landscape, portrait;
    RadioGroup rg_view_mode;

    public FragmentAdsApp() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ads_app, container, false);

        remove_app = view.findViewById(R.id.remove_app);
        more_apps = view.findViewById(R.id.more_apps);
        feedback = view.findViewById(R.id.feedback);
        donate = view.findViewById(R.id.donate);
        rate_us = (view).findViewById(R.id.rate_us);
        view_modes = (view).findViewById(R.id.view_modes);
        both_landscape_portrait = (view).findViewById(R.id.both_landscape_portrait);
        landscape = (view).findViewById(R.id.landscape);
        portrait = (view).findViewById(R.id.portrait);
        rg_view_mode = (view).findViewById(R.id.rg_view_mode);
        mHelper = CustomApp.getInstance().createIaHelper();

        mHelper.startSetup(new
                               IabHelper.OnIabSetupFinishedListener() {
                                   public void onIabSetupFinished(IabResult result) {
                                       if (result.isSuccess()) {
                                           try {
                                               ArrayList<String> items = new ArrayList<>();
                                               items.add(CustomApp.ADS_ITEM_SKU);
                                               Inventory inventory = mHelper.queryInventory(true, items);
                                               MyPreferences myPreferences = MyPreferences.getInstance();
                                               if(inventory.hasPurchase(CustomApp.ADS_ITEM_SKU)){
                                                   myPreferences.setRemoveAds(true);
                                                   try {
                                                       if(getActivity() instanceof  MainActivity){
                                                           ((MainActivity)getActivity()).hideBannerAds();
                                                       }
                                                   }catch (Exception ex){
                                                   }
                                               } else {
                                                   myPreferences.setRemoveAds(false);
                                               }
                                               remove_app.setVisibility(!MyPreferences.getInstance().getRemoveAds() ? View.VISIBLE : View.GONE);
                                           }
                                           catch (Exception ex){
                                           }
                                       }
                                   }
                               });
        remove_app.setVisibility(!MyPreferences.getInstance().getRemoveAds() ? View.VISIBLE : View.GONE);

        rate_us.setVisibility(!MyPreferences.getInstance().getRateUsToStars() ? View.VISIBLE: View.GONE);

        remove_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHelper != null){
                    mHelper.flagEndAsync();
                }
                try {
                    mHelper.launchPurchaseFlow(FragmentAdsApp.this.getActivity(), CustomApp.ADS_ITEM_SKU, 10001,
                            mPurchaseFinishedListener);
                }
                catch (Exception ex){
                }
            }
        });
        more_apps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://search?q=pub:GoodProducts+Soft");
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (Exception e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/developer?id=GoodProducts+Soft")));
                }
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("text/plain");
                final PackageManager pm = FragmentAdsApp.this.getActivity().getPackageManager();
                final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
                ResolveInfo best = null;
                for (final ResolveInfo info : matches)
                    if (info.activityInfo.packageName.endsWith(".gm") ||
                            info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
                if (best != null) {
                    intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                    intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"goodproductssoft@gmail.com"});
                    String version = "version ";
                    try {
                        PackageInfo pInfo = FragmentAdsApp.this.getActivity().getPackageManager().getPackageInfo(FragmentAdsApp.this.getActivity().getPackageName(), 0);
                        version += pInfo.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    String nameApp = FragmentAdsApp.this.getActivity().getResources().getString(R.string.app_name);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback " + nameApp + " " + version);
                }
                try {
                    startActivity(intent);
                }catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(CustomApp.getInstance(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(getActivity() instanceof  MainActivity){
                            ((MainActivity)getActivity()).getIntersitialAds();
                    }
                }catch (Exception ex){

                }
            }
        });

        rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!MyPreferences.getInstance().getRateUsToStars()) {
                    Uri uri = Uri.parse("market://details?id=" + FragmentAdsApp.this.getActivity().getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                    MyPreferences.getInstance().setRateUsToStars(true);
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                            Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                    try {
                        startActivity(goToMarket);
                    } catch (Exception e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + FragmentAdsApp.this.getActivity().getPackageName())));
                    }
                }
            }
        });

        InitRadioGroup();

        rg_view_mode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.both_landscape_portrait:
                        FragmentAdsApp.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                        MyPreferences.getInstance().setViewModes(0);
                        break;
                    case R.id.landscape:
                        FragmentAdsApp.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        MyPreferences.getInstance().setViewModes(1);
                        break;
                    case R.id.portrait:
                        FragmentAdsApp.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        MyPreferences.getInstance().setViewModes(2);
                        break;
                }
            }
        });
//        final String[] listItems;
//        listItems = getResources().getStringArray(R.array.view_modes);
//
//        view_modes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AlertDialog.Builder mBuilder = new AlertDialog.Builder(FragmentAdsApp.this.getActivity());
//                mBuilder.setTitle("Choose an view mode");
//                mBuilder.setSingleChoiceItems(listItems, MyPreferences.getInstance().getViewModes(), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if(i == 0){
//                            FragmentAdsApp.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//                            MyPreferences.getInstance().setViewModes(0);
//                        }
//                        else if(i == 1){
//                            FragmentAdsApp.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                            MyPreferences.getInstance().setViewModes(1);
//                        }
//                        else {
//                            FragmentAdsApp.this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                            MyPreferences.getInstance().setViewModes(2);
//                        }
//                        dialogInterface.dismiss();
//                    }
//                });
//
//                AlertDialog mDialog = mBuilder.create();
//                mDialog.show();
//            }
//        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        // Pass on the activity result to the helper for handling
        if (mHelper != null && !mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        @Override
        public void onIabPurchaseFinished(IabResult result, com.goodproductssoft.minningpool.util.Purchase info) {
            if (result.isFailure()) {
                return;
            }
            else if (result.isSuccess() && info.getSku().equals(CustomApp.ADS_ITEM_SKU)) {
                MyPreferences.getInstance().setRemoveAds(true);
//                mHelper.queryInventoryAsync(mReceivedInventoryListener);
                remove_app.setVisibility(!MyPreferences.getInstance().getRemoveAds() ? View.VISIBLE : View.GONE);
                try {
                    if(getActivity() instanceof  MainActivity){
                            ((MainActivity)getActivity()).hideBannerAds();
                    }
                }catch (Exception ex){
                }
            }
        }
    };

//    IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener
//            = new IabHelper.QueryInventoryFinishedListener() {
//        public void onQueryInventoryFinished(IabResult result,
//                                             Inventory inventory) {
//
//            try {
//                if (result.isFailure()) {
//                    // Handle failure
////                    Purchase purchase = inventory.getPurchase(CustomApp.ADS_ITEM_SKU);
////                    if (purchase != null) {
////                        mHelper.consumeAsync(inventory.getPurchase(CustomApp.ADS_ITEM_SKU),
////                                mConsumeFinishedListener);
////                    }
//                }
//            }
//            catch (Exception ex){}
//        }
//    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
                                                                    new IabHelper.OnConsumeFinishedListener() {
                                                                        @Override
                                                                        public void onConsumeFinished(com.goodproductssoft.minningpool.util.Purchase purchase, IabResult result) {
                                                                            if (result.isSuccess()) {
                                                        //                        clickButton.setEnabled(true);
                                                                            } else {
                                                                                // handle error
                                                                            }
                                                                        }
                                                                    };

    private void InitRadioGroup(){
        int checkedViewMode = MyPreferences.getInstance().getViewModes();
        switch (checkedViewMode){
            case 0:
                both_landscape_portrait.setChecked(true);
                break;
            case 1:
                landscape.setChecked(true);
                break;
            case 2:
                portrait.setChecked(true);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        rate_us.setVisibility(!MyPreferences.getInstance().getRateUsToStars() ? View.VISIBLE: View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) {
            mHelper.dispose();
        }
        mHelper = null;
    }
}

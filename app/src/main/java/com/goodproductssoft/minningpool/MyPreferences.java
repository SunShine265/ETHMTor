package com.goodproductssoft.minningpool;

import android.content.Context;
import android.content.SharedPreferences;

import com.goodproductssoft.minningpool.activities.MainActivity;
import com.goodproductssoft.minningpool.models.IdSuggestsion;
import com.goodproductssoft.minningpool.models.Miner;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by user on 5/11/2017.
 */

public final class MyPreferences {
    private Context mContext;

    public static final String PREFS_NAME = "ETHERMINE_APP";
    public final static String ID = "ID";
    public final static String IDMINER= "IDMINER";
    public final static String ID_SUGGESTSION= "ID_SUGGESTSION";
    public final static String SHOW_ADS_REMAIN_TIMES= "SHOW_ADS_REMAIN_TIMES";
    public final static String SHOW_RATE_REMAIN_TIMES= "SHOW_RATE_REMAIN_TIMES";
    public final static String REMOVE_ADS = "REMOVE_ADS";
    public final static String VIEW_MODES = "VIEW_MODES";
    public final static String RATE_US_TO_STARS = "RATE_US_TO_STARS";

    private static MyPreferences myPreferences;
    public final static MyPreferences getInstance() {
        if(myPreferences == null){
            myPreferences = new MyPreferences(CustomApp.getInstance());
        }
        return myPreferences;
    }

    public MyPreferences(Context context) {
        mContext = context;
    }

    private SharedPreferences getSharedPreferences(){
        try {
            return mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
        catch (Exception ex){
            return CustomApp.getInstance().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public void setRemoveAds(boolean value){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = getSharedPreferences();
        editor = settings.edit();
        editor.putBoolean(REMOVE_ADS, value);
        editor.commit();
    }

    public boolean getRemoveAds(){
        SharedPreferences settings;
        settings = getSharedPreferences();
        boolean removeAds = settings.getBoolean(REMOVE_ADS, false);
        return removeAds;
    }

    public void setRateUsToStars(boolean value){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = getSharedPreferences();
        editor = settings.edit();
        editor.putBoolean(RATE_US_TO_STARS, value);
        editor.commit();
    }

    public boolean getRateUsToStars(){
        SharedPreferences settings;
        settings = getSharedPreferences();
        boolean rateUsToStars = settings.getBoolean(RATE_US_TO_STARS, false);
        return rateUsToStars;
    }

    public void setViewModes(int value){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = getSharedPreferences();
        editor = settings.edit();
        editor.putInt(VIEW_MODES, value);
        editor.commit();
    }

    public int getViewModes(){
        SharedPreferences settings;
        settings = getSharedPreferences();
        int viewModes = settings.getInt(VIEW_MODES, 0);
        return viewModes;
    }

    public void setShowRateRemainTimes(long times){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = getSharedPreferences();
        editor = settings.edit();
        editor.putLong(SHOW_RATE_REMAIN_TIMES, times);
        editor.commit();
    }

    public long getShowRateRemainTimes(){
        SharedPreferences settings;
        settings = getSharedPreferences();
        long remainTimes = settings.getLong(SHOW_RATE_REMAIN_TIMES, MainActivity.SHOW_RATE_REMAIN_TIMES);
        return remainTimes;
    }

    public void setShowAdsRemainTimes(long times){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = getSharedPreferences();
        editor = settings.edit();
        editor.putLong(SHOW_ADS_REMAIN_TIMES, times);
        editor.commit();
    }

    public long getShowAdsRemainTimes(){
        SharedPreferences settings;
        settings = getSharedPreferences();
        long remainTimes = settings.getLong(SHOW_ADS_REMAIN_TIMES, MainActivity.SHOW_RATE_REMAIN_TIMES + 5);
        return remainTimes;
    }

    public void SaveIdSuggestsions(List<IdSuggestsion> objects){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = getSharedPreferences();
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonIdMiner = gson.toJson(objects);
        editor.putString(ID_SUGGESTSION, jsonIdMiner);
        editor.commit();
    }

    public ArrayList<IdSuggestsion> GetIdSuggestsions(){
        SharedPreferences settings;
        List<IdSuggestsion> idSuggestsionses;
        settings = getSharedPreferences();
        if(settings.contains(ID_SUGGESTSION)){
            String jsonIdSuggestsions = settings.getString(ID_SUGGESTSION, null);
            Gson gson = new Gson();
            IdSuggestsion[] idSuggestsionses1Items = gson.fromJson(jsonIdSuggestsions, IdSuggestsion[].class);
            idSuggestsionses = Arrays.asList(idSuggestsionses1Items);
            idSuggestsionses = new ArrayList<IdSuggestsion>(idSuggestsionses);
        }
        else
            return null;
        return (ArrayList<IdSuggestsion>) idSuggestsionses;
    }

    private void SaveIdMiners(List<Miner> objects){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = getSharedPreferences();
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonIdMiner = gson.toJson(objects);
        editor.putString(IDMINER, jsonIdMiner);
        editor.commit();
    }

    public void AddIdMiner(Miner miner){
        List<Miner> miners = new ArrayList<Miner>();
//        List<Miner> miners = GetIdMiners();
//        if(miners == null){
//            miners = new ArrayList<Miner>();
//        }
        miners.add(miner);
        SaveIdMiners(miners);
    }

    public void UpdateMiner(Miner miner){
        if (miner != null) {
            ArrayList<Miner> miners = GetIdMiners();
            if(miners == null){
                miners = new ArrayList<>();
            }
            ArrayList<Miner> removeMiners = new ArrayList<>(miners);
            for (Miner removeMiner : removeMiners) {
                if (removeMiner == null
                        || removeMiner.getId() == null
                        || removeMiner.getId().isEmpty()){
                    miners.remove(removeMiner);
                }
            }

            for (int i = 0; i < miners.size(); i++) {
                if (miners.get(i).getId().equals(miner.getId())
                        && miners.get(i).getType().equals(miner.getType())) {
                    miners.add(miner);
                    miners.remove(miners.get(i));
                    break;
                }
            }

            SaveIdMiners(miners);
        }
    }

    public ArrayList<Miner> GetIdMiners(){
        SharedPreferences settings;
        ArrayList<Miner> miners = new ArrayList<Miner>();
        settings = getSharedPreferences();
        if(settings.contains(IDMINER)){
            String jsonIdMiner = settings.getString(IDMINER, null);
            if(jsonIdMiner != null) {
                Gson gson = new Gson();
                Miner[] minersItems = gson.fromJson(jsonIdMiner, Miner[].class);
                miners = new ArrayList<Miner>(Arrays.asList(minersItems));
            }
        }
        return miners;
    }
}

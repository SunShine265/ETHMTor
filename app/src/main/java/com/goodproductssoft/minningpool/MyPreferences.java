package com.goodproductssoft.minningpool;

import android.content.Context;
import android.content.SharedPreferences;

import com.goodproductssoft.minningpool.activitys.MainActivity;
import com.goodproductssoft.minningpool.models.IdSuggestsion;
import com.goodproductssoft.minningpool.models.Miner;
import com.goodproductssoft.minningpool.models.YourWorkerNotify;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by user on 5/11/2017.
 */

public final class MyPreferences {
    public static final String PREFS_NAME = "ETHERMINE_APP";
    public final static String ID = "ID";
    public final static String IDMINER= "IDMINER";
    public final static String ID_SUGGESTSION= "ID_SUGGESTSION";
    public final static String SHOW_ADS_REMAIN_TIMES= "SHOW_ADS_REMAIN_TIMES";

    public final static String YOUR_WORKER_OFF = "YOUR_WORKER_OFF";

    public MyPreferences() {
        super();
    }

    public static void setID(String value, Context context){
        SharedPreferences token = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor edit = token.edit();
        edit.putString(ID, value);
        edit.commit();
    }

    public  String getID(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(ID, "");
    }

    public void setShowAdsRemainTimes(Context context, long times){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putLong(SHOW_ADS_REMAIN_TIMES, times);
        editor.commit();
    }


    public long getShowAdsRemainTimes(Context context){
        SharedPreferences settings;
        settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        long remainTimes = settings.getLong(SHOW_ADS_REMAIN_TIMES, MainActivity.MAX_SHOW_ADS_REMAIN_TIMES);
        return remainTimes;
    }

    public void SaveIdSuggestsions(Context context, List<IdSuggestsion> objects){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        editor = settings.edit();
        Gson gson = new Gson();
        String jsonIdMiner = gson.toJson(objects);
        editor.putString(ID_SUGGESTSION, jsonIdMiner);
        editor.commit();
    }

    public ArrayList<IdSuggestsion> GetIdSuggestsions(Context context){
        SharedPreferences settings;
        List<IdSuggestsion> idSuggestsionses;
        settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
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

    public static void SaveYourWorkerOff(ArrayList<YourWorkerNotify> value, Context context){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonIdMiner = gson.toJson(value);
        editor.putString(YOUR_WORKER_OFF, jsonIdMiner);
        editor.commit();
    }

    public  ArrayList<YourWorkerNotify> getYourWorkerOff(Context context){
        SharedPreferences settings;
        List<YourWorkerNotify> yourWorkers;
        settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        if(settings.contains(YOUR_WORKER_OFF)){
            String jsonYourWorkerOff = settings.getString(YOUR_WORKER_OFF, null);
            Gson gson = new Gson();
//            YourWorkerOff yourWorkerOff = gson.fromJson(jsonYourWorkerOff, YourWorkerOff.class);
//            yourWorker = yourWorkerOff;
            YourWorkerNotify[] youWotkerItems = gson.fromJson(jsonYourWorkerOff, YourWorkerNotify[].class);
            yourWorkers = Arrays.asList(youWotkerItems);
            yourWorkers = new ArrayList<YourWorkerNotify>(yourWorkers);
        }
        else
            return null;
        return (ArrayList<YourWorkerNotify>) yourWorkers;
    }

    public void AddYourWorkerOff(Context context, YourWorkerNotify yourWorkerNotify){
        ArrayList<YourWorkerNotify> yourWorkerNotifies = new ArrayList<YourWorkerNotify>();
        yourWorkerNotifies.add(yourWorkerNotify);
        SaveYourWorkerOff(yourWorkerNotifies, context);
    }

    public void RemoveYourWorkerOff(Context context, Miner miner){
        ArrayList<Miner> miners = GetIdMiners(context);
        if(miners != null){
            miners.remove(miner);
            SaveIdMiners(context, miners);
        }
    }

    private void SaveIdMiners(Context context, List<Miner> objects){
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonIdMiner = gson.toJson(objects);
        editor.putString(IDMINER, jsonIdMiner);
        editor.commit();
    }

    public void AddIdMiner(Context context, Miner miner){
        List<Miner> miners = new ArrayList<Miner>();
//        List<Miner> miners = GetIdMiners(context);
//        if(miners == null){
//            miners = new ArrayList<Miner>();
//        }
        miners.add(miner);
        SaveIdMiners(context, miners);
    }

    public void UpdateMiner(Context context, Miner miner){
        ArrayList<Miner> miners = GetIdMiners(context);
        if (miners != null) {
            for (int i = 0; i < miners.size(); i++) {
                if (miners.get(i) == null
                    || miners.get(i).getId() == null
                    || miners.get(i).getId().isEmpty()){
                    miners.remove(miners.get(i));
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

            SaveIdMiners(context, miners);
        }
    }

    public void RemoveAll(Context context){
        ArrayList<Miner> miners = new ArrayList<>();
        SaveIdMiners(context, miners);
//        ArrayList<Miner> miners = GetIdMiners(context);
//        if(miners != null){
//            for (int i = 0; i < miners.size(); i++){
//                miners.remove(miners.get(i));
//            }
//            SaveIdMiners(context, miners);
//        }
    }

    public ArrayList<Miner> GetIdMiners(Context context){
        SharedPreferences settings;
        ArrayList<Miner> miners = new ArrayList<Miner>();
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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

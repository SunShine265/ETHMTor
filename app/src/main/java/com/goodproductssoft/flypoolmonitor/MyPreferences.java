package com.goodproductssoft.flypoolmonitor;

import android.content.Context;
import android.content.SharedPreferences;

import com.goodproductssoft.flypoolmonitor.models.Miner;
import com.goodproductssoft.flypoolmonitor.models.YourWorkerNotify;
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

    public void SaveIdMiners(Context context, List<Miner> objects){
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

    public void RemoveMiner(Context context, Miner miner){
        ArrayList<Miner> miners = GetIdMiners(context);
        if(miners != null){
            miners.remove(miner);
            SaveIdMiners(context, miners);
        }
    }

    public void UpdateMiner(Context context, Miner miner){
        ArrayList<Miner> miners = GetIdMiners(context);
        if(miners != null){
            Miner tempMiner = new Miner();
            tempMiner = miner;
            for(int i = 0; i < miners.size(); i++){
                if(miners.get(i).getId().equals(miner.getId())){
                    miners.remove(miners.get(i));
                    break;
                }
            }
            miners.add(tempMiner);
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
        List<Miner> miners;
        settings = context.getSharedPreferences(PREFS_NAME, context.MODE_PRIVATE);
        if(settings.contains(IDMINER)){
            String jsonIdMiner = settings.getString(IDMINER, null);
            Gson gson = new Gson();
            Miner[] minersItems = gson.fromJson(jsonIdMiner, Miner[].class);
            miners = Arrays.asList(minersItems);
            miners = new ArrayList<Miner>(miners);
        }
        else
            return null;
        return (ArrayList<Miner>) miners;
    }
}

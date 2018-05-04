package com.goodproductssoft.flypoolmonitor;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;

import com.goodproductssoft.flypoolmonitor.activitys.MainActivity;
import com.goodproductssoft.flypoolmonitor.models.Miner;
import com.goodproductssoft.flypoolmonitor.models.YourWorkerNotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.ALARM_SERVICE;

public class OnBroadcastService extends BroadcastReceiver {
    Context context;
//    SharedPreferences pref;
//    SharedPreferences.Editor editor;
    MyPreferences myPreferences;
    Miner miner;
    WifiManager wifiManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        this.context = context;
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            int time_for_repeate = 1000 * 120;
            AlarmManager processTimer = (AlarmManager) this.context.getSystemService(ALARM_SERVICE);
            Intent intentAlarm = new Intent(this.context, OnBroadcastService.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 1012, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
            processTimer.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), time_for_repeate, pendingIntent);
        }
        myPreferences = new MyPreferences();
//        pref = context.getSharedPreferences("MyPref", 0);
//        editor = pref.edit();
//        String idMiner = pref.getString("ID_MINER", "");
//        if(idMiner != null && !idMiner.isEmpty()) {
        miner = GetMinerIdActive();
        if(miner != null && miner.isNotification()) {
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            String urlWorker = miner.getEndpoint() + "/miner/" + miner.getId() + "/workers";
            new OnBroadcastService.GetWorker().execute(urlWorker);
        }
    }

    private Miner GetMinerIdActive(){
        ArrayList<Miner> miners = myPreferences.GetIdMiners(context);
        if(miners != null && !miners.isEmpty()) {
            for (int i = 0; i < miners.size(); i++) {
                if (miners.get(i).isActive()) {
                    return miners.get(i);
                }
            }
        }
        return null;
    }

    private class GetWorker extends AsyncTask<String, Void, ArrayList<YourWorkerNotify>> {

        WifiManager.WifiLock wifiLock;
        PowerManager.WakeLock wakeLock;

        private void AquireWifiLock()
        {
            if (wifiLock == null)
            {
                wifiLock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "GetWorker");
                wifiLock.acquire();
            }
        }

        private void AquireWakeLock()
        {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, "WakeLock");
            wakeLock.acquire();
        }

        private void ReleaseWakeLock()
        {
            if (wakeLock == null)
            {
                return;
            }
            wakeLock.release();
            wakeLock = null;
        }

        private void ReleaseWifiLock()
        {
            if (wifiLock == null)
            {
                return;
            }

            wifiLock .release();
            wifiLock = null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            try {
                AquireWifiLock();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }

        @Override
        protected ArrayList<YourWorkerNotify> doInBackground(String... url) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url[0]);
            ArrayList<YourWorkerNotify> currentYourWorkers = new ArrayList<>();

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray workers = jsonObj.getJSONArray("data");
                    // looping through All Contacts
                    for (int i = 0; i < workers.length(); i++) {
                        JSONObject value = workers.getJSONObject(i);
                        YourWorkerNotify yourWorkerNotify = new YourWorkerNotify();
                        double reportedHashrate;
                        try
                        {
                            reportedHashrate = value.getDouble("reportedHashrate");
                        }catch (JSONException e){
                            reportedHashrate = 0;
                        }
                        String nameYourWorker;
                        try
                        {
                            nameYourWorker = value.getString("worker");
                        }catch (NullPointerException e){
                            nameYourWorker = "";
                        }

                        yourWorkerNotify.setReportHashrate(reportedHashrate);
                        yourWorkerNotify.setNameYourWorker(nameYourWorker);
                        currentYourWorkers.add(yourWorkerNotify);
                    }
                } catch (final JSONException e) {
                  return null;
                }
            } else {
                return null;
            }
            return currentYourWorkers;
        }

        @Override
        protected void onPostExecute(ArrayList<YourWorkerNotify> result) {
            super.onPostExecute(result);
            try {
                ReleaseWifiLock();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
            if(result != null){
                String strMessages = "";
                ArrayList<YourWorkerNotify> yourWorkerNotifiesCurrent = new ArrayList<>(result);
                ArrayList<YourWorkerNotify> yourWorkerNotifiesBackup = miner.getWorkersBackup();
                miner.setWorkersBackup(yourWorkerNotifiesCurrent);
                myPreferences.UpdateMiner(OnBroadcastService.this.context, miner);

                ArrayList<YourWorkerNotify> listNotify = new ArrayList<>();
                for (YourWorkerNotify yourWorkerBackup : yourWorkerNotifiesBackup) {
                    if(yourWorkerBackup.getReportHashrate() > 0) {
                        boolean isOffline = true;
                        for (YourWorkerNotify yourWorkerCurrent : yourWorkerNotifiesCurrent) {
                            if (yourWorkerBackup.getNameYourWorker() != null &&
                                    yourWorkerBackup.getNameYourWorker().equals(yourWorkerCurrent.getNameYourWorker())) {
                                if (yourWorkerCurrent.getReportHashrate() == 0) {
                                    isOffline = true;
                                } else {
                                    isOffline = false;
                                }
                                break;
                            }
                        }
                        if(isOffline) {
                            listNotify.add(yourWorkerBackup);
                        }
                    }
                }
                for (int i = 0; i < listNotify.size(); i++) {
                    if (i > 0) {
                        strMessages += " ";
                    }
                    strMessages += listNotify.get(i).getIdMiner() + "." + listNotify.get(i).getNameYourWorker();
                }
                if (!strMessages.isEmpty()) {
                    try {
                        AquireWakeLock();
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                    NotificationManager mNotificationManager =
                            (NotificationManager) OnBroadcastService.this.context.getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        int notifyID = 1;
                        String CHANNEL_ID = "my_channel_01";// The id of the channel.
                        CharSequence name = "channel_name";// The user-visible name of the channel.
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
                        // Create a notification and set the notification channel.
                        Notification notification = new Notification.Builder(OnBroadcastService.this.context)
                                .setSmallIcon(R.drawable.icon_notify) // notification icon
                                .setContentTitle("Flypoot Monitor") // title for notification
                                .setStyle(new android.app.Notification.BigTextStyle().bigText(strMessages + " offline"))
                                .setContentText(strMessages) // message for notification
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setChannelId(CHANNEL_ID)
                                .build();
                        mNotificationManager.createNotificationChannel(mChannel);
                        mNotificationManager.notify(notifyID, notification);
                    } else {
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.icon_notify) // notification icon
                                .setContentTitle("Flypoot Monitor") // title for notification
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(strMessages + " offline"))
                                .setContentText(strMessages) // message for notification
                                .setAutoCancel(true) // clear notification after click
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT));
                        mNotificationManager.notify(0, mBuilder.build());
                    }
                    try {
                        ReleaseWakeLock();
                    }
                    catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
            try {
                AquireWakeLock();
            }
            catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
}

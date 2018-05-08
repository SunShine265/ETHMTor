package com.goodproductssoft.minningpool;

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

import com.goodproductssoft.minningpool.activitys.MainActivity;
import com.goodproductssoft.minningpool.models.Miner;
import com.goodproductssoft.minningpool.models.YourWorkerNotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.ALARM_SERVICE;

public class OnBroadcastService extends BroadcastReceiver {
    Context context;
    MyPreferences myPreferences;
    Miner miner;
    WifiManager wifiManager;
    static Date latestDate;

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

        try {
            Date currentTime = Calendar.getInstance().getTime();
            if(latestDate == null){
                latestDate = currentTime;
            }
            else {
                synchronized (latestDate) {
                    if (latestDate.getTime() + 1000 * 100 > currentTime.getTime()) {
                        if (latestDate.getTime() > currentTime.getTime()) {
                            latestDate = Calendar.getInstance().getTime();
                        }
                        return;
                    }
                    latestDate = currentTime;
                }
            }
        }
        catch (Exception ex){}

//        Toast.makeText(this.context, latestDate.toString(), Toast.LENGTH_LONG).show();

        myPreferences = new MyPreferences();
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

        private void ReleaseWifiLock()
        {
            if (wifiLock == null)
            {
                return;
            }

            wifiLock .release();
            wifiLock = null;
        }

        private void AquireWakeLock(long timeout)
        {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                    PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.ON_AFTER_RELEASE, "WakeLock");
            if(timeout > 0) {
                wakeLock.acquire(timeout);
            }
            else{
                wakeLock.acquire();
            }
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
            ArrayList<YourWorkerNotify> currentYourWorkers = new ArrayList<>();
            try {
                HttpHandler sh = new HttpHandler();
                // Making a request to url and getting response
                String jsonStr = sh.makeServiceCall(url[0]);
                ArrayList<String> listWorker = new ArrayList<>();

                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        JSONArray workers = jsonObj.getJSONArray("data");
                        // looping through All Contacts
                        for (int i = 0; i < workers.length(); i++) {
                            JSONObject value = workers.getJSONObject(i);
                            YourWorkerNotify notifyYourWorker = new YourWorkerNotify();
                            double reportedHashrate;
                            try {
                                reportedHashrate = value.getDouble("reportedHashrate");
                            } catch (JSONException e) {
                                reportedHashrate = 0;
                            }
                            String nameYourWorker;
                            try {
                                nameYourWorker = value.getString("worker");
                            } catch (NullPointerException e) {
                                nameYourWorker = "";
                            }

                            notifyYourWorker.setReportHashrate(reportedHashrate);
                            notifyYourWorker.setNameYourWorker(nameYourWorker);
                            notifyYourWorker.setIdMiner(miner.getId());
                            listWorker.add(nameYourWorker);
                            currentYourWorkers.add(notifyYourWorker);
                        }
                    } catch (final JSONException e) {
                        return null;
                    }
                } else {
                    return null;
                }
            } catch (Exception ex) {
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


            if (result != null) {
                String strMessages = "";
                ArrayList<YourWorkerNotify> yourWorkerNotifiesCurrent = new ArrayList<>(result);
                ArrayList<YourWorkerNotify> yourWorkerNotifiesBackup = miner.getWorkersBackup();
                miner.setWorkersBackup(yourWorkerNotifiesCurrent);
                myPreferences.UpdateMiner(OnBroadcastService.this.context, miner);

                //new
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
                        AquireWakeLock(-1);
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
                                .setSmallIcon(R.drawable.icon_eth) // notification icon
                                .setContentTitle("Ethermine Monitor") // title for notification
                                .setStyle(new android.app.Notification.BigTextStyle().bigText(strMessages + " offline"))
                                .setContentText(strMessages + " offline") // message for notification
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setChannelId(CHANNEL_ID)
                                .build();
                        mNotificationManager.createNotificationChannel(mChannel);
                        mNotificationManager.notify(notifyID, notification);
                    } else {
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.icon_eth) // notification icon
                                .setContentTitle("Ethermine Monitor") // title for notification
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(strMessages + " offline"))
                                .setContentText(strMessages + " offline") // message for notification
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
        }
    }
}

package com.goodproductssoft.minningpool.models;

import java.util.ArrayList;

/**
 * Created by user on 4/19/2018.
 */

public class Miner {
    static String endpointEth = "https://api.ethermine.org";
    static String endpointEtc = "https://api-etc.ethermine.org";
    static String endpointZec = "https://api-zcash.flypool.org";

    String id;
    CoinType type;
    String idMinerBackup;
    boolean isActive;
    boolean isNotification = true;
    Settings settings;
    ArrayList<YourWorkerNotify> workersBackup = new ArrayList<>();

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEndpoint() {
        return getType() == CoinType.ETC ? endpointEtc : getType() == CoinType.ZCash ? endpointZec : endpointEth;
    }

    public CoinType getType() {
        return type;
    }

    public void setType(CoinType type) {
        this.type = type;
    }

    public ArrayList<YourWorkerNotify> getWorkersBackup() {
        return workersBackup;
    }

    public void setWorkersBackup(ArrayList<YourWorkerNotify> workers) {
        this.workersBackup = workers;
    }

    public String getIdMinerBackup() {
        return idMinerBackup;
    }

    public void setIdMinerBackup(String idMinerBackup) {
        this.idMinerBackup = idMinerBackup;
    }

    public boolean isNotification() {
        return isNotification;
    }

    public void setNotification(boolean notification) {
        isNotification = notification;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public enum CoinType  {
        ETH, ETC, ZCash
    }

}

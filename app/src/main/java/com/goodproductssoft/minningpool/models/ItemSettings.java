package com.goodproductssoft.minningpool.models;

/**
 * Created by user on 4/19/2018.
 */

public class ItemSettings {
    String idMiner;
    boolean isEth;

    public boolean isEth() {
        return isEth;
    }

    public void setEth(boolean eth) {
        isEth = eth;
    }

    public String getIdMiner() {

        return idMiner;
    }

    public void setIdMiner(String idMiner) {
        this.idMiner = idMiner;
    }
}

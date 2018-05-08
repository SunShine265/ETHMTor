package com.goodproductssoft.minningpool.models;

/**
 * Created by user on 4/24/2018.
 */

public class YourWorkerNotify {
    String idMiner, nameYourWorker;
    double reportHashrate;

    public double getReportHashrate() {
        return reportHashrate;
    }

    public void setReportHashrate(double reportHashrate) {
        this.reportHashrate = reportHashrate;
    }

    public String getIdMiner() {
        return idMiner;
    }

    public void setIdMiner(String idMiner) {
        this.idMiner = idMiner;
    }

    public String getNameYourWorker() {
        return nameYourWorker;
    }

    public void setNameYourWorker(String nameYourWorker) {
        this.nameYourWorker = nameYourWorker;
    }
}

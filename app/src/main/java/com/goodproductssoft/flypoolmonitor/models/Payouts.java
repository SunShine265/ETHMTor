package com.goodproductssoft.flypoolmonitor.models;

/**
 * Created by user on 4/19/2018.
 */

public class Payouts{
    int total;
    double average;
    double totalETH;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public double getTotalETH() {
        return totalETH;
    }

    public void setTotalETH(double totalETH) {
        this.totalETH = totalETH;
    }
}

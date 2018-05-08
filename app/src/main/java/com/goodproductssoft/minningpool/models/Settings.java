package com.goodproductssoft.minningpool.models;

/**
 * Created by user on 4/19/2018.
 */

public class Settings{
    String email,ip;
    double payout;
    int monitor;

    public int getMonitor() {
        return monitor;
    }

    public void setMonitor(int monitor) {
        this.monitor = monitor;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public double getPayout() {
        return payout;
    }

    public void setPayout(double payout) {
        this.payout = payout;
    }
}

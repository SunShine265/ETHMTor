package com.goodproductssoft.flypoolmonitor.models;

/**
 * Created by user on 4/19/2018.
 */

public class Settings{
    String email,ip;
    double payout;

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

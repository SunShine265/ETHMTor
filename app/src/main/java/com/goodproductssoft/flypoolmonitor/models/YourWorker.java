package com.goodproductssoft.flypoolmonitor.models;

/**
 * Created by user on 4/19/2018.
 */

public class YourWorker {
    double  Current, Reported;
    String YourWorker, LastScreen;
    boolean isValue;
    int Valid, Stale, Invalid;

    public double getCurrent() {
        return Current;
    }

    public void setCurrent(double current) {
        Current = current;
    }

    public double getReported() {
        return Reported;
    }

    public void setReported(double reported) {
        Reported = reported;
    }

    public String getLastScreen() {
        return LastScreen;
    }

    public void setLastScreen(String lastScreen) {
        LastScreen = lastScreen;
    }

    public String getYourWorker() {
        return YourWorker;
    }

    public void setYourWorker(String yourWorker) {
        YourWorker = yourWorker;
    }

    public int getValid() {
        return Valid;
    }

    public void setValid(int valid) {
        Valid = valid;
    }

    public int getStale() {
        return Stale;
    }

    public void setStale(int stale) {
        Stale = stale;
    }

    public int getInvalid() {
        return Invalid;
    }

    public void setInvalid(int invalid) {
        Invalid = invalid;
    }

    public boolean isValue() {
        return isValue;
    }

    public void setValue(boolean value) {
        isValue = value;
    }
}
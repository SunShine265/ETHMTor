
/**
 * Created by user on 4/19/2018.
 */

public class TotalYourWorker{
    double  Current, Reported, LastScreen;
    int YourWorker, Valid, Stale, Invalid;
    boolean isValue;

    public boolean isValue() {
        return isValue;
    }

    public void setValue(boolean value) {
        isValue = value;
    }

    public int getYourWorker() {
        return YourWorker;
    }

    public void setYourWorker(int yourWorker) {
        this.YourWorker = yourWorker;
    }

    public double getCurrent() {
        return Current;
    }

    public void setCurrent(double totalCurrent) {
        this.Current = totalCurrent;
    }

    public double getReported() {
        return Reported;
    }

    public void setReported(double reported) {
        this.Reported = reported;
    }

    public int getValid() {
        return Valid;
    }

    public void setValid(int valid) {
        this.Valid = valid;
    }

    public int getStale() {
        return Stale;
    }

    public void setStale(int stale) {
        this.Stale = stale;
    }

    public int getInvalid() {
        return Invalid;
    }

    public void setInvalid(int invalid) {
        this.Invalid = invalid;
    }

    public double getLastScreen() {
        return LastScreen;
    }

    public void setLastScreen(double lastScreen) {
        this.LastScreen = lastScreen;
    }
}
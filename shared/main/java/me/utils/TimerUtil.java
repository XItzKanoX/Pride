package me.utils;

public class TimerUtil {

    public long lastMS = System.currentTimeMillis();

    private long currentMs;
    public void reset() {
        lastMS = System.currentTimeMillis();
    }


    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) reset();
            return true;
        }

        return false;
    }
    public boolean hasElapsed(long milliseconds) {
        return elapsed() > milliseconds;
    }
    public long elapsed() {
        return System.currentTimeMillis() - currentMs;
    }

    public boolean hasTimeElapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }


    public long getTime() {
        return System.currentTimeMillis() - lastMS;
    }

    public void setTime(long time) {
        lastMS = time;
    }

}

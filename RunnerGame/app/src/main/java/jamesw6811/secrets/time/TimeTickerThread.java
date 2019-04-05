package jamesw6811.secrets.time;

import android.util.Log;

/**
 * Created by james on 6/17/2017.
 */

public class TimeTickerThread extends Thread {
    public static final long FPS = 30;
    private static final String LOGTAG = TimeTickerThread.class.getName();
    private boolean running = false;
    private TimeTicked tt;

    public TimeTickerThread(TimeTicked tt) {
        this.tt = tt;
    }

    @Override
    public void start() {
        running = true;
        super.start();
    }

    public void stopRunning() {
        running = false;
    }

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long sleepTime;
        long startTime = System.currentTimeMillis();
        long lastTime;
        while (running) {
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (InterruptedException ie){
                Log.e(LOGTAG, "TimeTickerThread thread sleep interrupted, trying to recover.");
            }
            if (!running) break;
            lastTime = startTime;
            startTime = System.currentTimeMillis();
            tt.tickTime(((float) (startTime - lastTime)) / 1000f);
        }
    }
}

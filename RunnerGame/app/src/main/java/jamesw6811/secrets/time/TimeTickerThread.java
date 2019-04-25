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
    private float duration = 0;

    public TimeTickerThread(TimeTicked tt) {
        this.tt = tt;
    }

    @Override
    public void start() {
        running = true;
        super.start();
    }

    public float getDuration(){
        return duration;
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
            } catch (InterruptedException ie) {
                Log.e(LOGTAG, "TimeTickerThread thread sleep interrupted, trying to recover.");
            }
            if (!running) break;
            lastTime = startTime;
            startTime = System.currentTimeMillis();
            float timePassed = ((float) (startTime - lastTime)) / 1000f;
            tt.tickTime(timePassed);
            duration += timePassed;
        }
    }
}

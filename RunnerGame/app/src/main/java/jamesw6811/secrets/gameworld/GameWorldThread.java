package jamesw6811.secrets.gameworld;

import android.util.Log;

/**
 * Created by james on 6/17/2017.
 */

public class GameWorldThread extends Thread {
    private static final long FPS = 30;
    private static final String LOGTAG = GameWorldThread.class.getName();
    private boolean running = false;
    private GameWorld gw;

    GameWorldThread(GameWorld world) {
        gw = world;
    }

    @Override
    public void start() {
        running = true;
        super.start();
    }

    void stopRunning() {
        running = false;
    }

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime = System.currentTimeMillis();
        long sleepTime;
        long lastTime;
        while (running) {
            lastTime = startTime;
            startTime = System.currentTimeMillis();
            gw.tickTime(((float) (startTime - lastTime)) / 1000f);
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (InterruptedException ie){
                Log.e(LOGTAG, "GameWorldThread thread sleep interrupted, trying to recover.");
            }
        }
    }
}

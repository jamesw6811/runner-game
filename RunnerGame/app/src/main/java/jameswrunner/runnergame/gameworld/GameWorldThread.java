package jameswrunner.runnergame.gameworld;

/**
 * Created by james on 6/17/2017.
 */

public class GameWorldThread extends Thread {
    static final long FPS = 30;
    private boolean running = false;
    private GameWorld gw;

    public GameWorldThread(GameWorld world) {
        gw = world;
    }

    @Override
    public void start(){
        running = true;
        super.start();
    }

    public void stopRunning(){
        running = false;
    }

    @Override
    public void run() {
        long ticksPS = 1000 / FPS;
        long startTime = System.currentTimeMillis();
        long sleepTime;
        long lastTime;
        gw.initializeNewGame();
        while (running) {
            lastTime = startTime;
            startTime = System.currentTimeMillis();
            try {
                gw.tickTime(((float)(startTime - lastTime))/1000f);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            sleepTime = ticksPS-(System.currentTimeMillis() - startTime);
            try {
                if (sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }
}

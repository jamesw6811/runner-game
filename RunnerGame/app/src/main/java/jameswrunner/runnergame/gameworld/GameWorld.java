package jameswrunner.runnergame.gameworld;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import jameswrunner.runnergame.GameService;
import jameswrunner.runnergame.RunMapActivity;
import jameswrunner.runnergame.controls.RunningMediaController;

import static jameswrunner.runnergame.maputils.MapUtilities.locationToLatLng;

/**
 * Created by james on 6/17/2017.
 */

public class GameWorld {
    private static final int METERS_PER_SPIRIT = 30;

    public static final int ANNOUNCEMENT_PERIOD = 20 * 1000;
    public static final double NAV_BEEP_PERIOD_MULTIPLIER = 2500.0 / 300.0; // millis period per meter
    private static final String LOGTAG = GameWorld.class.getName();


    private LatLng lastGPS;
    private GameWorldThread gameWorldThread;
    private GameService gameService;

    private long lastAnnouncementTime = 0;
    private RunningMediaController.ClickState clickState;
    private double metersSinceSpirit = 0;

    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    private Player player;
    private Headquarters headquarters = null;

    public GameWorld(Location firstGPS, GameService gs) {
        gameService = gs;
        lastGPS = locationToLatLng(firstGPS);
        clickState = gameService.getController().getClickState(true);
        player = new Player(this, lastGPS);
        focusCameraOnGameObject(player);
    }

    public synchronized void clearUIState() {
        for (GameObject go : gameObjects) {
            go.clearMarkerState();
        }
    }

    public void refreshUIState() {
        focusCameraOnGameObject(player);
        for (GameObject go : gameObjects) {
            go.updateMarker();
        }
    }

    public synchronized void updateGPS(Location loc) {
        LatLng ll = locationToLatLng(loc);
        lastGPS = ll;
    }

    public void initializeAndStartRunning() {
        if (gameWorldThread == null || !gameWorldThread.isAlive()) {
            gameWorldThread = new GameWorldThread(this);
            gameWorldThread.start();
            speakTTS("Game started.");
        }
    }

    public void stopRunning() {
        if (gameWorldThread != null) {
            gameWorldThread.stopRunning();
        }
    }

    public synchronized void tickTime(final float timeDelta) {
        // Update state from controller input and GPS input
        clickState = gameService.getController().getClickState(true);
        player.updatePosition(lastGPS);

        // Check discoveries
        metersSinceSpirit += player.getLastDistanceTravelled();
        if (metersSinceSpirit > METERS_PER_SPIRIT) {
            metersSinceSpirit -= METERS_PER_SPIRIT;
            player.giveSpirits(1);
            speakTTS("" + player.getSpirits() + " spirits.");
        }

        // Check building
        if (clickState.doubleClicked) {
            if (headquarters == null && player.getSpirits() >= 10) {
                player.takeSpirits(10);
                headquarters = new Headquarters(this, player.getPosition(), "Spirit Well", "Well");
            }
        }

        // Check announcements
        if (System.currentTimeMillis() - lastAnnouncementTime > ANNOUNCEMENT_PERIOD) {
            if (headquarters == null && player.getSpirits() >= 10) {
                speakTTS("You have enough spirits to create the spirit well.");
            }
            lastAnnouncementTime = System.currentTimeMillis();
        }

        // Check win conditions and draw
        checkWinConditions();
    }

    private void refreshAnnouncement() {
        lastAnnouncementTime = 0;
    }

    private void checkWinConditions() {

    }

    // Only called from GameObject
    public void addObject(GameObject gameObject) {
        gameObjects.add(gameObject);
    }

    // Only called from GameObject
    public void removeObject(GameObject gameObject) {
        gameObjects.remove(gameObject);
    }


    private void speakTTS(CharSequence speech) {
        gameService.getTTSRunner().addSpeech(speech);
    }

    private void focusCameraOnPosition(final LatLng ll, final float zoom) {
        gameService.passMapUpdate(new RunMapActivity.MapUpdate() {
            @Override
            public void updateMap(GoogleMap map) {
                map.moveCamera(CameraUpdateFactory.newLatLng(ll));
                map.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            }
        });
    }

    public void focusCameraOnGameObject(GameObject go) {
        if (go != null) focusCameraOnPosition(go.getPosition(), 17f);
    }

    protected GameService getGameService(){
        return gameService;
    }

}

package jamesw6811.secrets.gameworld;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.RunMapActivity;
import jamesw6811.secrets.controls.RunningMediaController;
import jamesw6811.secrets.gameworld.chase.ChaseManager;
import jamesw6811.secrets.gameworld.difficulty.DifficultySettings;
import jamesw6811.secrets.gameworld.map.GameUIUpdateProcessor;
import jamesw6811.secrets.gameworld.map.MapManager;
import jamesw6811.secrets.gameworld.map.Player;
import jamesw6811.secrets.gameworld.story.StoryManager;
import jamesw6811.secrets.sound.TextToSpeechRunner;
import jamesw6811.secrets.sound.ToneRunner;
import jamesw6811.secrets.time.TimeTicked;
import jamesw6811.secrets.time.TimeTickerThread;

import static jamesw6811.secrets.location.MapUtilities.locationToLatLng;

/**
 * Created by james on 6/17/2017.
 */

public class GameWorld implements TimeTicked {

    private static final String LOGTAG = GameWorld.class.getName();

    private TimeTickerThread timeTickerThread;

    private RunningMediaController controller;
    private RunningMediaController.ClickState lastClickState;

    private Player player;

    private ChaseManager chaseManager;
    private DifficultySettings difficultySettings;
    private StoryManager storyManager;
    private MapManager mapManager;
    private GameUIUpdateProcessor ui;
    private LatLng lastGPS;

    public GameWorld(Location firstGPS, double pace, Context ctx, GameUIUpdateProcessor ui, TextToSpeechRunner tts, ToneRunner tone, RunningMediaController controller) {
        this.controller = controller;
        lastClickState = controller.getClickState(true);
        this.ui = ui;
        updateGPS(firstGPS);

        difficultySettings = new DifficultySettings();
        difficultySettings.setPaceSettings(pace);

        storyManager = new StoryManager(ctx, player, tts) // Player is null here because both map manager relies on storymanager and vice versa

        mapManager = new MapManager(difficultySettings, storyManager, chaseManager, ui, ctx, locationToLatLng(firstGPS));
        player = mapManager.getPlayer();

        chaseManager = new ChaseManager(difficultySettings, storyManager, tone);


        focusCameraOnPlayer();
    }

    public void initializeAndStartRunning() {
        if (timeTickerThread == null || !timeTickerThread.isAlive()) {
            timeTickerThread = new TimeTickerThread(this);
            timeTickerThread.start();
            storyManager.gameStarted();
        }
    }

    public void stopRunning() {
        if (timeTickerThread != null) {
            timeTickerThread.stopRunning();
        }
    }

    public synchronized void tickTime(final float timeDelta) {
        // Get player inputs
        updatePlayerState();

        // Handle time
        mapManager.tickAll(timeDelta);

        // Handle map movement
        mapManager.discoverResourcesAndSites();
        mapManager.handleApproach();

        // Handle chase
        chaseManager.handleChase(timeDelta, player.getLastDistanceTravelled());

        // Handle control interactions
        mapManager.handleInteractions(lastClickState);

        // Handle story & announcements
        storyManager.doAnnouncements();
        if (storyManager.checkWinConditions()) {
            timeTickerThread.stopRunning();
            storyManager.setOnDoneSpeaking(() -> ui.finishAndDebrief());
        }
    }

    public synchronized void clearUIState() {
        mapManager.clearUIState();
    }

    public void refreshUIState() {
        focusCameraOnPlayer();
        mapManager.refreshUIState();
    }

    public synchronized void updateGPS(Location loc) {
        LatLng ll = locationToLatLng(loc);
        lastGPS = ll;
    }

    // Update state from controller input and GPS input
    private void updatePlayerState() {
        lastClickState = controller.getClickState(true);
        player.updatePosition(lastGPS);
        focusCameraOnPlayer();
    }

    private void focusCameraOnPosition(final LatLng ll, final float zoom) {
        ui.processMapUpdate(new RunMapActivity.MapUpdate() {
            @Override
            public void updateMap(GoogleMap map) {
                map.moveCamera(CameraUpdateFactory.newLatLng(ll));
                map.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            }
        });
    }

    private void focusCameraOnPlayer() {
        if (player != null) focusCameraOnPosition(player.getPosition(), 17f);
    }

}


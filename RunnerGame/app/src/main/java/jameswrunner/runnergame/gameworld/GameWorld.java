package jameswrunner.runnergame.gameworld;

import android.location.Location;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import jameswrunner.runnergame.GameService;
import jameswrunner.runnergame.R;
import jameswrunner.runnergame.RunMapActivity;
import jameswrunner.runnergame.controls.RunningMediaController;

import static jameswrunner.runnergame.maputils.MapUtilities.locationToLatLng;

/**
 * Created by james on 6/17/2017.
 */

public class GameWorld {
    private static final int METERS_PER_RUNNING_RESOURCE = 50;
    private static final double METERS_IN_SIGHT = 15;
    private static final double METERS_DISCOVERY_MINIMUM = 250;

    public static final int ANNOUNCEMENT_PERIOD = 60 * 1000;
    public static final double NAV_BEEP_PERIOD_MULTIPLIER = 2500.0 / 300.0; // millis period per meter
    private static final String LOGTAG = GameWorld.class.getName();

    private LatLng lastGPS;
    private GameWorldThread gameWorldThread;
    private GameService gameService;

    private long lastAnnouncementTime = -ANNOUNCEMENT_PERIOD;
    private RunningMediaController.ClickState clickState;
    private double metersSinceRunningResource = 0;
    private Random random;

    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    private Player player;
    private Headquarters headquarters = null;

    public boolean tutorialFirstResource = false;
    public boolean tutorialHQbuilt = false;
    public boolean tutorialResourceBuildingDiscovered = false;
    public boolean tutorialResourceBuildingUpgraded = false;
    public boolean tutorialResourceBuildingCollected = false;
    public boolean tutorialSubResourceBuildingCollected = false;
    public boolean tutorialCompleted = false;

    public GameWorld(Location firstGPS, GameService gs) {
        gameService = gs;
        lastGPS = locationToLatLng(firstGPS);
        clickState = gameService.getController().getClickState(true);
        player = new Player(this, lastGPS);
        random = new Random();
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
            speakTTS(gameService.getString(R.string.game_started));
        }
    }

    public void stopRunning() {
        if (gameWorldThread != null) {
            gameWorldThread.stopRunning();
        }
    }

    public synchronized void tickTime(final float timeDelta) {
        updatePlayerState();
        tickAll(timeDelta);
        discoverSites();
        speakSights();
        handleInteractions();
        doAnnouncements();
        checkWinConditions();
    }

    // Update state from controller input and GPS input
    private void updatePlayerState() {
        clickState = gameService.getController().getClickState(true);
        player.updatePosition(lastGPS);
    }

    // Tick all GameWorld objects to handle time-related events
    private void tickAll(float timeDelta) {
        for (GameObject go : gameObjects) go.tickTime(timeDelta);
    }

    private LinkedList<GameObject> getObjectsInCircle(LatLng center, double radius) {
        LinkedList<GameObject> sights = new LinkedList<>();
        for (GameObject go : gameObjects) {
            double distance = SphericalUtil.computeDistanceBetween(go.getPosition(), center);
            if (distance <= radius) {
                sights.add(go);
            }
        }
        return sights;
    }

    // Discover new sites
    private void discoverSites() {
        LinkedList<GameObject> objectsInDiscoveryRange = getObjectsInCircle(player.getPosition(), METERS_DISCOVERY_MINIMUM);
        objectsInDiscoveryRange.remove(player);
        // Check discoveries
        // Spirit discovery
        metersSinceRunningResource += player.getLastDistanceTravelled();
        if (metersSinceRunningResource > METERS_PER_RUNNING_RESOURCE) {
            metersSinceRunningResource -= METERS_PER_RUNNING_RESOURCE;
            player.giveRunningResource(1);
            speakTTS(gameService.getString(R.string.receivedMovementResource));
            if (!tutorialFirstResource) refreshAnnouncement();
            tutorialFirstResource = true;
        }
        // Discovery - no other buildings in range
        if (headquarters != null && objectsInDiscoveryRange.size() == 0) {
            double discoverSeed = random.nextDouble();
            GameObject discovery = null;
            if (discoverSeed < 0.7) {
                discovery = new BuildingResourceSite(this, player.getPosition());
                if (!tutorialResourceBuildingDiscovered) refreshAnnouncement();
                tutorialResourceBuildingDiscovered = true;
            } else if (discoverSeed < 1.0 && tutorialResourceBuildingDiscovered) {
                discovery = new BuildingSubResourceSite(this, player.getPosition());
            }
            if (discovery != null) {
                speakTTS(gameService.getString(R.string.discoveredNotification, discovery.getSpokenName()));
            }
        }
    }

    // Handle interaction with nearby sites based on user input
    private void handleInteractions() {
        LinkedList<GameObject> objectsInInteractionRange = getObjectsInCircle(player.getPosition(), METERS_IN_SIGHT);
        objectsInInteractionRange.remove(player);
        // Check building or building upgrade
        if (clickState.doubleClicked) {
            if (headquarters == null && player.getRunningResource() >= Headquarters.RUNNING_RESOURCE_BUILD_COST) {
                player.takeRunningResource(Headquarters.RUNNING_RESOURCE_BUILD_COST);
                headquarters = new Headquarters(this, player.getPosition());
                speakTTS(gameService.getString(R.string.headquarters_build));
                tutorialHQbuilt = true;
            } else {
                Iterator<GameObject> goit = objectsInInteractionRange.iterator();
                while (goit.hasNext()){
                    GameObject tryUpgrade = goit.next();
                    if (tryUpgrade.isUpgradable()) {
                        tryUpgrade.upgrade(player);
                        refreshAnnouncement();
                        break;
                    }
                }
            }
        }
        // Check interaction
        if (clickState.singleClicked) {
            Iterator<GameObject> goit = objectsInInteractionRange.iterator();
            while (goit.hasNext()){
                GameObject tryInteract = goit.next();
                if (tryInteract.isInteractable()) {
                    tryInteract.interact(player);
                    refreshAnnouncement();
                    break;
                }
            }
        }
    }

    // Speak location names to the player as they approach known locations
    private void speakSights() {
        LinkedList<GameObject> newsights = getObjectsInCircle(player.getPosition(), METERS_IN_SIGHT);
        LinkedList<GameObject> oldsights = getObjectsInCircle(player.getLastPosition(), METERS_IN_SIGHT);
        newsights.removeAll(oldsights);
        newsights.remove(player);

        if (newsights.size() > 0){
            Iterator<GameObject> nextSight = newsights.iterator();
            String sightsText = "";
            while (nextSight.hasNext()) {
                String spokenName = nextSight.next().getSpokenName();
                if (newsights.size() == 1) {
                    sightsText += spokenName;
                } else if (nextSight.hasNext()){
                    sightsText += spokenName + gameService.getString(R.string.list_separator);
                } else {
                    sightsText += gameService.getString(R.string.list_final_and) + spokenName;
                }
            }
            speakTTS(gameService.getString(R.string.approachingAnnounce, sightsText));
        }
    }

    // Make announcements about the player's available resources and tutorial messages at a certain time interval
    private void doAnnouncements() {
        if (System.currentTimeMillis() - lastAnnouncementTime > ANNOUNCEMENT_PERIOD) {
            String resourceAnnounce = "";
            if (player.getRunningResource() > 0) resourceAnnounce += gameService.getString(R.string.movementResourceAnnounce, player.getRunningResource());
            else resourceAnnounce += gameService.getString(R.string.movementResourceAnnounceNone);
            if (player.getBuildingResource() > 0) resourceAnnounce +=  gameService.getString(R.string.buildingResourceAnnounce, player.getBuildingResource());
            speakTTS(resourceAnnounce);
            if (!tutorialFirstResource) speakTTS(gameService.getString(R.string.tutorialFirstResource));
            else if (!tutorialHQbuilt) {
                if (player.getRunningResource() < Headquarters.RUNNING_RESOURCE_BUILD_COST) speakTTS(gameService.getString(R.string.tutorialHQbuilt_notEnoughResources));
                else speakTTS(gameService.getString(R.string.tutorialHQbuilt_readyToBuild));
            }
            else if (!tutorialResourceBuildingDiscovered) {
                speakTTS(gameService.getString(R.string.tutorialResourceBuildingDiscovered));
            }
            else if (!tutorialResourceBuildingUpgraded) {
                speakTTS(gameService.getString(R.string.tutorialResourceBuildingUpgraded, BuildingResourceSite.RUNNING_RESOURCE_UPGRADE_COST));
            }
            else if (!tutorialResourceBuildingCollected) {
                speakTTS(gameService.getString(R.string.tutorialResourceBuildingCollected));
            }
            else if (!tutorialSubResourceBuildingCollected) {
                speakTTS(gameService.getString(R.string.tutorialSubResourceBuildingCollected));
            } else if (!tutorialCompleted) {
                speakTTS(gameService.getString(R.string.tutorialCompleted));
                tutorialCompleted = true;
            }
            lastAnnouncementTime = System.currentTimeMillis();
        }
    }

    // Refresh the announcement time so that it plays as soon as possible.
    private void refreshAnnouncement() {
        lastAnnouncementTime = -ANNOUNCEMENT_PERIOD;
    }

    // Check if the win/lose conditions for the game have been met and take action accordingly
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


    protected void speakTTS(CharSequence speech) {
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

package jamesw6811.secrets.gameworld.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import jamesw6811.secrets.R;
import jamesw6811.secrets.RunMapActivity;
import jamesw6811.secrets.controls.RunningMediaController;
import jamesw6811.secrets.gameworld.chase.ChaseManager;
import jamesw6811.secrets.gameworld.difficulty.DifficultySettings;
import jamesw6811.secrets.gameworld.map.discovery.DiscoveryScheme;
import jamesw6811.secrets.gameworld.map.site.SiteFactory;
import jamesw6811.secrets.gameworld.story.StoryManager;
import jamesw6811.secrets.sound.TextToSpeechRunner;

public class MapManager {
    private ArrayList<GameObject> gameObjects = new ArrayList<>();
    private Map<String, GameObject> gameObjectRegistry = new HashMap<>();
    private double metersSinceRunningResource = 0;
    private float metersRunningTotal = 0;
    private Player player;
    private DifficultySettings difficultySettings;
    private StoryManager story;
    private ChaseManager chase;
    private Context ctx;
    private GameUIUpdateProcessor ui;

    public MapManager(DifficultySettings dm, StoryManager st, ChaseManager cm, GameUIUpdateProcessor ui, Context ctx, LatLng playerStart) {
        this.ctx = ctx;
        difficultySettings = dm;
        story = st;
        chase = cm;
        this.ui = ui;
        player = new Player(this, playerStart);
    }

    public Player getPlayer() {
        return player;
    }

    // Handle approach activities and speak location names to the player as they approach known locations
    public void handleApproach() {
        LinkedList<GameObject> newsights = getObjectsInCircle(player.getPosition(), difficultySettings.getMetersInSight());
        LinkedList<GameObject> oldsights = getObjectsInCircle(player.getLastPosition(), difficultySettings.getMetersInSight());
        newsights.removeAll(oldsights);
        newsights.remove(player);

        // Speak location names
        if (newsights.size() > 0) {
            Iterator<GameObject> nextSight = newsights.iterator();
            String sightsText = "";
            while (nextSight.hasNext()) {
                String spokenName = nextSight.next().getSpokenName();
                if (newsights.size() == 1) {
                    sightsText += spokenName;
                } else if (nextSight.hasNext()) {
                    sightsText += spokenName + ctx.getString(R.string.list_separator);
                } else {
                    sightsText += ctx.getString(R.string.list_final_and) + spokenName;
                }
            }
            story.addSpeechToQueue(ctx.getString(R.string.approachingAnnounce, sightsText));
        }

        // Handle approach activities
        for (GameObject tryApproach : newsights) {
            if (tryApproach.hasApproachActivity()) {
                tryApproach.approach();
            }
        }
    }

    // Handle interaction with nearby sites based on user input
    public void handleInteractions(RunningMediaController.ClickState clickState) {
        // Don't allow interaction if injured
        if (player.isInjured()) {
            if (clickState.playClicked)
                story.addSpeechToQueue("You are too injured to do that. Get to a Dead Drop to patch yourself up.");
            return;
        }

        // Get objects in interaction range
        LinkedList<GameObject> objectsInInteractionRange = getObjectsInCircle(player.getPosition(), difficultySettings.getMetersInSight());
        objectsInInteractionRange.remove(player);

        // Check interaction
        if (clickState.playClicked) {
            boolean interacted = false;
            for (GameObject tryInteract : objectsInInteractionRange) {
                if (tryInteract.isInteractable()) {
                    tryInteract.interact();
                    story.refreshAnnouncement();
                    interacted = true;
                    break;
                }
            }
            if (!interacted)
                story.interruptQueueWithSpeech(ctx.getString(R.string.interaction_nothing_to_interact));
        }
    }

    // Discover new sites
    public void discoverResourcesAndSites() {
        LinkedList<GameObject> objectsInDiscoveryRange = getObjectsInCircle(player.getPosition(), difficultySettings.getMetersDiscoveryMinimum());
        objectsInDiscoveryRange.remove(player);

        metersRunningTotal += player.getLastDistanceTravelled();

        // Resource discovery
        metersSinceRunningResource += player.getLastDistanceTravelled();
        if (metersSinceRunningResource > difficultySettings.getMetersPerRunningResource()) {
            metersSinceRunningResource -= difficultySettings.getMetersPerRunningResource();
            int runningResource = 1;
            if (player.getDistanceFromCollectedRunningResources() <= difficultySettings.getMetersPerRunningResource()){
                runningResource += 2*player.getUpgradeLevelLapSupporter();
            } else {
                runningResource += 2*player.getUpgradeLevelDiscoverySupporter();
            }
            player.giveRunningResource(runningResource);
        }

        // Site discovery
        if (objectsInDiscoveryRange.size() == 0) {
            GameObject discovered = SiteFactory.getSite(story.discoverSite(), this, player.getPosition());
            if (discovered != null) {
                story.addSpeechToQueue(ctx.getString(R.string.discoveredNotification, discovered.getSpokenName()));
                if (discovered.hasApproachActivity()) discovered.approach();
            }
        }
    }

    // Tick all GameWorld objects to handle time-related events
    public void tickAll(float timeDelta) {
        for (GameObject go : gameObjects) go.tickTime(timeDelta);
    }

    public synchronized void clearUIState() {
        for (GameObject go : gameObjects) {
            go.clearMarkerState();
        }
    }

    public void refreshUIState() {
        for (GameObject go : gameObjects) {
            go.updateMarker();
        }
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

    public Context getContext() {
        return ctx;
    }

    public float getMetersRunningTotal() {
        return metersRunningTotal;
    }

    public static abstract class GameObject {
        GameUIUpdateProcessor ui;
        protected StoryManager story;
        protected ChaseManager chase;
        protected Player player;
        protected Context ctx;
        private MapManager mm;
        private String spokenName;
        private LatLng position;

        public GameObject(MapManager mm, String spokenName, LatLng latLng) {
            this.mm = mm;
            this.ui = mm.ui;
            this.story = mm.story;
            this.chase = mm.chase;
            this.player = mm.getPlayer();
            this.ctx = mm.ctx;
            this.spokenName = spokenName;
            this.position = latLng;
            mm.gameObjects.add(this);
            updateMarker();
        }

        public final MapManager getMap() {
            return mm;
        }

        protected final GameObject getRegisteredObject(String id){
            return mm.gameObjectRegistry.get(id);
        }

        protected final void registerThis(String id){
            mm.gameObjectRegistry.put(id, this);
        }

        public void destroy() {
            ui.processMapUpdate(new RunMapActivity.MapUpdate() {
                @Override
                public void updateMap(GoogleMap map) {
                    removeMarker();

                }
            });
            mm.gameObjects.remove(this);
        }

        protected void updateMarker() {
            boolean activeUI = ui.processMapUpdate(new RunMapActivity.MapUpdate() {
                @Override
                public void updateMap(GoogleMap map) {
                    drawMarker(map);
                }
            });
            if (!activeUI) clearMarkerState();
        }

        protected abstract void drawMarker(GoogleMap gm);

        protected abstract void clearMarkerState();

        protected abstract void removeMarker();

        protected String getSpokenName() {
            return spokenName;
        }

        protected void setSpokenName(String spokenName) {
            this.spokenName = spokenName;
        }

        public LatLng getPosition() {
            return position;
        }

        protected void setPosition(LatLng position) {
            this.position = position;
            updateMarker();
        }

        protected void tickTime(float timeDelta) {

        }

        protected boolean isInteractable() {
            return false;
        }

        protected void interact() {
            throw new UnsupportedOperationException("This object is not interactable.");
        }

        protected boolean hasApproachActivity() {
            return false;
        }

        protected void approach() {
            throw new UnsupportedOperationException("This object does not have an approach activity.");
        }
    }

}

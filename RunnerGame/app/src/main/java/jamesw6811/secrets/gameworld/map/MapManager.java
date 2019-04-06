package jamesw6811.secrets.gameworld.map;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import jamesw6811.secrets.R;
import jamesw6811.secrets.RunMapActivity;
import jamesw6811.secrets.controls.RunningMediaController;
import jamesw6811.secrets.gameworld.chase.ChaseManager;
import jamesw6811.secrets.gameworld.difficulty.DifficultySettings;
import jamesw6811.secrets.gameworld.story.StoryManager;
import jamesw6811.secrets.sound.TextToSpeechRunner;
import jamesw6811.secrets.sound.ToneRunner;

public class MapManager {
    private Random random;
    private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    private Headquarters headquarters = null;
    private double metersSinceRunningResource = 0;
    private Player player;
    private DifficultySettings difficultySettings;
    private StoryManager story;
    private ChaseManager chase;
    private Context ctx;
    private GameUIUpdateProcessor ui;

    public MapManager(DifficultySettings dm, StoryManager st, ChaseManager cm, GameUIUpdateProcessor ui, Context ctx, LatLng playerStart){
        this.ctx = ctx;
        difficultySettings = dm;
        story = st;
        chase = cm;
        this.ui = ui;
        random = new Random();
        player = new Player(this, playerStart);
    }

    public Player getPlayer(){
        return player;
    }

    // Handle approach activities and speak location names to the player as they approach known locations
    public void handleApproach() {
        LinkedList<GameObject> newsights = getObjectsInCircle(player.getPosition(), difficultySettings.getMetersInSight());
        LinkedList<GameObject> oldsights = getObjectsInCircle(player.getLastPosition(), difficultySettings.getMetersInSight());
        newsights.removeAll(oldsights);
        newsights.remove(player);

        // Handle approach activities
        for (GameObject tryApproach : newsights) {
            if (tryApproach.hasApproachActivity()) {
                tryApproach.approach();
            }
        }

        // Speak location names
        if (newsights.size() > 0){
            Iterator<GameObject> nextSight = newsights.iterator();
            String sightsText = "";
            while (nextSight.hasNext()) {
                String spokenName = nextSight.next().getSpokenName();
                if (newsights.size() == 1) {
                    sightsText += spokenName;
                } else if (nextSight.hasNext()){
                    sightsText += spokenName + ctx.getString(R.string.list_separator);
                } else {
                    sightsText += ctx.getString(R.string.list_final_and) + spokenName;
                }
            }
            story.addSpeechToQueue(ctx.getString(R.string.approachingAnnounce, sightsText));
        }
    }

    // Handle interaction with nearby sites based on user input
    public void handleInteractions(RunningMediaController.ClickState clickState) {
        // Don't allow interaction if injured
        if (player.isInjured()){
            if (clickState.singleClicked || clickState.doubleClicked) story.addSpeechToQueue(ctx.getString(R.string.interaction_injured));
            return;
        }

        // Get objects in interaction range
        LinkedList<GameObject> objectsInInteractionRange = getObjectsInCircle(player.getPosition(), difficultySettings.getMetersInSight());
        objectsInInteractionRange.remove(player);

        // Check building or building upgrade
        if (clickState.doubleClicked) {
            if (headquarters == null) {
                if (player.getRunningResource() >= Headquarters.RUNNING_RESOURCE_BUILD_COST) {
                    player.takeRunningResource(Headquarters.RUNNING_RESOURCE_BUILD_COST);
                    headquarters = new Headquarters(this, player.getPosition());
                    story.interruptQueueWithSpeech(ctx.getString(R.string.headquarters_build));
                    story.refreshAnnouncement();
                    story.tutorialHQbuilt = true;
                } else {
                    story.interruptQueueWithSpeech(ctx.getString(R.string.headquarters_build_not_enough_resources, Headquarters.RUNNING_RESOURCE_BUILD_COST));
                    story.addSpeechToQueue(TextToSpeechRunner.CRED_EARCON);
                }
            } else {
                boolean upgraded = false;
                for (GameObject tryUpgrade : objectsInInteractionRange) {
                    if (tryUpgrade.isUpgradable()) {
                        tryUpgrade.upgrade();
                        story.refreshAnnouncement();
                        upgraded = true;
                        break;
                    }
                }
                if (!upgraded) story.interruptQueueWithSpeech(ctx.getString(R.string.interaction_nothing_to_upgrade));
            }
        }
        // Check interaction
        if (clickState.singleClicked) {
            boolean interacted = false;
            for (GameObject tryInteract : objectsInInteractionRange) {
                if (tryInteract.isInteractable()) {
                    tryInteract.interact();
                    story.refreshAnnouncement();
                    interacted = true;
                    break;
                }
            }
            if (!interacted) story.interruptQueueWithSpeech(ctx.getString(R.string.interaction_nothing_to_interact));
        }
    }

    // Discover new sites
    public void discoverResourcesAndSites() {
        LinkedList<GameObject> objectsInDiscoveryRange = getObjectsInCircle(player.getPosition(), difficultySettings.getMetersDiscoveryMinimum());
        objectsInDiscoveryRange.remove(player);
        // Check discoveries
        // Spirit discovery
        metersSinceRunningResource += player.getLastDistanceTravelled();
        if (metersSinceRunningResource > difficultySettings.getMetersPerRunningResource()) {
            metersSinceRunningResource -= difficultySettings.getMetersPerRunningResource();
            player.giveRunningResource(1);
            story.addSpeechToQueue(TextToSpeechRunner.CRED_EARCON);
            if (!story.tutorialFirstResource) story.refreshAnnouncement();
            story.tutorialFirstResource = true;
        }
        // Discovery - no other buildings in range
        if (headquarters != null && objectsInDiscoveryRange.size() == 0) {
            double discoverSeed = random.nextDouble();
            GameObject discovery = null;
            if (discoverSeed < 0.35) {
                discovery = new BuildingResourceSite(this, player.getPosition());
                if (!story.tutorialResourceBuildingDiscovered) story.refreshAnnouncement();
                story.tutorialResourceBuildingDiscovered = true;
            } else if (discoverSeed < 0.70) {
                discovery = new BuildingSubResourceSite(this, player.getPosition());
            } else if (discoverSeed < 1.00) {
                discovery = new ChaseSite(this, player.getPosition());
            }
            if (discovery != null) {
                story.addSpeechToQueue(ctx.getString(R.string.discoveredNotification, discovery.getSpokenName()));
                if (discovery.hasApproachActivity()) discovery.approach();
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

    static abstract class GameObject {
        private MapManager mm;
        private String spokenName;
        private LatLng position;
        GameUIUpdateProcessor ui;
        StoryManager story;
        ChaseManager chase;
        Player player;
        Context ctx;

        GameObject(MapManager mm, String spokenName, LatLng latLng){
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

        MapManager getMap(){
            return mm;
        }

        void destroy() {
            ui.processMapUpdate(new RunMapActivity.MapUpdate() {
                @Override
                public void updateMap(GoogleMap map) {
                    removeMarker();

                }
            });
            mm.gameObjects.remove(this);
        }

        void updateMarker() {
            boolean activeUI = ui.processMapUpdate(new RunMapActivity.MapUpdate() {
                @Override
                public void updateMap(GoogleMap map) {
                    drawMarker(map);
                }
            });
            if (!activeUI) clearMarkerState();
        }

        abstract void drawMarker(GoogleMap gm);

        abstract void clearMarkerState();

        abstract void removeMarker();

        String getSpokenName() {
            return spokenName;
        }

        void setSpokenName(String spokenName) {
            this.spokenName = spokenName;
        }

        public LatLng getPosition() {
            return position;
        }

        void setPosition(LatLng position) {
            this.position = position;
            updateMarker();
        }

        boolean isUpgradable() {
            return false;
        }

        void upgrade() {
            throw new UnsupportedOperationException("This object is not upgradable.");
        }

        void tickTime(float timeDelta) {

        }

        boolean isInteractable() {
            return false;
        }

        void interact() {
            throw new UnsupportedOperationException("This object is not interactable.");
        }

        boolean hasApproachActivity() {
            return false;
        }

        void approach() {
            throw new UnsupportedOperationException("This object does not have an approach activity.");
        }
    }

    Context getContext(){
        return ctx;
    }

}

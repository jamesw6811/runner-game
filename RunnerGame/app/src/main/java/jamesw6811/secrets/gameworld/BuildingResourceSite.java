package jamesw6811.secrets.gameworld;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import jamesw6811.secrets.GameService;
import jamesw6811.secrets.R;

/**
 * Created by james on 6/17/2017.
 */

class BuildingResourceSite extends GameObject {
    public static final int RUNNING_RESOURCE_UPGRADE_COST = 10;
    public static final int MAX_RESOURCE = 10;
    public static final float RESOURCE_GENERATION_PERIOD = 30f;
    public static final int RESOURCE_AMOUNT_ON_BUILD = 5;

    private Marker marker;
    private boolean built = false;
    private int resource = 0;
    private float timeSinceLastResource = 0;

    BuildingResourceSite(GameWorld gw, LatLng pos) {
        super(gw, gw.getGameService().getString(R.string.buildingresourcesite_spokenName), pos);
    }

    synchronized void clearMarkerState() {
        marker = null;
    }

    @Override
    synchronized void removeMarker() {
        if (marker != null) marker.remove();
    }

    synchronized void drawMarker(GameService gs, GoogleMap map) {
        if (marker == null) {
            MarkerOptions mo = new MarkerOptions().position(getPosition()).visible(true);
            marker = map.addMarker(mo);
        } else {
            marker.setPosition(getPosition());
        }

        if (marker != null) {
            IconGenerator ig = new IconGenerator(gs);
            if (built){
                ig.setColor(Color.GREEN);
            } else {
                ig.setColor(Color.BLACK);
            }
            Bitmap icon = ig.makeIcon(gs.getString(R.string.buildingresourcesite_mapName));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    String getSpokenName(){
        if (!built) return super.getSpokenName();
        else return getGameWorld().getGameService().getString(R.string.buildingresourcesite_spokenNameWithResources, super.getSpokenName(), resource);
    }

    private void setBuilt(boolean b){
        built = b;
        updateMarker();
    }

    @Override
    boolean isUpgradable() {
        return !built;
    }

    @Override
    void upgrade() {
        Player player = getGameWorld().getPlayer();
        if (built) throw new UnsupportedOperationException("Cannot upgrade further.");
        if (player.getRunningResource() >= RUNNING_RESOURCE_UPGRADE_COST) {
            player.takeRunningResource(RUNNING_RESOURCE_UPGRADE_COST);
            setBuilt(true);
            resource = RESOURCE_AMOUNT_ON_BUILD;
            getGameWorld().tutorialResourceBuildingUpgraded = true;
            getGameWorld().interruptQueueWithSpeech(getGameWorld().getGameService().getString(R.string.buildingresourcesite_upgradeSuccess, RUNNING_RESOURCE_UPGRADE_COST));
        } else {
            getGameWorld().interruptQueueWithSpeech(getGameWorld().getGameService().getString(R.string.buildingresourcesite_upgradeNotEnoughResources, RUNNING_RESOURCE_UPGRADE_COST));
        }
    }

    @Override
    boolean isInteractable() {
        return true;
    }

    @Override
    void interact() {
        if (!built){
            getGameWorld().interruptQueueWithSpeech(getGameWorld().getGameService().getString(R.string.upgrade_needed_to_interact));
            return;
        }

        Player player = getGameWorld().getPlayer();
        if (resource > 0) {
            getGameWorld().tutorialResourceBuildingCollected = true;
            getGameWorld().interruptQueueWithSpeech(getGameWorld().getGameService().getString(R.string.buildingresourcesite_collectSuccess, resource));
            player.giveBuildingResource(resource);
            resource = 0;
        } else {
            getGameWorld().interruptQueueWithSpeech(getGameWorld().getGameService().getString(R.string.buildingresourcesite_collectOutOfResource));
        }
    }

    @Override
    void tickTime(float timeDelta) {
        super.tickTime(timeDelta);
        if (built) {
            timeSinceLastResource += timeDelta;
            if (timeSinceLastResource > RESOURCE_GENERATION_PERIOD) {
                timeSinceLastResource -= RESOURCE_GENERATION_PERIOD;
                resource = Math.min(MAX_RESOURCE, resource + 1);
            }
        }
    }
}

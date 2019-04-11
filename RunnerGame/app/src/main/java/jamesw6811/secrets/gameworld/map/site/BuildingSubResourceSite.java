package jamesw6811.secrets.gameworld.map.site;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import jamesw6811.secrets.R;
import jamesw6811.secrets.gameworld.map.MapManager;

/**
 * Created by james on 6/17/2017.
 */

public class BuildingSubResourceSite extends MapManager.GameObject {
    public static final int RUNNING_RESOURCE_UPGRADE_COST = 10;
    public static final int BUILDING_SUBRESOURCE_AMOUNT_PER_TRADE = 1;
    public static final float BUILT_EXPIRY_DURATION = 10 * 60f;
    public final int BUILDING_RESOURCE_UPGRADE_COST = 10;
    public final int BUILDING_RESOURCE_TRADE_COST = 10;
    private float timeSinceBuilt = 0;
    private Marker marker;
    private boolean built = false;

    public BuildingSubResourceSite(MapManager mm, LatLng pos) {
        super(mm, mm.getContext().getString(R.string.buildingsubresourcesite_spokenName), pos);
    }

    protected synchronized void clearMarkerState() {
        marker = null;
    }

    @Override
    protected synchronized void removeMarker() {
        if (marker != null) marker.remove();
    }

    protected synchronized void drawMarker(GoogleMap map) {
        if (marker == null) {
            MarkerOptions mo = new MarkerOptions().position(getPosition()).visible(true);
            marker = map.addMarker(mo);
        } else {
            marker.setPosition(getPosition());
        }

        if (marker != null) {
            IconGenerator ig = new IconGenerator(ctx);
            if (built) {
                ig.setColor(Color.GREEN);
            } else {
                ig.setColor(Color.BLACK);
            }
            Bitmap icon = ig.makeIcon(ctx.getString(R.string.buildingsubresourcesite_mapName));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    protected String getSpokenName() {
        if (!built) return super.getSpokenName();
        else return ctx.getString(R.string.buildingsubresourcesite_spokenNameBuilt);
    }

    private void setBuilt(boolean b) {
        built = b;
        updateMarker();
    }

    @Override
    protected boolean isInteractable() {
        return true;
    }

    @Override
    protected void interact() {
        if (!built) {
            if (player.getRunningResource() >= RUNNING_RESOURCE_UPGRADE_COST && player.getBuildingResource() >= BUILDING_RESOURCE_UPGRADE_COST) {
                player.takeRunningResource(RUNNING_RESOURCE_UPGRADE_COST);
                player.takeBuildingResource(BUILDING_RESOURCE_UPGRADE_COST);
                setBuilt(true);
                story.interruptQueueWithSpeech(ctx.getString(R.string.buildingsubresourcesite_upgradeSuccess, RUNNING_RESOURCE_UPGRADE_COST, BUILDING_RESOURCE_UPGRADE_COST));
            } else {
                story.interruptQueueWithSpeech(ctx.getString(R.string.buildingsubresourcesite_upgradeNotEnoughResources, RUNNING_RESOURCE_UPGRADE_COST, BUILDING_RESOURCE_UPGRADE_COST));
            }
            return;
        }

        if (player.getBuildingResource() >= BUILDING_RESOURCE_TRADE_COST) {
            story.tutorialSubResourceBuildingCollected = true;
            player.takeBuildingResource(BUILDING_RESOURCE_TRADE_COST);
            story.interruptQueueWithSpeech(ctx.getString(R.string.buildingsubresourcesite_collectSuccess, BUILDING_RESOURCE_TRADE_COST, BUILDING_SUBRESOURCE_AMOUNT_PER_TRADE));
            player.giveBuildingSubResource(BUILDING_SUBRESOURCE_AMOUNT_PER_TRADE);
        } else {
            story.interruptQueueWithSpeech(ctx.getString(R.string.buildingsubresourcesite_collectNotEnoughResources, BUILDING_RESOURCE_TRADE_COST));
        }
    }

    @Override
    protected void tickTime(float timeDelta) {
        super.tickTime(timeDelta);
        if (built) {
            timeSinceBuilt += timeDelta;
            if (timeSinceBuilt > BUILT_EXPIRY_DURATION) {
                setBuilt(false);
                story.addSpeechToQueue(ctx.getString(R.string.buildingsubresourcesite_buildExpired));
            }
        }
    }
}

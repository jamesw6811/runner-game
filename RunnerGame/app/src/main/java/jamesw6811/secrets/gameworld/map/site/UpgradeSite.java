package jamesw6811.secrets.gameworld.map.site;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import jamesw6811.secrets.gameworld.map.MapManager;

/**
 * Created by james on 6/17/2017.
 */

public abstract class UpgradeSite extends MapManager.GameObject {
    private Marker marker;
    private boolean bought = false;

    public UpgradeSite(MapManager mm, String spokenName, LatLng latLng) {
        super(mm, spokenName, latLng);
    }

    protected abstract CharSequence getUpgradeSiteMapName();
    protected abstract CharSequence getUpgradeSiteSpokenNameBeforeUpgrade();
    protected abstract CharSequence getUpgradeSiteSpokenNameAfterUpgrade();
    protected abstract CharSequence getUpgradeSiteUpgradeSpeech();
    protected abstract CharSequence getUpgradeSiteNotEnoughResourcesSpeech();
    protected abstract int getUpgradeSiteUpgradeCost();

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
            if (bought) {
                ig.setColor(Color.GREEN);
            } else {
                ig.setColor(Color.BLACK);
            }
            Bitmap icon = ig.makeIcon(getUpgradeSiteMapName());
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    protected String getSpokenName() {
        if (bought){
            return getUpgradeSiteSpokenNameAfterUpgrade().toString();
        } else {
            return getUpgradeSiteSpokenNameBeforeUpgrade().toString();
        }
    }

    protected void setBought(boolean b) {
        bought = b;
        updateMarker();
    }

    @Override
    protected boolean isInteractable() {
        return true;
    }

    @Override
    protected void interact() {
        if (!bought) {
            if (player.getRunningResource() >= getUpgradeSiteUpgradeCost()) {
                player.takeRunningResource(getUpgradeSiteUpgradeCost());
                setBought(true);
                doUpgrade();
                story.interruptQueueWithSpeech(getUpgradeSiteUpgradeSpeech());
            } else {
                story.interruptQueueWithSpeech(getUpgradeSiteNotEnoughResourcesSpeech());
            }
        }
    }

    protected abstract void doUpgrade();
}

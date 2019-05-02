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

public abstract class CaptureSite extends MapManager.GameObject {
    private Marker marker;
    private boolean captured = false;

    public CaptureSite(MapManager mm, String spokenName, LatLng latLng) {
        super(mm, spokenName, latLng);
    }

    protected abstract CharSequence getCaptureSiteMapName();
    protected abstract CharSequence getCaptureSiteSpokenNameBeforeCapture();
    protected abstract CharSequence getCaptureSiteSpokenNameAfterCapture();
    protected abstract CharSequence getCaptureSiteCaptureSpeech();
    protected abstract CharSequence getCaptureSiteNotEnoughResourcesSpeech();
    protected abstract int getCaptureSiteCaptureCost();

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
            if (captured) {
                ig.setColor(Color.GREEN);
            } else {
                ig.setColor(Color.BLACK);
            }
            Bitmap icon = ig.makeIcon(getCaptureSiteMapName());
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    protected String getSpokenName() {
        if (captured){
            return getCaptureSiteSpokenNameAfterCapture().toString();
        } else {
            return getCaptureSiteSpokenNameBeforeCapture().toString();
        }
    }

    protected void setCaptured(boolean b) {
        captured = b;
        updateMarker();
    }

    @Override
    protected boolean isInteractable() {
        return true;
    }

    @Override
    protected void interact() {
        if (!captured) {
            if (player.getRunningResource() >= getCaptureSiteCaptureCost()) {
                player.takeRunningResource(getCaptureSiteCaptureCost());
                setCaptured(true);
                story.interruptQueueWithSpeech(getCaptureSiteCaptureSpeech());
            } else {
                story.interruptQueueWithSpeech(getCaptureSiteNotEnoughResourcesSpeech());
            }
        }
    }
}

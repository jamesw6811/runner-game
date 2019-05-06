package jamesw6811.secrets.gameworld.map.site;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import jamesw6811.secrets.gameworld.chase.ChaseOriginator;
import jamesw6811.secrets.gameworld.map.MapManager;

public abstract class ChaseSite extends MapManager.GameObject implements ChaseOriginator {
    public static final String EVENT_CHASE_SITE_CHASE_STARTED = "EVENT_CHASE_SITE_CHASE_STARTED";
    private Marker marker;
    private boolean disabled = false;

    public ChaseSite(MapManager mm, String spokenName, LatLng position) {
        super(mm, spokenName, position);
    }

    @Override
    protected void drawMarker(GoogleMap map) {
        if (marker == null) {
            MarkerOptions mo = new MarkerOptions().position(getPosition()).visible(true);
            marker = map.addMarker(mo);
        } else {
            marker.setPosition(getPosition());
        }

        if (marker != null) {
            IconGenerator ig = new IconGenerator(ctx);
            ig.setColor(Color.RED);
            Bitmap icon = ig.makeIcon(getChaseSiteMapName());
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    protected abstract CharSequence getChaseSiteMapName();

    @Override
    protected void clearMarkerState() {
        marker = null;
    }

    @Override
    protected void removeMarker() {
        if (marker != null) marker.remove();
    }

    @Override
    protected boolean hasApproachActivity() {
        return true;
    }

    @Override
    protected void approach() {
        if (!disabled) {
            chase.startChase(true, getChaseDifficulty(), this);
            story.interruptQueueWithSpeech(getChaseStartMessage());
            story.processEvent(EVENT_CHASE_SITE_CHASE_STARTED);
        }
    }

    public void chaserTrapped(){
        disabled = true;
    }

    protected abstract double getChaseDifficulty();
    protected abstract String getChaseStartMessage();
}

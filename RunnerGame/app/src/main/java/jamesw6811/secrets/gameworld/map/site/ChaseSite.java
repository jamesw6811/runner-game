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
import jamesw6811.secrets.gameworld.chase.ChaseOriginator;
import jamesw6811.secrets.gameworld.map.MapManager;

public abstract class ChaseSite extends MapManager.GameObject implements ChaseOriginator {
    private Marker marker;

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
            Bitmap icon = ig.makeIcon(ctx.getString(R.string.chasesite_mapName));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

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
        chase.startChase(true, getChaseDifficulty(), this);
        story.interruptQueueWithSpeech(getChaseStartMessage());
    }

    protected abstract double getChaseDifficulty();
    protected abstract String getChaseStartMessage();
}

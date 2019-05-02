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

/**
 * Created by james on 6/17/2017.
 */

public class DropSite extends MapManager.GameObject {
    public static final String EVENT_DROP_SITE_ACTIVATED = "EVENT_DROP_SITE_ACTIVATED";
    private Marker marker;

    public DropSite(MapManager mm, LatLng pos) {
        super(mm, mm.getContext().getString(R.string.dropsite_spokenName), pos);
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
            ig.setColor(Color.GREEN);
            Bitmap icon = ig.makeIcon(ctx.getString(R.string.dropsite_mapName));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    protected boolean hasApproachActivity() {
        return true;
    }

    @Override
    protected void approach() {
        if (player.isInjured()) {
            player.fixInjury();
            story.addSpeechToQueue(ctx.getString(R.string.dropsite_fixInjuries));
        }
    }

    @Override
    protected boolean isInteractable() {
        return false;
    }

    @Override
    protected void interact() {
        story.processEvent(EVENT_DROP_SITE_ACTIVATED);
    }
}

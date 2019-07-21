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

import static jamesw6811.secrets.gameworld.map.site.DropSite.EVENT_DROP_SITE_ACTIVATED;

/**
 * Created by james on 6/17/2017.
 */

public class HazardSite extends MapManager.GameObject {
    private Marker marker;
    private boolean primed = false;
    private boolean triggered = false;
    public static final String EVENT_HAZARD_SITE_APPROACHED = "EVENT_HAZARD_SITE_APPROACHED";

    public HazardSite(MapManager mm, LatLng pos) {
        super(mm, mm.getContext().getString(R.string.hazardsite_spokenName), pos);
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
            if (!triggered) {
                ig.setColor(Color.RED);
            } else {
                ig.setColor(Color.GREEN);
            }
            Bitmap icon = ig.makeIcon(ctx.getString(R.string.hazardsite_mapName));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    protected String getSpokenName() {
        if (!triggered){
            return ctx.getString(R.string.hazardsite_spokenName);
        } else {
            return ctx.getString(R.string.hazardsite_spokenName_triggered);
        }
    }

    @Override
    protected boolean hasApproachActivity() {
        return !triggered;
    }

    @Override
    protected void approach() {
        if (primed) {
            if (!player.isInjured()) {
                player.injure();
                story.addSpeechToQueue(ctx.getString(R.string.hazardsite_injured));
                triggered = true;
                updateMarker();
            } else {
                story.addSpeechToQueue(ctx.getString(R.string.hazardsite_alreadyinjured));
            }
        } else {
            primed = true;
            story.addSpeechToQueue(ctx.getString(R.string.hazardsite_primed));
        }
        story.processEvent(EVENT_HAZARD_SITE_APPROACHED);
    }

}

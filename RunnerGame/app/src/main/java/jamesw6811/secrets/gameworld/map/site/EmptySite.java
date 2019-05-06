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

public class EmptySite extends MapManager.GameObject {
    private Marker marker;
    private static final String NAME = "an Empty Grassy Field";
    private static final String MAP_NAME = "Grass";

    public EmptySite(MapManager mm, LatLng pos) {
        super(mm, NAME, pos);
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
            Bitmap icon = ig.makeIcon(MAP_NAME);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

}

package jameswrunner.runnergame.gameworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import jameswrunner.runnergame.GameService;

/**
 * Created by james on 6/17/2017.
 */

public class Headquarters extends GameObject {
    private Marker marker;
    private String name;
    private String shortName;
    private LatLng position;

    public Headquarters(GameWorld gw, LatLng pos, String n, String sn) {
        super(gw);
        position = pos;
        name = n;
        shortName = sn;
    }

    protected synchronized void clearMarkerState() {
        marker = null;
    }

    @Override
    protected synchronized void removeMarker() {
        if (marker != null) marker.remove();
    }

    @Override
    protected LatLng getPosition() {
        return position;
    }

    protected synchronized void drawMarker(GameService gs, GoogleMap map) {
        if (marker == null) {
            MarkerOptions mo = new MarkerOptions().position(position).visible(true);
            marker = map.addMarker(mo);
        } else {
            marker.setPosition(position);
        }

        if (marker != null) {
            IconGenerator ig = new IconGenerator(gs);
            ig.setColor(Color.GREEN);
            Bitmap icon = ig.makeIcon(shortName);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }
}

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

public class ControlPoint extends GameObject {
    public Marker marker;
    public CAPTURESTATUS capturestatus;
    public String name;
    public String shortName;
    public LatLng position;

    public ControlPoint(GameWorld gw, LatLng pos, String n, String sn) {
        super(gw);
        position = pos;
        capturestatus = CAPTURESTATUS.NEUTRAL;
        name = n;
        shortName = sn;
    }

    public void updateCaptureStatus(CAPTURESTATUS capstat) {
        capturestatus = capstat;
    }

    private synchronized void updateCaptureStatusIcon(Context context) {
        int color = Color.BLACK;
        switch (capturestatus) {
            case NEUTRAL:
                color = Color.BLUE;
                break;
            case ENEMYTEAM:
                color = Color.RED;
                break;
            case OURTEAM:
                color = Color.GREEN;
                break;
        }

        if (marker != null) {
            IconGenerator ig = new IconGenerator(context);
            ig.setColor(color);
            Bitmap icon = ig.makeIcon(shortName);
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
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
        updateCaptureStatusIcon(gs);
    }

    public enum CAPTURESTATUS {
        //You can initialize enums using enumname(value)
        NEUTRAL(0),
        ENEMYTEAM(1),
        OURTEAM(2);
        private int capturestatus;

        CAPTURESTATUS(int cap) {
            capturestatus = cap;
        }

        public int GetCaptureStatus() {
            return capturestatus;
        }
    }
}

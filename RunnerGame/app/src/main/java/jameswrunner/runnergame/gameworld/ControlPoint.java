package jameswrunner.runnergame.gameworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

    public ControlPoint(GameWorld gw, GamePoint pos, String n, String sn) {
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

    protected synchronized void drawMarker(GameService gs, GoogleMap map, GameBoundaries bounds) {
        if (marker == null) {
            MarkerOptions mo = new MarkerOptions().position(bounds.gamePointtoLatLng(position)).visible(true);
            marker = map.addMarker(mo);
        } else {
            marker.setPosition(bounds.gamePointtoLatLng(position));
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

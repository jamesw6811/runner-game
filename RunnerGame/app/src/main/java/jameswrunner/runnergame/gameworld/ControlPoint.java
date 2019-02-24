package jameswrunner.runnergame.gameworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

/**
 * Created by james on 6/17/2017.
 */

public class ControlPoint {
    public GamePoint position;
    public Marker circle;
    public CAPTURESTATUS capturestatus;
    public String name;
    public String shortName;

    public ControlPoint(GamePoint pos, String n, String sn) {
        position = pos;
        capturestatus = CAPTURESTATUS.NEUTRAL;
        name = n;
        shortName = sn;
    }

    public void updateCaptureStatus(Context context, CAPTURESTATUS capstat) {
        capturestatus = capstat;
        updateCaptureStatusIcon(context);
    }

    private void updateCaptureStatusIcon(Context context) {
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

        if (circle != null) {
            IconGenerator ig = new IconGenerator(context);
            ig.setColor(color);
            Bitmap icon = ig.makeIcon(shortName);
            circle.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    public void updateMarker(Context context, GoogleMap gm, GameBoundaries bounds) {
        if (circle == null) {
            MarkerOptions mo = new MarkerOptions().position(bounds.gamePointtoLatLng(position)).visible(true);
            circle = gm.addMarker(mo);
            updateCaptureStatusIcon(context);
        } else {
            circle.setPosition(bounds.gamePointtoLatLng(position));
        }
    }

    public void destroy() {
        if (circle != null) circle.remove();
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

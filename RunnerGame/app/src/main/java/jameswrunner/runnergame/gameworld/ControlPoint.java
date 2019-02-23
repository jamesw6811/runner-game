package jameswrunner.runnergame.gameworld;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

/**
 * Created by james on 6/17/2017.
 */

public class ControlPoint {
    public GamePoint position;
    public Circle circle;
    public CAPTURESTATUS capturestatus;

    public ControlPoint(GamePoint pos) {
        position = pos;
        capturestatus = CAPTURESTATUS.NEUTRAL;
    }

    public void updateCaptureStatus(CAPTURESTATUS capstat) {
        capturestatus = capstat;
        updateCaptureStatusColor();
    }

    private void updateCaptureStatusColor() {
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
            circle.setFillColor(color);
        }
    }

    public void updateMarker(GoogleMap gm, GameBoundaries bounds) {
        if (circle == null) {
            circle = gm.addCircle(new CircleOptions().center(bounds.gamePointtoLatLng(position))
                    .radius(10f));
            updateCaptureStatusColor();
        } else {
            circle.setCenter(bounds.gamePointtoLatLng(position));
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

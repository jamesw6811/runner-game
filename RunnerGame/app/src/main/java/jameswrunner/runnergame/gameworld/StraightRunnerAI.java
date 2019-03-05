package jameswrunner.runnergame.gameworld;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import jameswrunner.runnergame.GameService;

/**
 * Created by james on 6/17/2017.
 */

public class StraightRunnerAI extends GameObject {
    public float heading;
    public float speed;
    public Circle circle;
    public LatLng position;

    public StraightRunnerAI(GameWorld gw, LatLng gp, float head, float speed) {
        super(gw);
        position = gp;
        heading = head;
        this.speed = speed;
    }

    public void tick(float time) {
        position = SphericalUtil.computeOffset(position, speed, heading);
    }

    @Override
    protected synchronized void drawMarker(GameService gs, GoogleMap gm) {
        if (circle == null) {
            circle = gm.addCircle(new CircleOptions().center(position)
                    .radius(10f)
                    .strokeColor(Color.RED));
        } else {
            circle.setCenter(position);
        }
    }

    @Override
    protected synchronized void clearMarkerState() {
        circle = null;
    }

    @Override
    protected synchronized void removeMarker() {
        if (circle != null) circle.remove();
    }

    @Override
    protected LatLng getPosition() {
        return position;
    }
}

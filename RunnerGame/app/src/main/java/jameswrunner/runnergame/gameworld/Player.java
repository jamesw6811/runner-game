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

public class Player extends GameObject {
    private Circle circle;
    private LatLng lastPosition;
    private double lastDistanceTravelled;
    private double lastHeadingTravelled;
    private int spirits;

    public Player(GameWorld gw, LatLng gp) {
        super(gw, "player", gp);
        spirits = 0;
    }

    @Override
    protected void drawMarker(GameService gs, GoogleMap gm) {
        if (circle == null) {
            circle = gm.addCircle(new CircleOptions().center(getPosition())
                    .radius(10f)
                    .strokeColor(Color.GREEN));
        } else {
            circle.setCenter(getPosition());
        }
    }

    @Override
    protected void clearMarkerState() {
        circle = null;
    }

    @Override
    protected void removeMarker() {
        if (circle != null) circle.remove();
    }

    public void updatePosition(LatLng lastGPS) {
        lastPosition = getPosition();
        if (lastGPS.equals(getPosition())) {
            lastDistanceTravelled = 0;
        } else {
            lastDistanceTravelled = SphericalUtil.computeDistanceBetween(getPosition(), lastGPS);
            lastHeadingTravelled = SphericalUtil.computeHeading(getPosition(), lastGPS);
            setPosition(lastGPS);
        }
    }

    public LatLng getLastPosition() { return  lastPosition; }

    public double getLastDistanceTravelled() {
        return lastDistanceTravelled;
    }

    public double getLastHeadingTravelled() {
        return lastHeadingTravelled;
    }

    public void giveSpirits(int i) {
        if (i < 1) throw new RuntimeException("Giving less than 1 spirit");
        spirits += i;
    }

    public int getSpirits() {
        return spirits;
    }

    public void takeSpirits(int i) {
        if (i < 1) throw new RuntimeException("Taking less than 1 spirit");
        spirits -= i;
    }
}

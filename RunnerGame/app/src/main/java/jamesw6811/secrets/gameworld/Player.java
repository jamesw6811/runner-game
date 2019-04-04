package jamesw6811.secrets.gameworld;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import jamesw6811.secrets.GameService;
import jamesw6811.secrets.R;

/**
 * Created by james on 6/17/2017.
 */

class Player extends GameObject {
    private Circle circle;
    private LatLng lastPosition;
    private double lastDistanceTravelled;
    private double lastHeadingTravelled;
    private int runningResource;
    private int buildingResource;
    private int buildingSubResource;
    private boolean injured = false;

    Player(GameWorld gw, LatLng gp) {
        super(gw, gw.getGameService().getString(R.string.player_spokenName), gp);
        runningResource = 0;
    }

    @Override
    void drawMarker(GameService gs, GoogleMap gm) {
        if (circle == null) {
            circle = gm.addCircle(new CircleOptions().center(getPosition())
                    .radius(10f)
                    .strokeColor(Color.GREEN));
        } else {
            circle.setCenter(getPosition());
        }
    }

    @Override
    void clearMarkerState() {
        circle = null;
    }

    @Override
    void removeMarker() {
        if (circle != null) circle.remove();
    }

    void updatePosition(LatLng lastGPS) {
        lastPosition = getPosition();
        if (lastGPS.equals(getPosition())) {
            lastDistanceTravelled = 0;
        } else {
            lastDistanceTravelled = SphericalUtil.computeDistanceBetween(getPosition(), lastGPS);
            lastHeadingTravelled = SphericalUtil.computeHeading(getPosition(), lastGPS);
            setPosition(lastGPS);
        }
    }

    LatLng getLastPosition() { return  lastPosition; }

    double getLastDistanceTravelled() {
        return lastDistanceTravelled;
    }

    double getLastHeadingTravelled() {
        return lastHeadingTravelled;
    }

    void giveRunningResource(int i) {
        if (i < 1) throw new RuntimeException("Giving less than 1 resource");
        runningResource += i;
    }

    int getRunningResource() {
        return runningResource;
    }

    void takeRunningResource(int i) {
        if (i < 1) throw new RuntimeException("Taking less than 1 resource");
        runningResource -= i;
    }

    void giveBuildingResource(int i) {
        if (i < 1) throw new RuntimeException("Giving less than 1 resource");
        buildingResource += i;
    }

    int getBuildingResource() {
        return buildingResource;
    }

    void takeBuildingResource(int i) {
        if (i < 1) throw new RuntimeException("Taking less than 1 resource");
        buildingResource -= i;
    }

    void giveBuildingSubResource(int i) {
        if (i < 1) throw new RuntimeException("Giving less than 1 resource");
        buildingSubResource += i;
    }

    int getBuildingSubResource() {
        return buildingSubResource;
    }

    void takeBuildingSubResource(int i) {
        if (i < 1) throw new RuntimeException("Taking less than 1 resource");
        buildingSubResource -= i;
    }

    void injure() {
        injured = true;
    }

    boolean isInjured() {
        return injured;
    }

    void fixInjury() {
        injured = false;
    }
}

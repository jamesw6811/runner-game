package jameswrunner.runnergame.gameworld;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import jameswrunner.runnergame.GameService;
import jameswrunner.runnergame.R;

/**
 * Created by james on 6/17/2017.
 */

public class Player extends GameObject {
    private Circle circle;
    private LatLng lastPosition;
    private double lastDistanceTravelled;
    private double lastHeadingTravelled;
    private int runningResource;
    private int buildingResource;
    private int buildingSubResource;
    private boolean injured = false;

    public Player(GameWorld gw, LatLng gp) {
        super(gw, gw.getGameService().getString(R.string.player_spokenName), gp);
        runningResource = 0;
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

    public void giveRunningResource(int i) {
        if (i < 1) throw new RuntimeException("Giving less than 1 resource");
        runningResource += i;
    }

    public int getRunningResource() {
        return runningResource;
    }

    public void takeRunningResource(int i) {
        if (i < 1) throw new RuntimeException("Taking less than 1 resource");
        runningResource -= i;
    }

    public void giveBuildingResource(int i) {
        if (i < 1) throw new RuntimeException("Giving less than 1 resource");
        buildingResource += i;
    }

    public int getBuildingResource() {
        return buildingResource;
    }

    public void takeBuildingResource(int i) {
        if (i < 1) throw new RuntimeException("Taking less than 1 resource");
        buildingResource -= i;
    }

    public void giveBuildingSubResource(int i) {
        if (i < 1) throw new RuntimeException("Giving less than 1 resource");
        buildingSubResource += i;
    }

    public int getBuildingSubResource() {
        return buildingSubResource;
    }

    public void takeBuildingSubResource(int i) {
        if (i < 1) throw new RuntimeException("Taking less than 1 resource");
        buildingSubResource -= i;
    }

    public void injure() {
        injured = true;
    }

    public boolean isInjured() {
        return injured;
    }

    public void fixInjury() {
        injured = false;
    }
}

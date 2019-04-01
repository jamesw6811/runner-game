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

public class StraightRunnerAI extends GameObject {
    private float heading;
    private float speed;
    private Circle circle;

    public StraightRunnerAI(GameWorld gw, LatLng gp, float head, float speed) {
        super(gw, gw.getGameService().getString(R.string.AI_runner_spokenName), gp);
        this.heading = head;
        this.speed = speed;
    }

    public void tick(float time) {
        setPosition(SphericalUtil.computeOffset(getPosition(), speed, heading));
    }

    @Override
    protected synchronized void drawMarker(GameService gs, GoogleMap gm) {
        if (circle == null) {
            circle = gm.addCircle(new CircleOptions().center(getPosition())
                    .radius(10f)
                    .strokeColor(Color.RED));
        } else {
            circle.setCenter(getPosition());
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

}

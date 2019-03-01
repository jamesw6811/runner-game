package jameswrunner.runnergame.gameworld;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

import jameswrunner.runnergame.GameService;

/**
 * Created by james on 6/17/2017.
 */

public class StraightRunnerAI extends GameObject {
    public GameHeading heading;
    public float speed;
    public Circle circle;

    public StraightRunnerAI(GameWorld gw, GamePoint gp, GameHeading head, float speed) {
        super(gw);
        position = gp;
        heading = head;
        this.speed = speed;
    }

    public void tick(float time) {
        position.x += speed * time * Math.cos(heading.getHeadingRadians());
        position.y += speed * time * Math.sin(heading.getHeadingRadians());
    }

    @Override
    protected synchronized void drawMarker(GameService gs, GoogleMap gm, GameBoundaries gb) {
        if (circle == null) {
            circle = gm.addCircle(new CircleOptions().center(gb.gamePointtoLatLng(position))
                    .radius(10f)
                    .strokeColor(Color.RED));
        } else {
            circle.setCenter(gb.gamePointtoLatLng(position));
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

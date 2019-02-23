package jameswrunner.runnergame.gameworld;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

/**
 * Created by james on 6/17/2017.
 */

public class StraightRunnerAI {
    public GamePoint position;
    public GameHeading heading;
    public float speed;
    public Circle circle;

    public StraightRunnerAI(GamePoint gp, GameHeading head, float speed) {
        position = gp;
        heading = head;
        this.speed = speed;
    }

    public void tick(float time) {
        position.x += speed * time * Math.cos(heading.getHeadingRadians());
        position.y += speed * time * Math.sin(heading.getHeadingRadians());
    }

    public void updateMarker(GoogleMap gm, GameBoundaries bounds) {
        if (circle == null) {
            circle = gm.addCircle(new CircleOptions().center(bounds.gamePointtoLatLng(position))
                    .radius(10f)
                    .strokeColor(Color.RED));
        } else {
            circle.setCenter(bounds.gamePointtoLatLng(position));
        }
    }

    public void destroy() {
        if (circle != null) circle.remove();
    }
}

package jameswrunner.runnergame.gameworld;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;

import jameswrunner.runnergame.GameService;

/**
 * Created by james on 6/17/2017.
 */

public class Player extends GameObject{
    public Circle circle;

    public Player(GameWorld gw, GamePoint gp) {
        super(gw);
        position = gp;
    }

    @Override
    protected void drawMarker(GameService gs, GoogleMap gm, GameBoundaries gb) {
        if (circle == null) {
            circle = gm.addCircle(new CircleOptions().center(gb.gamePointtoLatLng(position))
                    .radius(10f)
                    .strokeColor(Color.GREEN));
        } else {
            circle.setCenter(gb.gamePointtoLatLng(position));
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
}

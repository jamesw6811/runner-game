package jameswrunner.runnergame.gameworld;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import jameswrunner.runnergame.GameService;
import jameswrunner.runnergame.RunMapActivity;

public abstract class GameObject {
    private GameWorld gameWorld;

    public GameObject(GameWorld gw) {
        gameWorld = gw;
        gameWorld.addObject(this);
    }

    protected void updateMarker() {
        final GameService gs = getGameService();
        boolean activeUI = gs.passMapUpdate(new RunMapActivity.MapUpdate() {
            @Override
            public void updateMap(GoogleMap map) {
                drawMarker(gs, map);
            }
        });
        if (!activeUI) clearMarkerState();
    }

    protected abstract void drawMarker(GameService gs, GoogleMap gm);

    protected abstract void clearMarkerState();

    protected abstract void removeMarker();

    protected abstract LatLng getPosition();

    public void destroy() {
        getGameService().passMapUpdate(new RunMapActivity.MapUpdate() {
            @Override
            public void updateMap(GoogleMap map) {
                removeMarker();

            }
        });
        gameWorld.removeObject(this);
    }

    private GameService getGameService(){
        return gameWorld.getGameService();
    }

}

package jameswrunner.runnergame.gameworld;

import com.google.android.gms.maps.GoogleMap;

import jameswrunner.runnergame.GameService;
import jameswrunner.runnergame.RunMapActivity;

public abstract class GameObject {
    private GameWorld gameWorld;
    public GameObject(GameWorld gw){
        gameWorld = gw;
        gameWorld.addObject(this);
    }

    public GamePoint position;
    public void updateMarker(final GameService gameService, final GameBoundaries bounds) {
        boolean activeUI = gameService.passMapUpdate(new RunMapActivity.MapUpdate() {
            @Override
            public void updateMap(GoogleMap map) {
                drawMarker(gameService, map, bounds);
            }
        });
        if (!activeUI) clearMarkerState();
    }
    protected abstract void drawMarker(GameService gs, GoogleMap gm, GameBoundaries gb);
    protected abstract void clearMarkerState();

    public void destroy(final GameService gameService) {
        gameService.passMapUpdate(new RunMapActivity.MapUpdate() {
            @Override
            public void updateMap(GoogleMap map) {
        removeMarker();

            }
        });
        gameWorld.removeObject(this);
    }

    protected abstract void removeMarker();
}

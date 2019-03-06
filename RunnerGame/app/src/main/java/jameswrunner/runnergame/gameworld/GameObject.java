package jameswrunner.runnergame.gameworld;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import jameswrunner.runnergame.GameService;
import jameswrunner.runnergame.RunMapActivity;

public abstract class GameObject {
    private GameWorld gameWorld;
    private String spokenName;
    private LatLng position;

    public GameObject(GameWorld gw, String spokenName, LatLng position) {
        gameWorld = gw;
        gameWorld.addObject(this);
        this.spokenName = spokenName;
        this.position = position;
        updateMarker();
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

    protected GameWorld getGameWorld() { return gameWorld; }

    public String getSpokenName() {
        return spokenName;
    }

    public void setSpokenName(String spokenName) {
        this.spokenName = spokenName;
    }

    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
        updateMarker();
    }

    public boolean isUpgradable() {
        return false;
    }

    public void upgrade(Player player) {
        throw new UnsupportedOperationException("This object is not upgradable.");
    }
}

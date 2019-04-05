package jamesw6811.secrets.gameworld;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.GameService;
import jamesw6811.secrets.RunMapActivity;

abstract class GameObject {
    private GameWorld gameWorld;
    private String spokenName;
    private LatLng position;

    GameObject(GameWorld gw, String spokenName, LatLng position) {
        gameWorld = gw;
        gameWorld.addObject(this);
        this.spokenName = spokenName;
        this.position = position;
        updateMarker();
    }

    void updateMarker() {
        final GameService gs = getGameService();
        boolean activeUI = gs.passMapUpdate(new RunMapActivity.MapUpdate() {
            @Override
            public void updateMap(GoogleMap map) {
                drawMarker(gs, map);
            }
        });
        if (!activeUI) clearMarkerState();
    }

    abstract void drawMarker(GameService gs, GoogleMap gm);

    abstract void clearMarkerState();

    abstract void removeMarker();

    void destroy() {
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

    GameWorld getGameWorld() { return gameWorld; }

    String getSpokenName() {
        return spokenName;
    }

    void setSpokenName(String spokenName) {
        this.spokenName = spokenName;
    }

    LatLng getPosition() {
        return position;
    }

    void setPosition(LatLng position) {
        this.position = position;
        updateMarker();
    }

    boolean isUpgradable() {
        return false;
    }

    void upgrade() {
        throw new UnsupportedOperationException("This object is not upgradable.");
    }

    void tickTime(float timeDelta) {

    }

    boolean isInteractable() {
        return false;
    }

    void interact() {
        throw new UnsupportedOperationException("This object is not interactable.");
    }

    boolean hasApproachActivity() {
        return false;
    }

    void approach() {
        throw new UnsupportedOperationException("This object does not have an approach activity.");
    }
}

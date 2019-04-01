package jamesw6811.secrets.gameworld;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.GameService;
import jamesw6811.secrets.RunMapActivity;

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

    public void upgrade() {
        throw new UnsupportedOperationException("This object is not upgradable.");
    }

    public void tickTime(float timeDelta) {

    }

    public boolean isInteractable() {
        return false;
    }

    public void interact() {
        throw new UnsupportedOperationException("This object is not interactable.");
    }

    public boolean hasApproachActivity() {
        return false;
    }

    public void approach() {
        throw new UnsupportedOperationException("This object does not have an approach activity.");
    }
}

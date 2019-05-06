package jamesw6811.secrets.gameworld.map.site;

import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.R;
import jamesw6811.secrets.gameworld.chase.ChaseOriginator;
import jamesw6811.secrets.gameworld.map.MapManager;

public abstract class ChaserCaptureSite extends CaptureSite implements ChaseOriginator {
    public static final String EVENT_CHASE_CAPTURE_STARTED = "EVENT_CHASE_CAPTURE_STARTED";

    public ChaserCaptureSite(MapManager mm, String spokenName, LatLng latLng) {
        super(mm, spokenName, latLng);
    }

    protected abstract double getChaseDifficulty();

    @Override
    protected void setCaptured(boolean b) {
        super.setCaptured(b);
        chase.startChase(true, getChaseDifficulty(), this);
    }

}

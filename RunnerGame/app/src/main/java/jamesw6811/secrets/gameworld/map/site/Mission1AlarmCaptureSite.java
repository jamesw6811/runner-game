package jamesw6811.secrets.gameworld.map.site;

import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.gameworld.map.MapManager;

public class Mission1AlarmCaptureSite extends AlarmCaptureSite {
    private static final int CAPTURE_CRED = 10;

    public Mission1AlarmCaptureSite(MapManager mm, LatLng latLng) {
        super(mm, "a Redwood Rogue", latLng);
    }

    @Override
    float getAlarmCaptureSiteTime() {
        return 60*60*5;
    }

    @Override
    protected CharSequence getCaptureSiteMapName() {
        return "RR";
    }

    @Override
    protected CharSequence getCaptureSiteSpokenNameBeforeCapture() {
        return "a Redwood Rogue";
    }

    @Override
    protected CharSequence getCaptureSiteSpokenNameAfterCapture() {
        return "the Redwood Rogue";
    }

    @Override
    protected CharSequence getCaptureSiteCaptureSpeech() {
        return "You turned the Redwood Rogue to your side.";
    }

    @Override
    protected CharSequence getCaptureSiteNotEnoughResourcesSpeech() {
        return "You need " + CAPTURE_CRED + " Vine Cred to turn the Redwood Rogue to your side.";
    }

    @Override
    protected int getCaptureSiteCaptureCost() {
        return CAPTURE_CRED;
    }
}

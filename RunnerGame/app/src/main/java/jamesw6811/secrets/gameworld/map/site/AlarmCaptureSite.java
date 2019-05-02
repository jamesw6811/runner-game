package jamesw6811.secrets.gameworld.map.site;

import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.gameworld.map.MapManager;

public abstract class AlarmCaptureSite extends CaptureSite {
    public static final String EVENT_ALARM_OUT = "EVENT_ALARM_OUT";
    private float timeAlarmOut;

    public AlarmCaptureSite(MapManager mm, String spokenName, LatLng latLng) {
        super(mm, spokenName, latLng);
    }

    abstract float getAlarmCaptureSiteTime();

    @Override
    protected void setCaptured(boolean b) {
        super.setCaptured(b);
        timeAlarmOut = getAlarmCaptureSiteTime();
    }

    @Override
    protected void tickTime(float timeDelta) {
        super.tickTime(timeDelta);
        timeAlarmOut -= timeDelta;
        if (timeAlarmOut <= 0){
            triggerAlarmOut();
        }
    }

    protected void triggerAlarmOut(){
        story.processEvent(EVENT_ALARM_OUT);
    }
}

package jamesw6811.secrets.gameworld.map.site;

import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.gameworld.map.MapManager;

public abstract class AlarmCaptureSite extends CaptureSite {
    public static final String EVENT_ALARM_OUT = "EVENT_ALARM_OUT";
    private float timeAlarmOut;

    public AlarmCaptureSite(MapManager mm, String spokenName, LatLng latLng) {
        super(mm, spokenName, latLng);
    }

    protected abstract float getAlarmCaptureSiteTime();
    protected abstract void doAlarmAnnouncement(int minutesRemaining);

    @Override
    protected void setCaptured(boolean b) {
        super.setCaptured(b);
        timeAlarmOut = getAlarmCaptureSiteTime();
    }

    @Override
    protected void tickTime(float timeDelta) {
        super.tickTime(timeDelta);
        if (timeAlarmOut > 0) {
            int minutesBefore = (int) Math.floor(timeAlarmOut / 60.0);
            timeAlarmOut -= timeDelta;
            int minutesAfter = (int) Math.floor(timeAlarmOut / 60.0);
            if (timeAlarmOut <= 0) {
                triggerAlarmOut();
            } else if (minutesAfter != minutesBefore){
                doAlarmAnnouncement(minutesBefore);
            }
        }
    }

    protected void triggerAlarmOut(){
        story.processEvent(EVENT_ALARM_OUT);
    }
}

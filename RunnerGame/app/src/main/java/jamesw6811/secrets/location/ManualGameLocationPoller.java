package jamesw6811.secrets.location;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

public class ManualGameLocationPoller extends GameLocationPoller {
    boolean polling = false;
    NewLocationListener listener;

    public ManualGameLocationPoller(Context ctx, NewLocationListener newLocationListener) {
        listener = newLocationListener;
    }

    @Override
    public void startPolling() {
        polling = true;
    }

    @Override
    public void stopPolling() {
        polling = false;
    }

    public void manualSetLocation(LatLng location){
        listener.onNewLocation(MapUtilities.LatLngToLocation(location));
    }
}

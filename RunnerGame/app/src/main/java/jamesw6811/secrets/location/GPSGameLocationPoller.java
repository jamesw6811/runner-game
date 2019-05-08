package jamesw6811.secrets.location;

import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class GPSGameLocationPoller extends GameLocationPoller{
    public static final float MINIMUM_ACCURACY_REQUIRED = 10f;
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 3*1000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 2*1000;
    public static final float SMALLEST_DISPLACEMENT = 10f;
    private static final String LOGTAG = GPSGameLocationPoller.class.getName();

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private LocationRequest mLocationRequest;
    private NewLocationListener newLocationListener;
    private Location lastLocation;


    public GPSGameLocationPoller(Context ctx, NewLocationListener newLocationListener) {
        this.newLocationListener = newLocationListener;
        // Start location listening
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };
        createLocationRequest();
    }

    public void startPolling() {
        getLastLocation();
        requestLocationUpdates();
    }

    public void stopPolling() {
        removeLocationUpdates();
    }

    private void requestLocationUpdates() {
        Log.i(LOGTAG, "Requesting location updates");
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e(LOGTAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    private void removeLocationUpdates() {
        Log.i(LOGTAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        } catch (SecurityException unlikely) {
            Log.e(LOGTAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful() || task.getResult() == null) {
                            Log.w(LOGTAG, "Failed to get location.");
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(LOGTAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        if (location.getAccuracy() < MINIMUM_ACCURACY_REQUIRED) {
            if (lastLocation == null || (lastLocation.distanceTo(location) >= SMALLEST_DISPLACEMENT)) {
                lastLocation = location;
                newLocationListener.onNewLocation(location);
            }
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

}

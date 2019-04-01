package jamesw6811.secrets;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;
import jamesw6811.secrets.controls.RunningMediaController;
import jamesw6811.secrets.gameworld.GameWorld;
import jamesw6811.secrets.sound.TextToSpeechRunner;
import jamesw6811.secrets.sound.ToneRunner;

public class GameService extends Service {
    public static final float MINIMUM_ACCURACY_REQUIRED = 25f;
    public static GameService runningInstance = null;

    private static final String LOGTAG = GameService.class.getName();
    private static final String PACKAGE_NAME =
            "app.jamesw.jameswrunner.runnergame." + LOGTAG;
    private static final String CHANNEL_ID = "gameservice_notifications";
    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 500;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int NOTIFICATION_ID = 1;
    private final IBinder mBinder = new LocalBinder();
    private NotificationManager mNotificationManager;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private Location mLocation;
    private RunMapActivity mActivity;
    private TextToSpeechRunner ttser;
    private ToneRunner toner;
    private RunningMediaController controller;
    private GameWorld gw;
    private boolean uiBound;
    private double pace = -1;

    public GameService() {
    }

    public static GameService getRunningInstance(){
        return runningInstance;
    }


    @Override
    public void onCreate() {
        runningInstance = this;
        uiBound = false;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread(LOGTAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        ttser = new TextToSpeechRunner(this);
        toner = new ToneRunner();
        controller = new RunningMediaController(this);
        requestLocationUpdates();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGTAG, "Service started");

        MediaButtonReceiver.handleIntent(controller.getMediaSession(), intent);
        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION,
                false);

        // We got here because the user decided to remove location updates from the notification.
        if (startedFromNotification) {
            removeLocationUpdates();
            stopSelf();
        } else {
            startForeground(NOTIFICATION_ID, getNotification());
        }

        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) comes to the foreground
        // and binds with this service. The service should cease to be a foreground service
        // when that happens.
        Log.i(LOGTAG, "in onBind()");

        // Set pace setting from extra
        double paceIntent = intent.getDoubleExtra(RunMapActivity.EXTRA_PACE, -1);
        if (paceIntent>0) pace = paceIntent;

        onAllBind();
        return mBinder;
    }

    public void bindUI(RunMapActivity act) {
        this.mActivity = act;
        uiBound = true;
        if (gw != null) gw.refreshUIState();
    }

    @Override
    public void onRebind(Intent intent) {
        // Called when a client (MainActivity in case of this sample) returns to the foreground
        // and binds once again with this service. The service should cease to be a foreground
        // service when that happens.
        Log.i(LOGTAG, "in onRebind()");

        // Set pace setting from extra
        double paceIntent = intent.getDoubleExtra(RunMapActivity.EXTRA_PACE, -1);
        if (paceIntent>0) pace = paceIntent;

        onAllBind();
        super.onRebind(intent);
    }

    private void onAllBind() {
        controller.refreshPriority();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(LOGTAG, "Last client unbound from service");
        uiBound = false;
        if (gw != null) gw.clearUIState();
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    @Override
    public void onDestroy() {
        runningInstance = null;
        if (controller != null) {
            controller.release();
        }
        if (gw != null) {
            gw.stopRunning();
        }
        if (ttser != null) {
            ttser.stopSpeech();
            ttser.release();
        }
        if (toner != null) {
            toner.stopTone();
            toner.release();
        }
        mServiceHandler.removeCallbacksAndMessages(null);
    }

    /**
     * Makes a request for location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void requestLocationUpdates() {
        Log.i(LOGTAG, "Requesting location updates");
        startService(new Intent(getApplicationContext(), GameService.class));
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e(LOGTAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Removes location updates. Note that in this sample we merely log the
     * {@link SecurityException}.
     */
    public void removeLocationUpdates() {
        Log.i(LOGTAG, "Removing location updates");
        try {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            stopSelf();
        } catch (SecurityException unlikely) {
            Log.e(LOGTAG, "Lost location permission. Could not remove updates. " + unlikely);
        }
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        Intent intent = new Intent(this, GameService.class);

        CharSequence text = getString(R.string.foreground_service_notification_description);
        CharSequence title = getString(R.string.foreground_service_notification_title);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent to launch activity with no back stack
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, RunMapActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(activityPendingIntent)
                .setContentText(text)
                .setContentTitle(title)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        return builder.build();
    }

    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                mLocation = task.getResult();
                            } else {
                                Log.w(LOGTAG, "Failed to get location.");
                            }
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(LOGTAG, "Lost location permission." + unlikely);
        }
    }

    private void onNewLocation(Location location) {
        mLocation = location;

        if (ttser.isInitialized() && location.getAccuracy() < MINIMUM_ACCURACY_REQUIRED) {
            if (gw == null && uiBound) {
                gw = new GameWorld(location, pace, this);
                gw.initializeAndStartRunning();
            } else if (gw != null) {
                gw.updateGPS(location);
            }
        }
    }


    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public boolean passMapUpdate(RunMapActivity.MapUpdate mu) {
        if (mActivity != null && uiBound) {
            mActivity.processMapUpdate(mu);
            return true;
        }
        return false;
    }

    public TextToSpeechRunner getTTSRunner() {
        return ttser;
    }

    public ToneRunner getToneRunner() {
        return toner;
    }

    public RunningMediaController getController() {
        return controller;
    }

    public void finish() {
        if (mActivity != null) {
            mActivity.finish();
        }
        this.stopSelf();
    }

    public void finishAndDebrief() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int num_medals = sharedPref.getInt(getString(R.string.magnolia_medals_key), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.magnolia_medals_key), num_medals+1);
        editor.apply();

        Intent intent = new Intent(this, DebriefingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        GameService getService() {
            return GameService.this;
        }
    }

}

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
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;
import jamesw6811.secrets.controls.RunningMediaController;
import jamesw6811.secrets.gameworld.GameWorld;
import jamesw6811.secrets.location.GameLocationPoller;
import jamesw6811.secrets.sound.TextToSpeechRunner;
import jamesw6811.secrets.sound.ToneRunner;

public class GameService extends Service {
    public static GameService runningInstance = null;

    private static final String LOGTAG = GameService.class.getName();
    private static final String CHANNEL_ID = "gameservice_notifications";
    private static final int NOTIFICATION_ID = 1;

    private final IBinder mBinder = new LocalBinder();
    private Handler mServiceHandler;
    private RunMapActivity mActivity;
    private GameLocationPoller gameLocationPoller;
    private TextToSpeechRunner ttser;
    private ToneRunner toner;
    private RunningMediaController controller;
    private GameWorld gw;
    private boolean uiBound;
    private double pace = -1;

    public static GameService getRunningInstance(){
        return runningInstance;
    }

    @Override
    public void onCreate() {
        // Set running state
        runningInstance = this;
        uiBound = false;

        // Initialize handler
        HandlerThread handlerThread = new HandlerThread(LOGTAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());

        setupNotificationsChannel();

        // Setup services
        ttser = new TextToSpeechRunner(this);
        toner = new ToneRunner();
        controller = new RunningMediaController(this);
        gameLocationPoller = new GameLocationPoller(this, GameService.this::startGameOrUpdateLocation);
        gameLocationPoller.startPolling();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaButtonReceiver.handleIntent(controller.getMediaSession(), intent);
        startForeground(NOTIFICATION_ID, getNotification());
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public IBinder onBind(Intent intent) {
        setPaceFromIntent(intent);
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
        setPaceFromIntent(intent);
        onAllBind();
        super.onRebind(intent);
    }

    private void setPaceFromIntent(Intent intent){
        // Set pace setting from extra
        double paceIntent = intent.getDoubleExtra(RunMapActivity.EXTRA_PACE, -1);
        if (paceIntent>0) pace = paceIntent;
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
        gameLocationPoller.stopPolling();
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

    private void setupNotificationsChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Android O requires a Notification Channel.
            CharSequence name = getString(R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {
        CharSequence text = getString(R.string.foreground_service_notification_description);
        CharSequence title = getString(R.string.foreground_service_notification_title);

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

    public void startGameOrUpdateLocation(Location location) {
        if(ttser.isInitialized()) {
            if (gw == null && uiBound) {
                gw = new GameWorld(location, pace, this);
                gw.initializeAndStartRunning();
            } else if (gw != null) {
                gw.updateGPS(location);
            }
        }
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

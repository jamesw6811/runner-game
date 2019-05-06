package jamesw6811.secrets;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

import java.security.InvalidParameterException;

import androidx.core.app.NotificationCompat;
import androidx.media.session.MediaButtonReceiver;
import jamesw6811.secrets.controls.RunningMediaController;
import jamesw6811.secrets.gameworld.GameWorld;
import jamesw6811.secrets.gameworld.map.GameUIUpdateProcessor;
import jamesw6811.secrets.gameworld.story.GameResult;
import jamesw6811.secrets.gameworld.story.StoryMission;
import jamesw6811.secrets.location.GPSGameLocationPoller;
import jamesw6811.secrets.location.GameLocationPoller;
import jamesw6811.secrets.sound.TextToSpeechRunner;
import jamesw6811.secrets.sound.ToneRunner;

public class GameService extends Service implements GameUIUpdateProcessor {
    private static final String LOGTAG = GameService.class.getName();
    private static final String CHANNEL_ID = "gameservice_notifications";
    private static final int NOTIFICATION_ID = 1;
    public static GameService runningInstance = null;
    private final IBinder mBinder = new LocalBinder();
    private Handler mServiceHandler;
    private RunMapActivity mActivity;
    private GameLocationPoller GPSGameLocationPoller;
    private TextToSpeechRunner ttser;
    private ToneRunner toner;
    private RunningMediaController controller;
    private boolean servicesSet = false;
    public boolean started = false;
    private GameWorld gw;
    private boolean uiBound;
    private double pace = -1;
    private int missionNumber = 0;
    public static GameService getRunningInstance() {
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
        setupServices();

        // Persist service
        startService(new Intent(getApplicationContext(), GameService.class));
    }

    public void setServices(TextToSpeechRunner ttser, ToneRunner toner, RunningMediaController controller, GPSGameLocationPoller GPSGameLocationPoller){
        this.ttser = ttser;
        this.toner = toner;
        this.controller = controller;
        this.GPSGameLocationPoller = GPSGameLocationPoller;
        servicesSet = true;
    }

    private void setupServices() { // Separate method so easy to extend and test
        if (servicesSet) return;
        ttser = new TextToSpeechRunner(this);
        toner = new ToneRunner();
        controller = new RunningMediaController(this);
        GPSGameLocationPoller = new GPSGameLocationPoller(this, GameService.this::startGameOrUpdateLocation);
        servicesSet = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (controller != null) MediaButtonReceiver.handleIntent(controller.getMediaSession(), intent);
        startForeground(NOTIFICATION_ID, getNotification());
        started = true;
        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public IBinder onBind(Intent intent) {
        setPaceFromIntent(intent);
        missionNumber = intent.getIntExtra(StoryMission.EXTRA_MISSION, 0);
        if (missionNumber == 0) throw new InvalidParameterException("No mission specified in extra.");
        onAllBind();
        if (GPSGameLocationPoller != null) GPSGameLocationPoller.startPolling();
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

    private void setPaceFromIntent(Intent intent) {
        // Set pace setting from extra
        double paceIntent = intent.getDoubleExtra(RunMapActivity.EXTRA_PACE, -1);
        if (paceIntent > 0) pace = paceIntent;
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
        GPSGameLocationPoller.stopPolling();
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

    public void startGameOrUpdateLocation(Location location) {
        if (ttser.isInitialized()) {
            if (gw == null && uiBound) {
                gw = new GameWorld(location, pace, missionNumber, this, this, ttser, toner, controller);
                gw.initializeAndStartRunning();
                mActivity.gameStarted();
            } else if (gw != null) {
                gw.updateGPS(location);
            }
        }
    }

    public void setGameLocationPoller(GameLocationPoller gameLocationPoller){
        this.GPSGameLocationPoller.stopPolling();
        this.GPSGameLocationPoller = gameLocationPoller;
        this.GPSGameLocationPoller.startPolling();
    }

    @Override
    public boolean processMapUpdate(RunMapActivity.MapUpdate mu) {
        if (mActivity != null && uiBound) {
            mActivity.processMapUpdate(mu);
            return true;
        }
        return false;
    }

    @Override
    public void finishAndDebrief(GameResult gr) {
        if (gr.success) StoryMission.getMission(missionNumber).doRewardsAndUnlocks(this);

        Intent intent = new Intent(this, DebriefingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(StoryMission.EXTRA_MISSION, missionNumber);
        intent.putExtra(RunStatsActivity.EXTRA_DURATION, gr.duration);
        intent.putExtra(RunStatsActivity.EXTRA_DISTANCE, gr.distance);
        intent.putExtra(DebriefingActivity.EXTRA_SUCCESS, gr.success);
        startActivity(intent);

        finishServiceAndActivity();
    }

    private void finishServiceAndActivity(){
        mActivity.finish();
        finish();
    }

    public void abortClicked() {
        if (gw != null) gw.abort();
        else finishAndDebrief(new GameResult(0,0,false));
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

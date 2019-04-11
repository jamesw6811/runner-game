package jamesw6811.secrets.controls;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import jamesw6811.secrets.GameService;
import jamesw6811.secrets.R;

public class RunningMediaController {
    private static final String LOGTAG = RunningMediaController.class.getName();
    private MediaSessionCompat mediaSession;
    private MediaPlayer mSilencePlayer;
    private Context ctx;
    private boolean playClicked = false;
    private boolean skipToNextClicked = false;

    public RunningMediaController(Context ctx) {
        this.ctx = ctx;
        initialize();
    }

    private void initialize() {
        mediaSession = new MediaSessionCompat(ctx, LOGTAG);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setPlaybackState(new PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_PLAYING, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 1)
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE | PlaybackStateCompat.ACTION_PAUSE | PlaybackStateCompat.ACTION_SKIP_TO_NEXT)
                .build());
        PendingIntent servicePendingIntent = PendingIntent.getService(ctx, 0, new Intent(ctx, GameService.class), 0);
        mediaSession.setMediaButtonReceiver(servicePendingIntent);
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                return super.onMediaButtonEvent(mediaButtonIntent);
            }

            @Override
            public void onPause() {
                onSingleClick();
            }

            @Override
            public void onPlay() {
                onSingleClick();
            }

            @Override
            public void onStop() {
                onSingleClick();
            }

            @Override
            public void onSkipToNext() {
                onDoubleClick();
            }
        });
        mediaSession.setActive(true);
        mSilencePlayer = MediaPlayer.create(ctx, R.raw.silent);
        mSilencePlayer.setLooping(true);
        mSilencePlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
            }
        });
        mSilencePlayer.start();
    }

    public void release() {
        if (mediaSession != null) mediaSession.release();
        if (mSilencePlayer != null) {
            mSilencePlayer.stop();
            mSilencePlayer.release();
        }
    }

    private void onSingleClick() {
        playClicked = true;
    }

    private void onDoubleClick() {
        skipToNextClicked = true;
    }

    public ClickState getClickState(boolean reset) {
        ClickState cs = new ClickState();
        cs.playClicked = playClicked;
        cs.skipToNextClicked = skipToNextClicked;
        if (reset) resetClickState();
        return cs;
    }

    public void resetClickState() {
        playClicked = false;
        skipToNextClicked = false;
    }

    public MediaSessionCompat getMediaSession() {
        return mediaSession;
    }

    /*
    Refresh priority over other media sources to use media controls for game controls.
     */
    public void refreshPriority() {
        release();
        initialize();
    }

    public class ClickState {
        public boolean playClicked = false;
        public boolean skipToNextClicked = false;
    }
}

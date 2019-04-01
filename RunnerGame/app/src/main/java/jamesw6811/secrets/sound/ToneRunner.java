package jamesw6811.secrets.sound;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.os.HandlerThread;

public class ToneRunner {

    public static final int MIN_PERIOD = 200;
    private static final String LOGTAG = ToneRunner.class.getName();
    private static final int TONE_GENERATOR_VOLUME = 100;
    private static final int TONE_LENGTH = MIN_PERIOD / 2;
    private static final int TONE_TO_PLAY = android.media.ToneGenerator.TONE_CDMA_DIAL_TONE_LITE;
    private Handler mToneHandler;
    private ToneGenerator tonegen;
    private int mPeriod;
    private boolean looping;

    public ToneRunner() {
        HandlerThread toneHandlerThread = new HandlerThread(LOGTAG);
        toneHandlerThread.start();
        mToneHandler = new Handler(toneHandlerThread.getLooper());
        tonegen = new android.media.ToneGenerator(AudioManager.STREAM_MUSIC, TONE_GENERATOR_VOLUME);
        looping = false;
    }

    public void playTone(int period) {
        mPeriod = Math.max(MIN_PERIOD, period);
        if (!looping) {
            looping = true;
            toneLoop();
        }
    }

    private void toneLoop() {
        if (looping) {
            final int period = mPeriod;
            mToneHandler.post(new Runnable() {
                @Override
                public void run() {
                    tonegen.startTone(TONE_TO_PLAY, TONE_LENGTH);
                    try {
                        Thread.sleep(period);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ToneRunner.this.toneLoop();
                }
            });
        }
    }

    public void stopTone() {
        tonegen.stopTone();
        looping = false;
    }

    public void release() {
        stopTone();
    }
}

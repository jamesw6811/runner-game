package jamesw6811.secrets.sound;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import jamesw6811.secrets.R;

public class TextToSpeechRunner {
    private static final String LOGTAG = TextToSpeechRunner.class.getName();
    private boolean initialized = false;
    private TextToSpeech tts;
    private AudioManager mAudioManager;
    private TTSRunnerListener listener;
    public static final String CRED_EARCON = "[cred]";
    private Runnable onDoneSpeaking;

    public TextToSpeechRunner(Context ctx) {
        // initialization of the audio attributes and focus request
        mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
        listener = new TTSRunnerListener();
        tts = new TextToSpeech(ctx, listener);
        tts.setOnUtteranceProgressListener(listener);
    }

    public void addSpeech(CharSequence toSay) {
        tts.speak(toSay, TextToSpeech.QUEUE_ADD, null, LOGTAG);
    }

    /*
    Interrupt existing speech to say 'toSay'
     */
    public void interruptSpeech(CharSequence toSay) {
        stopSpeech();
        addSpeech(toSay);
    }

    public void stopSpeech() {
        tts.stop();
    }

    public boolean isStillSpeaking() {
        return tts.isSpeaking();
    }

    public void release() {
        stopSpeech();
        tts.shutdown();
        mAudioManager.abandonAudioFocus(listener);
    }

    public void setOnDoneSpeaking(Runnable runnable) {
        onDoneSpeaking = runnable;
    }

    private class TTSRunnerListener extends UtteranceProgressListener implements AudioManager.OnAudioFocusChangeListener, TextToSpeech.OnInitListener {
        final TextToSpeechRunner ttsr;

        TTSRunnerListener() {
            this.ttsr = TextToSpeechRunner.this;
        }

        @Override
        public void onStart(String utteranceId) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build();
                AudioFocusRequest mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                        .setAudioAttributes(mPlaybackAttributes)
                        .setAcceptsDelayedFocusGain(true)
                        .setWillPauseWhenDucked(true)
                        .setOnAudioFocusChangeListener(this)
                        .build();
                mAudioManager.requestAudioFocus(mFocusRequest);
            } else {
                mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            }
        }

        @Override
        public void onDone(String utteranceId) {
            mAudioManager.abandonAudioFocus(this);
            if (!isStillSpeaking()) {
                if (onDoneSpeaking != null) onDoneSpeaking.run();
                onDoneSpeaking = null;
            }
        }

        @Override
        public void onError(String utteranceId) {

        }

        @Override
        public void onInit(int status) {
            Log.d(LOGTAG, "TextToSpeech initialized with status:" + status);
            if (status != TextToSpeech.ERROR) {
                Log.d(LOGTAG, "TextToSpeech no error");
            }
            tts.addSpeech(CRED_EARCON, "jamesw6811.secrets", R.raw.cred);
            initialized = true;
        }

        @Override
        public void onAudioFocusChange(int focusChange) {
        }

    }

    public boolean isInitialized(){
        return initialized;
    }
}

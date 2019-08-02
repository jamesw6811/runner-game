package jamesw6811.secrets;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import java.util.prefs.Preferences;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SplashActivity extends Activity {

        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_splash);

            scheduleSplashScreen();
        }

        private void scheduleSplashScreen() {
            long splashScreenDuration = getSplashScreenDuration();
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
            }, splashScreenDuration);
        }

        private long getSplashScreenDuration(){
            SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
            String prefKeyFirstLaunch = "pref_first_launch";

            if (sp.getBoolean(prefKeyFirstLaunch, true)){
                // If this is the first launch, make it slow (> 3 seconds) and set flag to false
                sp.edit().putBoolean(prefKeyFirstLaunch, false).apply();
                return 5000;
            } else {
                return 1000;
            }
        }


}

package jamesw6811.secrets;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import jamesw6811.secrets.controls.RunningMediaController;

public class BriefingActivity extends Activity {
    RunningMediaController runningMediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefing);
        runningMediaController = new RunningMediaController(this){
            @Override
            protected void onSingleClick() {
                super.onSingleClick();
                this.release();
                BriefingActivity.this.startGameActivity();
            }

            @Override
            protected void onDoubleClick() {
                super.onDoubleClick();
            }
        };
    }

    private void startGameActivity() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int pacePref = sharedPref.getInt(getString(R.string.saved_pace_key), -1);
        Intent intent = new Intent(this, RunMapActivity.class);
        intent.putExtra(RunMapActivity.EXTRA_PACE, (double) pacePref);
        startActivity(intent);
        finish();
    }
}

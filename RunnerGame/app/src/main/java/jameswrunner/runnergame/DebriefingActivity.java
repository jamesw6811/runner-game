package jameswrunner.runnergame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class DebriefingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debriefing);
        Button startButton = findViewById(R.id.debrief_button_done);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishDebrief();
            }
        });
    }

    private void finishDebrief() {
        // TODO: Ask for feedback.
        finish();
    }
}

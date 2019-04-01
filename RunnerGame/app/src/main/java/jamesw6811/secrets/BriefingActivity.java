package jamesw6811.secrets;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class BriefingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefing);
        Button startButton = findViewById(R.id.brief_start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity();
            }
        });
    }

    private void startGameActivity() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int pacePref = sharedPref.getInt(getString(R.string.saved_pace_key), -1);
        Intent intent = new Intent(this, RunMapActivity.class);
        intent.putExtra(RunMapActivity.EXTRA_PACE, (double)pacePref);
        startActivity(intent);
        finish();
    }
}

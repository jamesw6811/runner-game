package jamesw6811.secrets;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RunStatsActivity extends Activity {
    public static final String EXTRA_DISTANCE = "jamesw6811.secrets.RunStatsActivity.EXTRA_DISTANCE";
    public static final String EXTRA_DURATION = "jamesw6811.secrets.RunStatsActivity.EXTRA_DURATION";
    private static final float MILES_PER_METER = 0.000621371f;

    private TextView statsContents;

    private float distance;
    private float duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_stats);
        distance = getIntent().getFloatExtra(EXTRA_DISTANCE, -1);
        duration = getIntent().getFloatExtra(EXTRA_DURATION, -1);
        statsContents = findViewById(R.id.run_stats_contents);
        findViewById(R.id.button_back).setOnClickListener(v -> onBackPressed());
        updateStatsText();
    }

    private void updateStatsText(){
        float distance_miles = MILES_PER_METER*distance;
        int duration_minutes = (int)(duration / 60);
        int duration_seconds = (int)(duration - duration_minutes*60);
        float pace = duration/distance_miles;
        int pace_minutes = (int)(pace/60);
        int pace_seconds = (int)(pace - pace_minutes*60);
        statsContents.setText(getString(R.string.run_stats_contents, duration_minutes, duration_seconds, pace_minutes, pace_seconds, distance_miles));
    }
}

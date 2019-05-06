package jamesw6811.secrets;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.security.InvalidParameterException;

import jamesw6811.secrets.controls.RunningMediaController;
import jamesw6811.secrets.gameworld.story.StoryMission;

public class BriefingActivity extends Activity {
    RunningMediaController runningMediaController;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefing);
        final int mission = getIntent().getIntExtra(StoryMission.EXTRA_MISSION, 0);
        if (mission == 0) throw new InvalidParameterException("No mission specified.");
        StoryMission storyMission = StoryMission.getMission(mission);
        ((TextView)findViewById(R.id.contents_story_briefing)).setText(storyMission.getBriefing());
        ((TextView)findViewById(R.id.title_briefing)).setText(storyMission.getName());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        runningMediaController = new RunningMediaController(this){
            @Override
            protected void onSingleClick() {
                super.onSingleClick();
                this.release();
                BriefingActivity.this.startGameActivity(mission);
            }

            @Override
            protected void onDoubleClick() {
                super.onDoubleClick();
            }
        };
    }

    private void startGameActivity(int mission) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Integer.toString(mission));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, StoryMission.getMission(mission).getName());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "mission");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int pacePref = sharedPref.getInt(getString(R.string.saved_pace_key), -1);
        Intent intent = new Intent(this, RunMapActivity.class);
        intent.putExtras(getIntent());
        intent.putExtra(RunMapActivity.EXTRA_PACE, (double) pacePref);
        startActivity(intent);
        finish();
    }
}

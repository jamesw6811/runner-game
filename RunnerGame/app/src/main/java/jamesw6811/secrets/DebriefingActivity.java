package jamesw6811.secrets;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.util.Objects;

import jamesw6811.secrets.gameworld.story.StoryMission;

public class DebriefingActivity extends Activity {
    public static final String EXTRA_SUCCESS = "jamesw6811.secrets.DebriefingActivity.EXTRA_SUCCESS";
    private static final String EMAILFEEDBACK_SUBJECT = "Super Helpful Sappy Secrets Feedbark";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debriefing);
        Button startButton = findViewById(R.id.debrief_button_done);
        startButton.setOnClickListener(v -> finishDebrief());
        findViewById(R.id.debrief_button_stats).setOnClickListener(v -> startStatsActivity());

        int mission = getIntent().getIntExtra(StoryMission.EXTRA_MISSION, 0);
        if (mission == 0) throw new InvalidParameterException("No mission specified.");
        StoryMission storyMission = StoryMission.getMission(mission);
        ((TextView)findViewById(R.id.title_briefing)).setText(storyMission.getName());

        boolean success = getIntent().getBooleanExtra(EXTRA_SUCCESS, false);
        if (success){
            ((TextView)findViewById(R.id.contents_story_briefing)).setText(storyMission.getSuccessDebriefing());
        } else {
            ((TextView)findViewById(R.id.contents_story_briefing)).setText(storyMission.getFailureDebriefing());
        }
    }

    private void startStatsActivity(){
        Intent intent = new Intent(getApplicationContext(), RunStatsActivity.class);
        intent.putExtras(Objects.requireNonNull(getIntent().getExtras()));
        startActivity(intent);
    }

    private void finishDebrief() {
        final String[] EMAILSFEEDBACK = {getString(R.string.feedback_email)};
        composeEmail(EMAILSFEEDBACK, EMAILFEEDBACK_SUBJECT, getString(R.string.feedback_email_content));
        finish();
    }

    private void composeEmail(String[] addresses, String subject, String text) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

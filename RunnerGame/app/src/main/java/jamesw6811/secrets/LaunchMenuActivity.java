package jamesw6811.secrets;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import jamesw6811.secrets.gameworld.story.StoryMission;

public class LaunchMenuActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    public static final String EXTRA_SEEN_INTRO_ALREADY = "LaunchMenuActivity.EXTRA_SEEN_INTRO_ALREADY";
    private static final int SPEED_OFFSET = 4; // minutes per mile
    private Button startButton;
    private SeekBar speedSettingBar;
    private int latest_mission_unlocked;
    private int selected_mission;
    private TextView missionText;
    private Button prevMissionButton;
    private Button nextMissionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        boolean hasSeenIntroScreen = intent.hasExtra(EXTRA_SEEN_INTRO_ALREADY);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        int num_medals = sharedPref.getInt(getString(R.string.magnolia_medals_key), 0);

        // If the game is already running, resume the game map
        if (GameService.runningInstance != null) {
            resumeGameActivity();
        } else if (!hasSeenIntroScreen) {
            showIntroScreen();
        }

        // Set up the settings/start screen
        setContentView(R.layout.activity_launch_menu);
        speedSettingBar = findViewById(R.id.running_speed_seek_bar);
        speedSettingBar.setOnSeekBarChangeListener(this);

        // Set up mission selection
        startButton = findViewById(R.id.start_game_button);
        missionText = findViewById(R.id.mission_number);
        latest_mission_unlocked = sharedPref.getInt(getString(R.string.latest_mission_unlock_key), 1);
        selected_mission = latest_mission_unlocked;
        startButton.setOnClickListener(v -> startBriefingScreen(selected_mission));
        prevMissionButton = findViewById(R.id.prev_mission_button);
        nextMissionButton = findViewById(R.id.next_mission_button);
        prevMissionButton.setOnClickListener(v -> incrementMission(-1));
        nextMissionButton.setOnClickListener(v -> incrementMission(1));
        updateMissionUI();

        int defaultValue = getResources().getInteger(R.integer.default_pace_key);
        int pacePref = sharedPref.getInt(getString(R.string.saved_pace_key), defaultValue);
        speedSettingBar.setProgress(pacePref - SPEED_OFFSET);
        updateSpeedFromBar();

        if (num_medals > 0) {
            TextView medalsText = findViewById(R.id.contents_medals);
            medalsText.setText(String.format(getString(R.string.text_medals), num_medals));
        }
    }

    private void incrementMission(int i) {
        selected_mission = Math.min(latest_mission_unlocked, Math.max(1, selected_mission + i));
        updateMissionUI();
    }

    private void updateMissionUI(){
        if (selected_mission == 1) prevMissionButton.setVisibility(View.INVISIBLE);
        else prevMissionButton.setVisibility(View.VISIBLE);
        if (selected_mission == latest_mission_unlocked) nextMissionButton.setVisibility(View.INVISIBLE);
        else nextMissionButton.setVisibility(View.VISIBLE);
        missionText.setText(new StringBuilder().append(this.getString(R.string.mission_number)).append(selected_mission).toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the game is already running, resume the game map
        if (GameService.runningInstance != null) {
            resumeGameActivity();
        }
    }

    private void resumeGameActivity() {
        Intent intent = new Intent(this, RunMapActivity.class);
        startActivity(intent);
        finish();
    }

    private void showIntroScreen() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
        finish();
    }

    private void startBriefingScreen(int missionNumber) {
        Intent intent = new Intent(this, BriefingActivity.class);
        intent.putExtra(StoryMission.EXTRA_MISSION, missionNumber);
        startActivity(intent);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateSpeedFromBar();
    }

    private void updateSpeedFromBar() {
        int speedSettingPace = speedSettingBar.getProgress() + SPEED_OFFSET;
        startButton.setText(this.getResources().getString(R.string.pace_set_button_text, speedSettingPace));
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.saved_pace_key), speedSettingPace);
        editor.apply();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

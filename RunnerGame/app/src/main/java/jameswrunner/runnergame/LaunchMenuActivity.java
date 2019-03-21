package jameswrunner.runnergame;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class LaunchMenuActivity extends Activity implements SeekBar.OnSeekBarChangeListener {
    private static final int SPEED_OFFSET = 4; // minutes per mile
    private TextView speedSettingText;
    private int speedSettingPace = 0;
    private SeekBar speedSettingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up the settings/start screen
        setContentView(R.layout.activity_launch_menu);
        speedSettingBar = findViewById(R.id.running_speed_seek_bar);
        speedSettingText = findViewById(R.id.running_speed_text);
        speedSettingBar.setOnSeekBarChangeListener(this);

        Button startButton = findViewById(R.id.start_game_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameActivity();
            }
        });

        updateSpeedFromBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If the game is already running, resume the game map
        if (GameService.runningInstance != null){
            resumeGameActivity();
            return;
        }
    }

    private void resumeGameActivity() {
        Intent intent = new Intent(this, RunMapActivity.class);
        startActivity(intent);
        finish();
    }

    private void startGameActivity() {
        Intent intent = new Intent(this, RunMapActivity.class);
        intent.putExtra(RunMapActivity.EXTRA_PACE, (double)speedSettingPace);
        startActivity(intent);
        finish();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateSpeedFromBar();
    }

    private void updateSpeedFromBar() {
        speedSettingPace = speedSettingBar.getProgress() + SPEED_OFFSET;
        speedSettingText.setText(this.getResources().getString(R.string.pace_setting_text, speedSettingPace));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}

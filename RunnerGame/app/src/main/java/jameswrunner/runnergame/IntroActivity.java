package jameswrunner.runnergame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IntroActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Button safeButton = findViewById(R.id.button_safe);
        safeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueToLaunch();
            }
        });
    }

    private void continueToLaunch() {
        Intent intent = new Intent(this, LaunchMenuActivity.class);
        intent.putExtra(LaunchMenuActivity.EXTRA_SEEN_INTRO_ALREADY, true);
        startActivity(intent);
        finish();
    }
}

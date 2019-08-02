package jamesw6811.secrets;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class IntroActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Button safeButton = findViewById(R.id.button_safe);
        safeButton.setOnClickListener(v -> continueToLaunch());
        showEULA();
    }

    private void showEULA() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean eulaAgreed = sharedPref.getBoolean(getString(R.string.eula_agreed_key), false);
        if(!eulaAgreed)
        {
            new AlertDialog.Builder(this, R.style.SappyAlertDialogStyle)
                    .setIcon(R.drawable.amu_bubble_mask)
                    .setTitle(R.string.eula_title)
                    .setMessage(getText(R.string.eula_content))
                    .setPositiveButton(R.string.accept, (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(getString(R.string.eula_agreed_key), true);
                        editor.apply();
                    })
                    .setNegativeButton(R.string.decline, (dialog, which) -> finish())
                    .show();
        }
    }

    private void continueToLaunch() {
        Intent intent = new Intent(this, LaunchMenuActivity.class);
        intent.putExtra(LaunchMenuActivity.EXTRA_SEEN_INTRO_ALREADY, true);
        startActivity(intent);
        finish();
    }
}

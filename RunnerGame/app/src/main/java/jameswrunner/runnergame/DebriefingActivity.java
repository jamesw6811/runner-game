package jameswrunner.runnergame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

public class DebriefingActivity extends Activity {
    private static final String EMAILFEEDBACK = "jamesw6811@gmail.com";
    private static final String[] EMAILSFEEDBACK = {EMAILFEEDBACK};
    private static final String EMAILFEEDBACK_SUBJECT = "Super Helpful Sappy Secrets Feedbark";
    private static final String EMAILFEEDBACK_TEMPLATE = "Sappy Secrets Developers,\n\nSappy Secrets has room to improve. I really liked:\n\n\nI didn't like:\n\n\nAnd you should also know:\n\n\n";


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
        composeEmail(EMAILSFEEDBACK, EMAILFEEDBACK_SUBJECT, EMAILFEEDBACK_TEMPLATE);
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

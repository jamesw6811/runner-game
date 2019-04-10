package jamesw6811.secrets;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DebriefingActivity extends Activity {
    private static final String EMAILFEEDBACK_SUBJECT = "Super Helpful Sappy Secrets Feedbark";


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

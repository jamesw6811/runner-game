package jamesw6811.secrets;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowAlertDialog;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.hamcrest.JMock1Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class IntroActivityTest {
    @Test
    public void opening_shouldShowEULA() {
        try(ActivityScenario<IntroActivity> scenario = ActivityScenario.launch(IntroActivity.class)) {
            scenario.onActivity(activity -> {
                AlertDialog alert =
                        ShadowAlertDialog.getLatestAlertDialog();
                ShadowAlertDialog sAlert = shadowOf(alert);
                assertEquals(sAlert.getTitle().toString(), activity.getString(R.string.eula_title));
            });
        }
    }
    @Test
    public void opening_ifAccepted_shouldNotShowEULA() {
        try(ActivityScenario<IntroActivity> scenario = ActivityScenario.launch(IntroActivity.class)) {
            scenario.onActivity(activity -> {
                AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
                alert.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
            });
            scenario.recreate();
            scenario.onActivity(activity -> assertEquals(1, ShadowAlertDialog.getShownDialogs().size()));
        }
    }
    @Test
    public void opening_ifDeclined_shouldShowEULA() {
        try(ActivityScenario<IntroActivity> scenario = ActivityScenario.launch(IntroActivity.class)) {
            scenario.onActivity(activity -> {
                AlertDialog alert = ShadowAlertDialog.getLatestAlertDialog();
                alert.getButton(DialogInterface.BUTTON_NEGATIVE).performClick();
            });
            scenario.recreate();
            scenario.onActivity(activity -> assertEquals(2, ShadowAlertDialog.getShownDialogs().size()));
        }
    }
    @Test
    public void clickingSafe_shouldStartLaunchMenuActivity() {
        try(ActivityScenario<IntroActivity> scenario = ActivityScenario.launch(IntroActivity.class)) {
            scenario.onActivity(activity -> {
                activity.findViewById(R.id.button_safe).performClick();

                Intent expectedIntent = new Intent(activity, LaunchMenuActivity.class);
                Intent actual = shadowOf((Application) getApplicationContext()).getNextStartedActivity();
                assertEquals(expectedIntent.getComponent(), actual.getComponent());
            });
        }
    }
}
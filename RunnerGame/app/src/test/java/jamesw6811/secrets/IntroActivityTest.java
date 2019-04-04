package jamesw6811.secrets;

import android.app.Application;
import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import androidx.test.core.app.ActivityScenario;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class IntroActivityTest {
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
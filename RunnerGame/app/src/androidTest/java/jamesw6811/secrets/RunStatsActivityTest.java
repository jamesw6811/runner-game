package jamesw6811.secrets;

import android.content.Context;
import android.content.Intent;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class RunStatsActivityTest {
    @Rule
    public ActivityTestRule rule = new ActivityTestRule<RunStatsActivity>(RunStatsActivity.class){
        @Override
        protected Intent getActivityIntent() {
            Intent intent = new Intent(getApplicationContext(),RunStatsActivity.class);
            intent.putExtra(RunStatsActivity.EXTRA_DISTANCE, 2.43567f/RunStatsActivity.MILES_PER_METER);
            intent.putExtra(RunStatsActivity.EXTRA_DURATION, 60f*60f+60f*13f+52f+0.32f);
            return intent;
        }
    };

    @Test
    public void stats_should_show_minutes_seconds_correctly() throws Exception {
        onView(withId(R.id.run_stats_contents)).check(matches(withSubstring("73:52")));
        onView(withId(R.id.run_stats_contents)).check(matches(withSubstring("2.44")));
        onView(withId(R.id.run_stats_contents)).check(matches(withSubstring("30:19")));
    }
}
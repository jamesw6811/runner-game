package jamesw6811.secrets;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.shadows.ShadowAlertDialog;
import org.robolectric.shadows.ShadowNotificationManager;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;


@RunWith(RobolectricTestRunner.class)
public class GameServiceTest {
    @Test
    public void starting_shouldStartForegroundNotification() {
        ServiceController<GameService> gs = Robolectric.buildService(GameService.class);
        gs.startCommand(0,0);
        ShadowNotificationManager notificationManager = shadowOf((NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE));
        Notification note = notificationManager.getAllNotifications().get(0);
     }
}
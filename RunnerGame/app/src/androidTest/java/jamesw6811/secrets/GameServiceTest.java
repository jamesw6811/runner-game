package jamesw6811.secrets;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ServiceTestRule;

import jamesw6811.secrets.gameworld.story.StoryMission;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.*;

public class GameServiceTest {
    @Rule
    public final ServiceTestRule mServiceRule = new ServiceTestRule();
    GameService gameService;
    boolean mBound;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {


        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GameService.LocalBinder binder = (GameService.LocalBinder) service;
            gameService = binder.getService();
            //gameService.bindUI(GameServiceTest.this);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            gameService = null;
            mBound = false;
        }
    };
    @Test
    public void binding_shouldStartForeground() throws Exception {
        // Context of the app under test.
        Looper.prepare();
        Context appContext = getApplicationContext();
        Intent serviceIntent = new Intent(appContext, GameService.class);
        serviceIntent.putExtra(StoryMission.EXTRA_MISSION, 1);
        IBinder binder = mServiceRule.bindService(serviceIntent, mServiceConnection,
                Context.BIND_AUTO_CREATE);
        assertNotNull(gameService);
        assertTrue(mBound);
        assertTrue(gameService.serviceStarted);
    }
}
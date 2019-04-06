package jamesw6811.secrets.time;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TimeTickerThreadTest {
    class TimeCounter implements TimeTicked {
        float total = 0;
        int calls = 0;

        @Override
        public void tickTime(float timeDelta) {
            total += timeDelta;
            calls++;
        }
    }

    @Test
    public void should_tickTime() throws InterruptedException{
        double framesToTest = 10.5; // Don't place directly on multiple of FPS
        long testWaitTime = (long)Math.floor(framesToTest/TimeTickerThread.FPS*1000);
        TimeCounter tc = new TimeCounter();
        TimeTickerThread ttt = new TimeTickerThread(tc);
        ttt.start();
        Thread.sleep(testWaitTime);
        ttt.stopRunning();
        Thread.sleep(testWaitTime);
        assertEquals((int)Math.floor(TimeTickerThread.FPS*testWaitTime/1000.0), tc.calls);
        assertEquals(tc.calls/(double)TimeTickerThread.FPS, tc.total, 1.0/(double)TimeTickerThread.FPS);
    }

}
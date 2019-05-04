package jamesw6811.secrets.gameworld.story;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jamesw6811.secrets.R;
import jamesw6811.secrets.gameworld.map.site.Headquarters;
import jamesw6811.secrets.gameworld.map.Player;
import jamesw6811.secrets.sound.TextToSpeechRunner;

public abstract class StoryManager {
    public static final int ANNOUNCEMENT_PERIOD = 120 * 1000;
    private static final long CHASE_ANNOUNCEMENT_PERIOD = 30 * 1000;
    private long lastAnnouncementTime = -ANNOUNCEMENT_PERIOD;
    private long lastChaseAnnouncementTime = -CHASE_ANNOUNCEMENT_PERIOD;
    private Context ctx;
    private TextToSpeechRunner tts;

    private List<String> receivedEvents;
    private List<Long> receivedEventsTime;

    StoryManager(Context c, TextToSpeechRunner tts) {
        ctx = c;
        this.tts = tts;
        receivedEvents = new ArrayList<>();
        receivedEventsTime = new ArrayList<>();
    }

    public final void resetChaseAnnouncement() {
        lastChaseAnnouncementTime = System.currentTimeMillis();
    }

    public final void doChaseAnnouncements(CharSequence chaseMessage) {
        if (System.currentTimeMillis() - lastChaseAnnouncementTime > CHASE_ANNOUNCEMENT_PERIOD) {
            lastChaseAnnouncementTime = System.currentTimeMillis();
            addSpeechToQueue(chaseMessage);
        }
    }

    // Make announcements about the player's available resources and tutorial messages at a certain time interval
    public final void doAnnouncementsIfTime(Player player) {
        if (System.currentTimeMillis() - lastAnnouncementTime > ANNOUNCEMENT_PERIOD) {
            doAnnouncementsNow(player);
        }
        lastAnnouncementTime = System.currentTimeMillis();
    }

    // Refresh the announcement time so that it plays as soon as possible.
    public final void refreshAnnouncement() {
        lastAnnouncementTime = -ANNOUNCEMENT_PERIOD;
    }

    public final void processEvent(String event){
        receivedEvents.add(event);
        receivedEventsTime.add(System.currentTimeMillis());
        eventReceived(event);
    }

    final boolean didReceiveEvent(String event){
        return receivedEvents.contains(event);
    }

    final long getTimeEventReceived(String event){
        return receivedEventsTime.get(receivedEvents.indexOf(event));
    }

    final int getNumEventsReceived(String event){
        return Collections.frequency(receivedEvents, event);
    }

    public final void addSpeechToQueue(CharSequence speech) {
        tts.addSpeech(speech);
    }
    public final void interruptQueueWithSpeech(CharSequence speech) {
        tts.interruptSpeech(speech);
    }
    public final void setOnDoneSpeaking(Runnable runnable) {
        tts.setOnDoneSpeaking(runnable);
    }

    final Context getContext(){
        return ctx;
    }

    // What site to discover next?
    public abstract Class discoverSite();

    // What to do when the game starts
    public abstract void gameStarted();

    // What to do when an event is received
    abstract void eventReceived(String event);

    // Check if the win/lose conditions for the game have been met and take action accordingly
    public abstract boolean checkWinConditions();
    public abstract boolean checkLoseConditions();

    void doAnnouncementsNow(Player player){
        // Resources announcement
        String resourceAnnounce = "";
        if (player.getRunningResource() > 0)
            resourceAnnounce += ctx.getString(R.string.movementResourceAnnounce, player.getRunningResource());
        else resourceAnnounce += ctx.getString(R.string.movementResourceAnnounceNone);
        if (player.getBuildingResource() > 0)
            resourceAnnounce += ctx.getString(R.string.buildingResourceAnnounce, player.getBuildingResource());
        if (player.getBuildingSubResource() > 0)
            resourceAnnounce += ctx.getString(R.string.buildingSubResourceAnnounce, player.getBuildingSubResource());
        addSpeechToQueue(resourceAnnounce);

        // Poison announcement
        if (player.isInjured()) addSpeechToQueue(ctx.getString(R.string.tutorialplayerinjured));
    }

}

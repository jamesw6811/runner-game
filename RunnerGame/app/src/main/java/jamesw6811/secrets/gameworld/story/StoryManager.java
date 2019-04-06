package jamesw6811.secrets.gameworld.story;

import android.content.Context;

import jamesw6811.secrets.R;
import jamesw6811.secrets.gameworld.map.Headquarters;
import jamesw6811.secrets.gameworld.map.Player;
import jamesw6811.secrets.sound.TextToSpeechRunner;

public class StoryManager {
    public static final int ANNOUNCEMENT_PERIOD = 120 * 1000;
    private long lastAnnouncementTime = -ANNOUNCEMENT_PERIOD;

    private static final long CHASE_ANNOUNCEMENT_PERIOD = 30*1000;
    private long lastChaseAnnouncementTime = -CHASE_ANNOUNCEMENT_PERIOD;

    public boolean tutorialFirstResource = false;
    public boolean tutorialHQbuilt = false;
    public boolean tutorialResourceBuildingDiscovered = false;
    public boolean tutorialResourceBuildingUpgraded = false;
    public boolean tutorialResourceBuildingCollected = false;
    public boolean tutorialSubResourceBuildingCollected = false;
    public boolean tutorialCompleted = false;
    public boolean winCondition = false;
    
    private Context ctx;
    private TextToSpeechRunner tts;

    public StoryManager(Context c, TextToSpeechRunner tts) {
        ctx = c;
        this.tts = tts;
    }

    public void gameStarted() {
        addSpeechToQueue(ctx.getString(R.string.game_started));
    }


    public void resetChaseAnnouncement() {
        lastChaseAnnouncementTime = System.currentTimeMillis();
    }

    public void doChaseAnnouncements(CharSequence chaseMessage){
        if (System.currentTimeMillis() - lastChaseAnnouncementTime > CHASE_ANNOUNCEMENT_PERIOD) {
            lastChaseAnnouncementTime = System.currentTimeMillis();
            addSpeechToQueue(chaseMessage);
        }
    }

    // Make announcements about the player's available resources and tutorial messages at a certain time interval
    public void doAnnouncements(Player player) {
        if (System.currentTimeMillis() - lastAnnouncementTime > ANNOUNCEMENT_PERIOD) {
            // Resources announcement
            String resourceAnnounce = "";
            if (player.getRunningResource() > 0) resourceAnnounce += ctx.getString(R.string.movementResourceAnnounce, player.getRunningResource());
            else resourceAnnounce += ctx.getString(R.string.movementResourceAnnounceNone);
            if (player.getBuildingResource() > 0) resourceAnnounce +=  ctx.getString(R.string.buildingResourceAnnounce, player.getBuildingResource());
            if (player.getBuildingSubResource() > 0) resourceAnnounce +=  ctx.getString(R.string.buildingSubResourceAnnounce, player.getBuildingSubResource());
            addSpeechToQueue(resourceAnnounce);

            // Tutorial announcement
            if (player.isInjured()) addSpeechToQueue(ctx.getString(R.string.tutorialplayerinjured));
            else if (!tutorialFirstResource) {
                addSpeechToQueue(ctx.getString(R.string.tutorialFirstResource));
            }
            else if (!tutorialHQbuilt) {
                if (player.getRunningResource() < Headquarters.RUNNING_RESOURCE_BUILD_COST) addSpeechToQueue(ctx.getString(R.string.tutorialHQbuilt_notEnoughResources));
                else addSpeechToQueue(ctx.getString(R.string.tutorialHQbuilt_readyToBuild));
            }
            else if (!tutorialResourceBuildingDiscovered) {
                addSpeechToQueue(ctx.getString(R.string.tutorialResourceBuildingDiscovered));
            }
            else if (!tutorialResourceBuildingUpgraded) {
                addSpeechToQueue(ctx.getString(R.string.tutorialResourceBuildingUpgraded));
            }
            else if (!tutorialResourceBuildingCollected) {
                addSpeechToQueue(ctx.getString(R.string.tutorialResourceBuildingCollected));
            }
            else if (!tutorialSubResourceBuildingCollected) {
                addSpeechToQueue(ctx.getString(R.string.tutorialSubResourceBuildingCollected));
            } else if (!tutorialCompleted) {
                addSpeechToQueue(ctx.getString(R.string.tutorialCompleted));
                tutorialCompleted = true;
            }
            lastAnnouncementTime = System.currentTimeMillis();
        }
    }

    // Refresh the announcement time so that it plays as soon as possible.
    public void refreshAnnouncement() {
        lastAnnouncementTime = -ANNOUNCEMENT_PERIOD;
    }

    public void addSpeechToQueue(CharSequence speech) {
        tts.addSpeech(speech);
    }

    public void interruptQueueWithSpeech(CharSequence speech) {
        tts.interruptSpeech(speech);
    }

    // Check if the win/lose conditions for the game have been met and take action accordingly
    public boolean checkWinConditions() {
        if (winCondition){
            addSpeechToQueue(ctx.getString(R.string.win_message));
            return true;
        }
        return false;
    }

    public void setOnDoneSpeaking(Runnable runnable) {
        tts.setOnDoneSpeaking(runnable);
    }
}

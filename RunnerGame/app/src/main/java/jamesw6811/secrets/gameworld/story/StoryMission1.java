package jamesw6811.secrets.gameworld.story;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jamesw6811.secrets.R;
import jamesw6811.secrets.gameworld.map.MapManager;
import jamesw6811.secrets.gameworld.map.Player;
import jamesw6811.secrets.gameworld.map.discovery.DiscoveryScheme;
import jamesw6811.secrets.gameworld.map.discovery.MultiStageCardsBasedDiscoveryScheme;
import jamesw6811.secrets.gameworld.map.site.AlarmCaptureSite;
import jamesw6811.secrets.gameworld.map.site.CaptureSite;
import jamesw6811.secrets.gameworld.map.site.DropSite;
import jamesw6811.secrets.gameworld.map.site.EmptySite;
import jamesw6811.secrets.gameworld.map.site.RunningDiscoveryUpgradeSite;
import jamesw6811.secrets.gameworld.map.site.RunningLapUpgradeSite;
import jamesw6811.secrets.sound.TextToSpeechRunner;

public class StoryMission1 extends StoryMission {
    public static final int NEXT_MISSION_NUMBER = 2; // Setting to 2 before having a mission 2 will crash
    public static final int NUMBER_OF_CAPTURES_WIN = 1;

    @Override
    public StoryManager buildStoryManager(Context c, TextToSpeechRunner tts, Random random) {
        return new MissionStoryManager(c, tts, random);
    }

    @Override
    public String getName() {
        return "The Redwood Rogue";
    }

    @Override
    public String getBriefing() {
        return "New recruit Agent Almond,\n" +
                "\n" +
                "I’m your handler. You can call me Director Stem. You and I are low on the food chain, but we can make a big difference.\n" +
                "\n" +
                "Almond - as you know, the Oaken Empire is threatening to make sawdust out of every Redwood. We must know what they are planning and stop them before anyone gets hurt. We’ve received intelligence suggesting there is a defecting Oaken agent who is tired of the sap-shed and ready to turn over valuable files to us. \n" +
                "\n" +
                "Your mission is simple: collect some Vine Cred. Then find the source and collect the files. Finally, deliver the files to the Dead Drop Date Palm. This is your first mission for the Republic - don’t let us down.\n" +
                "\n" +
                "S\n";
    }

    @Override
    public String getSuccessDebriefing() {
        return "Nice work, Agent Almond. You found the source and got us valuable information. We’ll need some time to analyze the files. Check for another mission soon.\n" +
                "\n" +
                "S\n";
    }

    @Override
    public String getFailureDebriefing() {
        return "We had to abort the mission. Let’s lay low for a while and then try to contact the Oaken Agent again.\n" +
                "\n" +
                "S\n";
    }

    @Override
    public void doRewardsAndUnlocks(Context c) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(c);
        int num_medals = sharedPref.getInt(c.getString(R.string.magnolia_medals_key), 0);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(c.getString(R.string.magnolia_medals_key), num_medals + 1);
        editor.putInt(c.getString(R.string.latest_mission_unlock_key), NEXT_MISSION_NUMBER);
        editor.apply();
    }

    static class MissionDiscoverySchemeOrdered extends MultiStageCardsBasedDiscoveryScheme {
        MissionDiscoverySchemeOrdered(Random random) {
            super(null); // null skips shuffling
            List<Class> stage1 = new LinkedList<>();
            List<Class> stage2 = new LinkedList<>();
            List<List<Class>> decks = new LinkedList<>();
            stage1.add(EmptySite.class);
            stage1.add(DropSite.class);
            stage1.add(Mission1AlarmCaptureSite.class); // Must normally match number of captures for win condition
            stage1.add(RunningDiscoveryUpgradeSite.class);
            stage1.add(RunningLapUpgradeSite.class);
            stage2.add(EmptySite.class);
            decks.add(stage1);
            decks.add(stage2);
            setDecksAndShuffle(decks);
        }
    }

    public static class Mission1AlarmCaptureSite extends AlarmCaptureSite {
        private static final int CAPTURE_CRED = 10;
        private static final int ALARM_TIMEOUT = 60*5;

        public Mission1AlarmCaptureSite(MapManager mm, LatLng latLng) {
            super(mm, "the Oaken Agent", latLng);
        }

        @Override
        protected float getAlarmCaptureSiteTime() {
            return ALARM_TIMEOUT;
        }

        @Override
        protected void doAlarmAnnouncement(int minutesRemaining) {
            story.addSpeechToQueue("You have " + minutesRemaining + " minutes until the Oaken agents catch you up! Get to the drop site.");
        }

        @Override
        protected CharSequence getCaptureSiteMapName() {
            return "Oaken Agent";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameBeforeCapture() {
            return "the Oaken Agent with a file for you";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameAfterCapture() {
            return "the empty-handed Oaken Agent";
        }

        @Override
        protected CharSequence getCaptureSiteCaptureSpeech() {
            return "You got the files from the Oaken Agent.";
        }

        @Override
        protected CharSequence getCaptureSiteNotEnoughResourcesSpeech() {
            return "You need " + CAPTURE_CRED + " Vine Cred to get the files from the Oaken Agent.";
        }

        @Override
        protected int getCaptureSiteCaptureCost() {
            return CAPTURE_CRED;
        }
    }

    class MissionStoryManager extends StoryManager{
        private int numberCaptures = 0;
        private boolean winConditionMet = false;
        private boolean loseConditionMet = false;
        private DiscoveryScheme discoveryScheme;

        MissionStoryManager(Context c, TextToSpeechRunner tts, Random random) {
            super(c, tts);
            discoveryScheme = new MissionDiscoverySchemeOrdered(random);
        }

        @Override
        public Class discoverSite() {
            return discoveryScheme.discover();
        }

        void doAnnouncementsNow(Player player){
            super.doAnnouncementsNow(player);
        }

        public void gameStarted() {
            addSpeechToQueue("Hello Agent Almond. I'm an artificially intelligent holographic life-form. But you can call me Holly for short. I'll provide you support on your missions. Your objective is to find the Oaken agent and and collect the files. You'll need 10 Vine Cred to pay the source. One way to get Vine Cred is by exploring.");
        }

        @Override
        void eventReceived(String event) {
            switch (event) {
                case CaptureSite.EVENT_CAPTURE_SITE_CAPTURED:
                    numberCaptures++;
                    addSpeechToQueue("You got the files, but I detect that you are being trailed and need to get to the Dead Drop. Fast. I estimate you have 5 minutes.");
                    break;
                case AlarmCaptureSite
                            .EVENT_ALARM_OUT:
                    loseConditionMet = true;
                    addSpeechToQueue("The enemy agents are catching us up. We need to abort for now. I'm sending the debriefing to your mobile device.");
                    break;
                case DropSite.EVENT_DROP_SITE_ACTIVATED:
                    if (numberCaptures == NUMBER_OF_CAPTURES_WIN){
                        winConditionMet = true;
                        addSpeechToQueue("Nice work Agent Almond. We have the files. I'm sending the debriefing to your mobile device.");
                    }
                    else addSpeechToQueue("We can't end the mission yet, Almond. We still have work to do!");
                    break;
            }
        }

        public boolean checkWinConditions() {
            return winConditionMet;
        }

        @Override
        public boolean checkLoseConditions() {
            return loseConditionMet;
        }
    }
}

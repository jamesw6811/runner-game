package jamesw6811.secrets.gameworld.story;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jamesw6811.secrets.R;
import jamesw6811.secrets.gameworld.map.Player;
import jamesw6811.secrets.gameworld.map.discovery.DiscoveryScheme;
import jamesw6811.secrets.gameworld.map.discovery.MultiStageCardsBasedDiscoveryScheme;
import jamesw6811.secrets.gameworld.map.site.AlarmCaptureSite;
import jamesw6811.secrets.gameworld.map.site.CaptureSite;
import jamesw6811.secrets.gameworld.map.site.DropSite;
import jamesw6811.secrets.gameworld.map.site.EmptySite;
import jamesw6811.secrets.gameworld.map.site.Mission1AlarmCaptureSite;
import jamesw6811.secrets.gameworld.map.site.RunningDiscoveryUpgradeSite;
import jamesw6811.secrets.gameworld.map.site.RunningLapUpgradeSite;
import jamesw6811.secrets.sound.TextToSpeechRunner;

public class StoryMission1 extends StoryMission {
    public static final int NEXT_MISSION_NUMBER = 2;
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
        return "“New recruit Agent Almond,\n" +
                "\n" +
                "I’m your handler. You can call me Director Stem. You and I are low on the food chain, but we can make a big difference.\n" +
                "\n" +
                "Almond - as you know, the Oaken Empire is threatening to make sawdust out of every Redwood. We must know what they are planning and stop them before anyone gets hurt. We’ve received intelligence suggesting there is a defecting Oaken agent who is tired of the sap-shed and ready to turn over valuable files to us. \n" +
                "\n" +
                "Your mission is simple: collect some Vine Cred. Then find the source and collect the files. Finally, deliver the files to the Dead Drop Date Palm. This is your first mission for the Republic - don’t let us down.”\n" +
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
        return "We had to abort the mission. Let’s lay low for a while and then try to contact the Redwood Rogue again.\n" +
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
            addSpeechToQueue(getContext().getString(R.string.game_started));
        }

        @Override
        void eventReceived(String event) {
            switch (event) {
                case CaptureSite.EVENT_CAPTURE_SITE_CAPTURED:
                    numberCaptures++;
                    break;
                case AlarmCaptureSite
                            .EVENT_ALARM_OUT:
                    loseConditionMet = true;
                    break;
                case DropSite.EVENT_DROP_SITE_ACTIVATED:
                    if (numberCaptures == NUMBER_OF_CAPTURES_WIN) winConditionMet = true;
                    else addSpeechToQueue("We can't end the mission yet, Almond. We still have work to do!");
                    break;
            }
        }

        public boolean checkWinConditions() {
            if (winConditionMet) {
                addSpeechToQueue("Nice work Agent Almond. We have the files. I'm sending the debriefing to your mobile device.");
                return true;
            } else return false;
        }
    }
}

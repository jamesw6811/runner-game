package jamesw6811.secrets.gameworld.story;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jamesw6811.secrets.R;
import jamesw6811.secrets.gameworld.map.Player;
import jamesw6811.secrets.gameworld.map.discovery.CardsBasedDiscoveryScheme;
import jamesw6811.secrets.gameworld.map.discovery.DiscoveryScheme;
import jamesw6811.secrets.gameworld.map.site.BuildingResourceSite;
import jamesw6811.secrets.gameworld.map.site.BuildingSubResourceSite;
import jamesw6811.secrets.gameworld.map.site.ChaseSite;
import jamesw6811.secrets.sound.TextToSpeechRunner;

public class StoryMission1 extends StoryMission {
    public static final int NEXT_MISSION_NUMBER = 2;

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
                "As you know, we’ve been in a prolonged conflict with the Redwood Republic, which is hell-bent on making sawdust out of every Oak. We’ve received some intel that there is a defecting Redwood agent, tired of the sap-shed and ready to talk to us. \n" +
                "\n" +
                "The task is simple: collect some Vine Cred, convert the Redwood rogue agent to our side, then get yourself to the Dead Drop Date Palm. This is your first mission with the Oaken Empire - don’t let us down.\n" +
                "\n" +
                "S\n";
    }

    @Override
    public String getSuccessDebriefing() {
        return "Nice work, Agent Almond. You converted the Redwood Rogue to the Oaken Empire. We’ll need some time to talk to this traitor and learn about the Redwoods’ plans. Check for another mission soon.\n" +
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

    class MissionDiscoveryScheme extends CardsBasedDiscoveryScheme {
        MissionDiscoveryScheme(Random random) {
            super(random);
            List<Class> cards = new LinkedList<>();
            cards.add(BuildingResourceSite.class);
            cards.add(BuildingSubResourceSite.class);
            cards.add(ChaseSite.class);
            cards.add(BuildingResourceSite.class);
            cards.add(BuildingSubResourceSite.class);
            cards.add(ChaseSite.class);
            setDeckAndShuffle(cards);
        }
    }

    class MissionStoryManager extends StoryManager{
        private DiscoveryScheme discoveryScheme;
        MissionStoryManager(Context c, TextToSpeechRunner tts, Random random) {
            super(c, tts);
            discoveryScheme = new MissionDiscoveryScheme(random);
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

        }

        public boolean checkWinConditions() {
            throw new RuntimeException("Not yet implemented"); // Need to add win conditions here and winning speech
/*            if (true) {
                addSpeechToQueue(getContext().getString(R.string.win_message));
                return true;
            } else return false;*/
        }
    }
}

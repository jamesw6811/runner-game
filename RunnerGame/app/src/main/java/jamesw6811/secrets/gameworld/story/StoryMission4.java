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
import jamesw6811.secrets.gameworld.map.site.CaptureSite;
import jamesw6811.secrets.gameworld.map.site.ChaseSite;
import jamesw6811.secrets.gameworld.map.site.DropSite;
import jamesw6811.secrets.gameworld.map.site.EmptySite;
import jamesw6811.secrets.gameworld.map.site.HazardSite;
import jamesw6811.secrets.gameworld.map.site.RunningDiscoveryUpgradeSite;
import jamesw6811.secrets.gameworld.map.site.RunningLapUpgradeSite;
import jamesw6811.secrets.gameworld.map.site.TrapSite;
import jamesw6811.secrets.gameworld.map.site.TrapUpgradeSite;
import jamesw6811.secrets.gameworld.map.site.WalletUpgradeSite;
import jamesw6811.secrets.sound.TextToSpeechRunner;

public class StoryMission4 extends StoryMission {
    public static final int NEXT_MISSION_NUMBER = 5; // Setting to 2 before having a mission 2 will crash
    public static final int NUMBER_OF_CAPTURES_WIN = 3;

    @Override
    public StoryManager buildStoryManager(Context c, TextToSpeechRunner tts, Random random) {
        return new MissionStoryManager(c, tts, random);
    }

    @Override
    public String getName() {
        return "Stabbed in the Bark";
    }

    @Override
    public String getBriefing() {
        return "Agent Almond,\n" +
                "\n" +
                "This is Secretary Root, the head of the spy service. I’ve heard of your exemplary service getting us valuable information. The only problem is that our Republic’s government has not been receiving this information. It has been intercepted. \n" +
                "\n" +
                "We believe that Director Stem is a double-agent working for the Oaken Empire. We also believe that he was the one who told the Nightshade Organization you were behind the hack and where to find you. We have to find out why he’s doing this. \n" +
                "\n" +
                "Stem has hidden several stashes of crucial info around an old Nightshade chemical waste disposal plant. Find those stashes, but be careful Agent. You can expect resistance; Stem is a master spy, and he has eyes on every branch. In 25 minutes we’ll abort the mission - with or without you.\n" +
                "\n" +
                "-R\n";
    }

    @Override
    public String getSuccessDebriefing() {
        return "Good work Agent. We will analyze this information and confirm if Director Stem is a double-agent.\n" +
                "\n" +
                "- R";
    }

    @Override
    public String getFailureDebriefing() {
        return "We have to abort. Stem cannot know we are tracking him. Go about your business as normal and we’ll try again later. \n" +
                "\n" +
                "- R\n";
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

    static class MissionDiscoveryScheme extends MultiStageCardsBasedDiscoveryScheme {
        MissionDiscoveryScheme(Random random) {
            super(random); // null skips shuffling
            List<Class> stage0 = new LinkedList<>();
            List<Class> stage1 = new LinkedList<>();
            List<Class> stage2 = new LinkedList<>();
            List<List<Class>> decks = new LinkedList<>();

            stage0.add(DropSite.class);
            stage0.add(RunningDiscoveryUpgradeSite.class);
            stage0.add(RunningDiscoveryUpgradeSite.class);
            stage0.add(RunningLapUpgradeSite.class);
            stage0.add(RunningLapUpgradeSite.class);
            stage0.add(TrapUpgradeSite.class);
            stage0.add(WalletUpgradeSite.class);
            stage0.add(WalletUpgradeSite.class);
            stage0.add(HazardSite.class);

            stage1.add(TrapSite.class);
            stage1.add(TrapSite.class);
            stage1.add(DropSite.class);
            stage1.add(HazardSite.class);
            stage1.add(Mission4GuardSite.class);
            stage1.add(Mission4GuardSite.class);
            stage1.add(Mission4CaptureSite.class);
            stage1.add(Mission4CaptureSite.class);
            stage1.add(Mission4CaptureSite.class);

            stage2.add(HazardSite.class);
            stage2.add(EmptySite.class);

            decks.add(stage0);
            decks.add(stage1);
            decks.add(stage2);
            setDecksAndShuffle(decks);
        }
    }

    public static class Mission4GuardSite extends ChaseSite {

        public Mission4GuardSite(MapManager mm, LatLng position) {
            super(mm, "a Nightshade Guard Tower", position);
        }

        @Override
        protected CharSequence getChaseSiteMapName() {
            return "Guard";
        }

        @Override
        protected double getChaseDifficulty() {
            return 0.8;
        }

        @Override
        protected String getChaseStartMessage() {
            return "A Nightshade Guard begins chasing you! Run!";
        }

        @Override
        public void chaseSuccessful() {
            story.addSpeechToQueue("You lost the Nightshade guard. Nice work.");
        }

        @Override
        public void chaseFailed() {
            story.addSpeechToQueue("The Nightshade guard caught you. You got away but you were injured. Get to a Dead Drop to patch yourself up.");
            player.injure();
        }

        @Override
        public CharSequence getChaseMessage() {
            return "You are being chased by a Nightshade guard. Run away!";
        }

        @Override
        public CharSequence getChaserName() {
            return "a Nightshade guard";
        }
    }

    public static class Mission4CaptureSite extends CaptureSite {

        private static final int CAPTURE_CRED = 20;

        public Mission4CaptureSite(MapManager mm, LatLng latLng) {
            super(mm, "one of Stem's stashes", latLng);
        }

        @Override
        protected CharSequence getCaptureSiteMapName() {
            return "Stash";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameBeforeCapture() {
            return "one of Stem's stashes";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameAfterCapture() {
            return "a stash you already hacked";
        }

        @Override
        protected CharSequence getCaptureSiteCaptureSpeech() {
            return "You hacked open one of Stem's stashes. He is keeping lots of secrets.";
        }

        @Override
        protected CharSequence getCaptureSiteNotEnoughResourcesSpeech() {
            return "You need " + getCaptureSiteCaptureCost() + " Vine Cred to gain access to this stash.";
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
        private float timeAlarmOut = 60.0f*25.0f - 1;
        private boolean firstHazardApproached = false;
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
        }

        @Override
        public void tick(float timeDelta) {
            super.tick(timeDelta);
            double timeAnnounceResolution = 5.0*60.0;
            if (timeAlarmOut < timeAnnounceResolution) timeAnnounceResolution = 60.0;
            if (timeAlarmOut > 0) {
                int minutesBefore = (int) Math.floor(timeAlarmOut / timeAnnounceResolution);
                timeAlarmOut -= timeDelta;
                int minutesAfter = (int) Math.floor(timeAlarmOut / timeAnnounceResolution);
                if (timeAlarmOut <= 0) {
                    loseConditionMet = true;
                } else if (minutesAfter != minutesBefore){
                    addSpeechToQueue("Nightshade reinforcements inbound. " + (int)(minutesBefore*timeAnnounceResolution/60.0f) + " minutes until mission abort.");
                }
            }
        }

        @Override
        void eventReceived(String event) {
            switch (event) {
                case CaptureSite.EVENT_CAPTURE_SITE_CAPTURED:
                    numberCaptures++;
                    if (numberCaptures == NUMBER_OF_CAPTURES_WIN) {
                        addSpeechToQueue("We have enough information on Director Stem's crimes. Get to the Dogwood Dead Drop for immediate tree-vac.");
                    }
                    break;
                case DropSite.EVENT_DROP_SITE_ACTIVATED:
                    if (numberCaptures == NUMBER_OF_CAPTURES_WIN){
                        winConditionMet = true;
                        interruptQueueWithSpeech("Nice work Agent Almond. Holly Copter inbound for tree-vac. I'm sending the debriefing to your mobile device.");
                    }
                    else interruptQueueWithSpeech("We can't evacuate you yet, Almond. We must find out the truth about Director Stem.");
                    break;
                case HazardSite.EVENT_HAZARD_SITE_APPROACHED:
                    if (!firstHazardApproached){
                        firstHazardApproached = true;
                        addSpeechToQueue("It looks like there is hazardous waste around the Nightshade chemical plant. Analyzing waste composition. Waste analyzed. 100% toxic to tree spies. I recommend you avoid it.");
                    }
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

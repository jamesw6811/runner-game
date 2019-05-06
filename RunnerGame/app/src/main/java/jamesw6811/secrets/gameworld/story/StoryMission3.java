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
import jamesw6811.secrets.gameworld.map.site.ChaseSite;
import jamesw6811.secrets.gameworld.map.site.ChaserCaptureSite;
import jamesw6811.secrets.gameworld.map.site.DropSite;
import jamesw6811.secrets.gameworld.map.site.EmptySite;
import jamesw6811.secrets.gameworld.map.site.RunningDiscoveryUpgradeSite;
import jamesw6811.secrets.gameworld.map.site.RunningLapUpgradeSite;
import jamesw6811.secrets.gameworld.map.site.TrapSite;
import jamesw6811.secrets.gameworld.map.site.TrapUpgradeSite;
import jamesw6811.secrets.sound.TextToSpeechRunner;

public class StoryMission3 extends StoryMission {
    public static final int NEXT_MISSION_NUMBER = 1; // Setting to 2 before having a mission 2 will crash
    public static final int NUMBER_OF_CAPTURES_WIN = 3;

    @Override
    public StoryManager buildStoryManager(Context c, TextToSpeechRunner tts, Random random) {
        return new MissionStoryManager(c, tts, random);
    }

    @Override
    public String getName() {
        return "Make Like a Tree";
    }

    @Override
    public String getBriefing() {
        return "URGENT: Agent Almond,\n" +
                "\n" +
                "I don’t know how, but the Nightshade Organization tracked the database hack back to you. They know your location and the Ginkgo Guard, a city reserve force, is coming for you. You only have 25 minutes to make it back into Redwood Republic territory. \n" +
                "\n" +
                "We can evacuate you, but there are three Anti-Air Aspens that will make splinters out of our HollyCopters. Take them out, evade or sabotage the Ginkgo Guards, and then get to a Dead Drop Date.\n" +
                "\n" +
                "S\n";
    }

    @Override
    public String getSuccessDebriefing() {
        return "I’m so glad you made it out OK. Take a rest, and we’ll talk soon.\n" +
                "\n" +
                "S\n";
    }

    @Override
    public String getFailureDebriefing() {
        return "We had to abort. Go underground for a while until we have another window to attempt an escape.\n" +
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

    static class MissionDiscoveryScheme extends MultiStageCardsBasedDiscoveryScheme {
        MissionDiscoveryScheme(Random random) {
            super(random); // null skips shuffling
            List<Class> stage0 = new LinkedList<>();
            List<Class> stage1 = new LinkedList<>();
            List<Class> stage2 = new LinkedList<>();
            List<Class> stage3 = new LinkedList<>();
            List<Class> stage4 = new LinkedList<>();
            List<List<Class>> decks = new LinkedList<>();

            stage0.add(Mission3GuardSite.class);

            stage1.add(TrapSite.class);

            stage2.add(DropSite.class);
            stage2.add(Mission3CaptureSite.class);
            stage2.add(TrapUpgradeSite.class);
            stage2.add(TrapSite.class);

            stage3.add(DropSite.class);
            stage3.add(Mission3CaptureSite.class);
            stage3.add(Mission3CaptureSite.class);
            stage3.add(Mission3GuardSite.class);
            stage3.add(Mission3GuardSite.class);
            stage3.add(TrapSite.class);

            stage4.add(EmptySite.class);
            stage4.add(EmptySite.class);
            stage4.add(Mission3GuardSite.class);

            decks.add(stage0);
            decks.add(stage1);
            decks.add(stage2);
            decks.add(stage3);
            decks.add(stage4);
            setDecksAndShuffle(decks);
        }
    }

    public static class Mission3GuardSite extends ChaseSite {

        public Mission3GuardSite(MapManager mm, LatLng position) {
            super(mm, "a Ginkgo Guard Tower", position);
        }

        @Override
        protected CharSequence getChaseSiteMapName() {
            return "Guard";
        }

        @Override
        protected double getChaseDifficulty() {
            return 0.75;
        }

        @Override
        protected String getChaseStartMessage() {
            return "A Ginkgo Guard begins chasing you! Run!";
        }

        @Override
        public void chaseSuccessful() {
            story.addSpeechToQueue("You lost the Ginkgo guard. Nice work.");
        }

        @Override
        public void chaseFailed() {
            story.addSpeechToQueue("The Ginkgo guard caught you. You got away but you were injured. Get to a Dead Drop to patch yourself up.");
            player.injure();
        }

        @Override
        public CharSequence getChaseMessage() {
            return "You are being chased by a Ginkgo guard. Run away!";
        }

        @Override
        public CharSequence getChaserName() {
            return "a Ginkgo guard";
        }
    }

    public static class Mission3CaptureSite extends CaptureSite {

        private static final int CAPTURE_CRED = 10;

        public Mission3CaptureSite(MapManager mm, LatLng latLng) {
            super(mm, "an Anti-Air Aspen", latLng);
        }

        @Override
        protected CharSequence getCaptureSiteMapName() {
            return "Aspen";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameBeforeCapture() {
            return "an Anti-Air Aspen";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameAfterCapture() {
            return "the remains of a sabotaged Anti-Air Aspen";
        }

        @Override
        protected CharSequence getCaptureSiteCaptureSpeech() {
            return "Calculating Sabotage Results. Anti-Air Aspen 100% sawdust.";
        }

        @Override
        protected CharSequence getCaptureSiteNotEnoughResourcesSpeech() {
            return "You need " + getCaptureSiteCaptureCost() + " Vine Cred to sabotage this Anti-Air Aspen.";
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
        private boolean firstChaseDone = false;
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
                    addSpeechToQueue("It's heating up out there. " + (int)(minutesBefore*timeAnnounceResolution/60.0f) + " minutes until mission abort.");
                }
            }
        }

        @Override
        void eventReceived(String event) {
            switch (event) {
                case CaptureSite.EVENT_CAPTURE_SITE_CAPTURED:
                    numberCaptures++;
                    break;
                case DropSite.EVENT_DROP_SITE_ACTIVATED:
                    if (numberCaptures == NUMBER_OF_CAPTURES_WIN){
                        winConditionMet = true;
                        interruptQueueWithSpeech("Nice work Agent Almond. Holly Copter inbound for tree-vac. I'm sending the debriefing to your mobile device.");
                    }
                    else interruptQueueWithSpeech("We can't evacuate you yet, Almond. The Anti-Air Aspens will shoot us down!");
                    break;
                case ChaseSite.EVENT_CHASE_SITE_CHASE_STARTED:
                    if (!firstChaseDone){
                        firstChaseDone = true;
                        addSpeechToQueue("Hollie calculating escape plan. First step: run away. Second step: sabotage anti-air aspens. Third step: get to Dogwood Dead Drop.");
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

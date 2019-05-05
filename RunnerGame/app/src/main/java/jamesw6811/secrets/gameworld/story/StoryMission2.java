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
import jamesw6811.secrets.sound.TextToSpeechRunner;

public class StoryMission2 extends StoryMission {
    public static final int NEXT_MISSION_NUMBER = 1; // Setting to 2 before having a mission 2 will crash
    public static final int NUMBER_OF_CAPTURES_WIN = 2;

    @Override
    public StoryManager buildStoryManager(Context c, TextToSpeechRunner tts, Random random) {
        return new MissionStoryManager(c, tts, random);
    }

    @Override
    public String getName() {
        return "Planting the Seeds";
    }

    @Override
    public String getBriefing() {
        return "Agent Almond,\n" +
                "\n" +
                "We’ve stumbled upon something big here. The defector had evidence that the Oaks are producing a biological weapon of mass destruction: a virus that only kills Redwood. They are producing this virus in the Nightshade Organization, a bio-engineering company located right across the border from us. We need to find out how far they are in the development of the virus. To do this, you will infiltrate the Oaken Empire - behind enemy vines.\n" +
                "\n" +
                "Your mission is to meet a technician sympathetic to our cause who will provide you with passwords to the Nightshade Organization’s network. This source used to work in the Oaken Empire’s War Department so be sure to have your wits about you.\n" +
                "\n" +
                "Once you have the passwords, hack into the Organization’s network, and upload our spying software into their databases. Do not get caught, Agent Almond. The Nightshade Organization’s guards will police the area and track you down in no time. If the Empire finds out we are onto them they may release the virus! Be fast and be safe.\n" +
                "\n" +
                "S\n";
    }

    @Override
    public String getSuccessDebriefing() {
        return "We got the data from their network! This is everything we could have hoped for. There might be a chance to stop this virus and keep the peace! Once we’ve analyzed the data, I’ll have more work for you.\n" +
                "S\n";
    }

    @Override
    public String getFailureDebriefing() {
        return "We had to abort the mission. Let’s lay low for a while and then try to hack their network again.\n" +
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
            List<Class> stage5 = new LinkedList<>();
            List<List<Class>> decks = new LinkedList<>();

            stage0.add(EmptySite.class);

            stage1.add(DropSite.class);
            stage1.add(RunningDiscoveryUpgradeSite.class);
            stage1.add(RunningLapUpgradeSite.class);

            stage2.add(Mission2FinalCaptureSite.class);

            stage3.add(DropSite.class);
            stage3.add(RunningDiscoveryUpgradeSite.class);
            stage3.add(RunningLapUpgradeSite.class);
            stage3.add(Mission2GuardSite.class);

            stage4.add(Mission2AlarmCaptureSite.class); // Must normally match number of captures for win condition

            stage5.add(EmptySite.class);

            decks.add(stage0);
            decks.add(stage1);
            decks.add(stage2);
            decks.add(stage3);
            decks.add(stage4);
            decks.add(stage5);
            setDecksAndShuffle(decks);
        }
    }

    public static class Mission2GuardSite extends ChaseSite {

        public Mission2GuardSite(MapManager mm, LatLng position) {
            super(mm, "a Nightshade Guard Tower", position);
        }

        @Override
        protected CharSequence getChaseSiteMapName() {
            return "Guard";
        }

        @Override
        protected double getChaseDifficulty() {
            return 0.70;
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
    }

    public static class Mission2AlarmCaptureSite extends AlarmCaptureSite {
        private static final int CAPTURE_CRED = 10;
        private static final int ALARM_TIMEOUT = 60*5;
        static final String ID = "FIRST_CAPTURE_SITE";

        public Mission2AlarmCaptureSite(MapManager mm, LatLng latLng) {
            super(mm, "the Nightshade Technician's house", latLng);
            registerThis(ID);
        }

        @Override
        protected float getAlarmCaptureSiteTime() {
            return ALARM_TIMEOUT;
        }

        @Override
        protected void doAlarmAnnouncement(int minutesRemaining) {
            story.addSpeechToQueue("You have " + minutesRemaining + " minutes until Nightshade finds you out! Hack the database and get to the drop site.");
        }

        @Override
        protected CharSequence getCaptureSiteMapName() {
            return "Technician";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameBeforeCapture() {
            return "the Nightshade Technician's house";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameAfterCapture() {
            return "a locked house";
        }

        @Override
        protected CharSequence getCaptureSiteCaptureSpeech() {
            return "Holly analyzing situation. Good news. You got the database passwords from the technician. Bad news. Nightshade is on your trail. You have 5 minutes to hack the database and get to the Drop Site.";
        }

        @Override
        protected CharSequence getCaptureSiteNotEnoughResourcesSpeech() {
            return "You need " + CAPTURE_CRED + " Vine Cred to get the passwords.";
        }

        @Override
        protected int getCaptureSiteCaptureCost() {
            return CAPTURE_CRED;
        }
    }

    public static class Mission2FinalCaptureSite extends ChaserCaptureSite {

        private static final int CAPTURE_CRED = 10;

        public Mission2FinalCaptureSite(MapManager mm, LatLng latLng) {
            super(mm, "the Nightshade Database", latLng);
        }

        @Override
        protected double getChaseDifficulty() {
            return 0.70;
        }

        @Override
        protected String getCaptureSiteDependency() {
            return Mission2AlarmCaptureSite.ID;
        }

        @Override
        protected String getCaptureSiteDependencyNotMetSpeech() {
            return "You don't have any passwords for the database. We need to get the passwords from the technician first.";
        }

        @Override
        protected CharSequence getCaptureSiteMapName() {
            return "Database";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameBeforeCapture() {
            return "the Nightshade Database";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameAfterCapture() {
            return "the hacked Nightshade Database";
        }

        @Override
        protected CharSequence getCaptureSiteCaptureSpeech() {
            return "HacheT spyware initialized. Making connection. Database successfully hacked.";
        }

        @Override
        protected CharSequence getCaptureSiteNotEnoughResourcesSpeech() {
            return "You need " + getCaptureSiteCaptureCost() + " Vine Cred to initialize the hacking spyware.";
        }

        @Override
        protected int getCaptureSiteCaptureCost() {
            return CAPTURE_CRED;
        }

        @Override
        public void chaseSuccessful() {
            story.addSpeechToQueue("You lost the Nightshade guard. Nice work. Now get to the Dead Drop before the rest of the organization catches up.");
        }

        @Override
        public void chaseFailed() {
            story.interruptQueueWithSpeech("The Nightshade guard caught you. You got away but you were injured. Get to a Dead Drop to patch yourself up.");
            player.injure();
        }

        @Override
        public CharSequence getChaseMessage() {
            return "A Nightshade guard is chasing you. Lose him.";
        }
    }

    class MissionStoryManager extends StoryManager{
        private int numberCaptures = 0;
        private boolean winConditionMet = false;
        private boolean loseConditionMet = false;
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
            addSpeechToQueue("Holly booting up. Holly online. Find the technician and get the passwords. You'll need 10 Vine Cred for the passwords and 10 Vine Cred to hack the database, but remember: you can only hold 10 Vine Cred at one time.");
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
                    interruptQueueWithSpeech("Nightshade is catching up with you - we need to abort. I'm sending the debriefing to your mobile device.");
                    break;
                case DropSite.EVENT_DROP_SITE_ACTIVATED:
                    if (numberCaptures == NUMBER_OF_CAPTURES_WIN){
                        winConditionMet = true;
                        interruptQueueWithSpeech("Nice work Agent Almond. Database connection established. I'm sending the debriefing to your mobile device.");
                    }
                    else interruptQueueWithSpeech("We can't end the mission yet, Almond. We still have work to do!");
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

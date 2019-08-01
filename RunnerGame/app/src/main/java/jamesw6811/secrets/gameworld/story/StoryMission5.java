package jamesw6811.secrets.gameworld.story;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

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

public class StoryMission5 extends StoryMission {
    public static final int NEXT_MISSION_NUMBER = 1; // Setting to 2 before having a mission 2 will crash
    public static final int NUMBER_OF_CAPTURES_WIN = 1;
    private static boolean released_virus = false;
    private static boolean disabled_regulator = false;

    @Override
    public StoryManager buildStoryManager(Context c, TextToSpeechRunner tts, Random random) {
        return new MissionStoryManager(c, tts, random);
    }

    @Override
    public String getName() {
        return "Out on a Limb";
    }

    @Override
    public String getBriefing() {
        return "Almond, we have analyzed the data you obtained regarding Agent Stem. It turns out he is not a double agent, but he had gone rogue and has been arrested. He was attempting to turn the virus back on the Oaken Empire, which would have killed millions of innocent trees - it’s not acceptable. He did help us after all, with a schematic and location of the Nightshade Viral Plant. \n" +
                "\n" +
                "We need you to go and disable the Viral Plant once and for all by disabling the temperature regulator, which will boil the virus and destroy the plant.\n" +
                "\n" +
                "- R\n" +
                "\n" +
                "<><><><>\n" +
                "<incoming 2nd transmission, includes “plant_program.app” attachment>\n" +
                "\n" +
                "URGENT: Agent Almond, this is Stem. The Oaken Empire will never stop hunting Redwoods. The only way to keep our Republic safe is to end this, once and for all, by ending the Oaken Empire. I’m sure Root is sending you to the Viral Plant. \n" +
                "\n" +
                "Upload the attached program into the Viral Plant’s mainframe, and you will turn the virus onto the Oaks. None of them are innocent. Beware, the Plant is full of guards and hazards, but also may have quite a few upgrades for you.\n" +
                "\n" +
                "- S\n";
    }

    @Override
    public String getSuccessDebriefing() {
        if (released_virus && disabled_regulator) {
            return "Almond, this is President Trunk. I am contacting you directly because you have accomplished the impossible. Despite conflicting orders from your superiors, you managed to both strike an offense blow at the Oaken Empire and disable the Viral Plant once and for all. \n" +
                    "\n" +
                    "You deserve a medal, but the game developers were too busy to put one in the game for this secret bonus ending. So all you get is this message: \n" +
                    "\n" +
                    "From the Office of President Trunk and the Developers of Sappy Secrets,\n" +
                    "Great work and thanks for playing.\n";
        } else if (released_virus){
            return "Almond, this is Stem. I’ve been released from my arrest after word reached the HQ that you flawlessly executed my plan. As I write this, the Oaken Empire is dying the death they deserve. It’s a new era of peace for the Redwood Republic and for you. There is no cost too high for that.\n" +
                    "\n" +
                    "- S\n";
        } else if (disabled_regulator){
            return "Almond, this is Root. I’ve confirmed that the Viral Plant is destroyed. The Nightshade Organization has lost years of research and all of the virus they planned to use on the Redwood Republic. We are safe for now, thanks to you. You deserve a long vacation. \n" +
                    "\n" +
                    "- R\n";
        } else {
            throw new RuntimeException("Success Debriefing requested without any success conditions met.");
        }
    }

    @Override
    public String getFailureDebriefing() {
        return "I’m glad we managed to get you out in one piece, but failure is not an option. We’ll send some agents to sabotage the Viral Plant supply lines to buy you another chance. Get back in there and end this.\n" +
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
            List<Class> stage3 = new LinkedList<>();
            List<Class> stage4 = new LinkedList<>();
            List<Class> stage5 = new LinkedList<>();
            List<Class> stage6 = new LinkedList<>();
            List<Class> stage7 = new LinkedList<>();
            List<List<Class>> decks = new LinkedList<>();

            stage0.add(DropSite.class);
            stage0.add(RunningDiscoveryUpgradeSite.class);
            stage0.add(RunningLapUpgradeSite.class);

            stage1.add(TrapUpgradeSite.class);
            stage1.add(WalletUpgradeSite.class);
            stage1.add(TrapUpgradeSite.class);
            stage1.add(WalletUpgradeSite.class);

            stage2.add(Mission5AlarmSite.class);

            stage3.add(RunningDiscoveryUpgradeSite.class);
            stage3.add(RunningLapUpgradeSite.class);
            stage3.add(Mission5GuardSite.class);
            stage3.add(Mission5CaptureSite.class);
            stage3.add(HazardSite.class);
            stage3.add(TrapSite.class);
            stage3.add(WalletUpgradeSite.class);
            stage3.add(WalletUpgradeSite.class);
            stage3.add(WalletUpgradeSite.class);

            stage4.add(RunningDiscoveryUpgradeSite.class);
            stage4.add(RunningLapUpgradeSite.class);
            stage4.add(Mission5GuardSite.class);
            stage4.add(Mission5CaptureSiteAlternate.class);
            stage4.add(HazardSite.class);
            stage4.add(TrapSite.class);
            stage4.add(WalletUpgradeSite.class);
            stage4.add(WalletUpgradeSite.class);
            stage4.add(WalletUpgradeSite.class);

            stage5.add(RunningDiscoveryUpgradeSite.class);
            stage5.add(RunningLapUpgradeSite.class);
            stage5.add(Mission5GuardSite.class);
            stage5.add(HazardSite.class);
            stage5.add(TrapSite.class);
            stage5.add(WalletUpgradeSite.class);
            stage5.add(WalletUpgradeSite.class);
            stage5.add(TrapUpgradeSite.class);
            stage5.add(TrapUpgradeSite.class);

            stage6.add(DropSite.class);

            stage7.add(RunningDiscoveryUpgradeSite.class);
            stage7.add(RunningLapUpgradeSite.class);
            stage7.add(Mission5GuardSite.class);
            stage7.add(HazardSite.class);
            stage7.add(TrapSite.class);
            stage7.add(WalletUpgradeSite.class);
            stage7.add(WalletUpgradeSite.class);
            stage7.add(TrapUpgradeSite.class);
            stage7.add(TrapUpgradeSite.class);

            decks.add(stage0);
            decks.add(stage1);
            decks.add(stage2);
            decks.add(stage3);
            decks.add(stage4);
            decks.add(stage5);
            decks.add(stage6);
            decks.add(stage7);
            setDecksAndShuffle(decks);
        }
    }

    public static class Mission5GuardSite extends ChaseSite {

        public Mission5GuardSite(MapManager mm, LatLng position) {
            super(mm, "a Nightshade Barracks", position);
        }

        @Override
        protected CharSequence getChaseSiteMapName() {
            return "Guard";
        }

        @Override
        protected double getChaseDifficulty() {
            return 0.95;
        }

        @Override
        protected String getChaseStartMessage() {
            return "A Nightshade Elite Guard begins chasing you! Run!";
        }

        @Override
        public void chaseSuccessful() {
            story.addSpeechToQueue("You lost the Nightshade elite guard. Nice work.");
        }

        @Override
        public void chaseFailed() {
            story.addSpeechToQueue("The Nightshade elite guard caught you. You got away but you were injured. Get to a Dead Drop to patch yourself up.");
            player.injure();
        }

        @Override
        public CharSequence getChaseMessage() {
            return "You are being chased by a Nightshade elite guard. Run away!";
        }

        @Override
        public CharSequence getChaserName() {
            return "a Nightshade elite guard";
        }
    }


    public static class Mission5AlarmSite extends MapManager.GameObject {
        private Marker marker;
        private static final String NAME = "a Viral Plant motion detector";
        private static final String MAP_NAME = "Alarm";
        private static final String MISSION5_ALARM_TRIGGERED_EVENT = "MISSION5_ALARM_TRIGGERED_EVENT";
        private boolean triggered = false;

        public Mission5AlarmSite(MapManager mm, LatLng pos) {
            super(mm, NAME, pos);
        }

        protected synchronized void clearMarkerState() {
            marker = null;
        }

        @Override
        protected synchronized void removeMarker() {
            if (marker != null) marker.remove();
        }

        protected synchronized void drawMarker(GoogleMap map) {
            if (marker == null) {
                MarkerOptions mo = new MarkerOptions().position(getPosition()).visible(true);
                marker = map.addMarker(mo);
            } else {
                marker.setPosition(getPosition());
            }

            if (marker != null) {
                IconGenerator ig = new IconGenerator(ctx);
                ig.setColor(Color.RED);
                Bitmap icon = ig.makeIcon(MAP_NAME);
                marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
            }
        }

        @Override
        protected boolean hasApproachActivity() {
            return !triggered;
        }

        @Override
        protected void approach() {
            story.processEvent(MISSION5_ALARM_TRIGGERED_EVENT);
            triggered = true;
        }
    }

    public static class Mission5CaptureSite extends CaptureSite {

        private static final int CAPTURE_CRED = 80;

        public Mission5CaptureSite(MapManager mm, LatLng latLng) {
            super(mm, "the Viral Plant temperature regulator", latLng);
        }

        @Override
        protected CharSequence getCaptureSiteMapName() {
            return "Regulator";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameBeforeCapture() {
            return "the Viral Plant temperature regulator";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameAfterCapture() {
            return "a disabled temperature regulator";
        }

        @Override
        protected CharSequence getCaptureSiteCaptureSpeech() {
            return "You disabled the temperature regulator. Analyzing plant performance. Temperature levels rising. Self destruction estimated in 5 minutes. Get away fast.";
        }

        @Override
        protected CharSequence getCaptureSiteNotEnoughResourcesSpeech() {
            return "You need " + getCaptureSiteCaptureCost() + " Vine Cred to disable the temperature regulator.";
        }

        @Override
        protected int getCaptureSiteCaptureCost() {
            return CAPTURE_CRED;
        }

        @Override
        protected void setCaptured(boolean b) {
            super.setCaptured(b);
            if (b){
                disabled_regulator = true;
            }
        }
    }


    public static class Mission5CaptureSiteAlternate extends CaptureSite {

        private static final int CAPTURE_CRED = 80;

        public Mission5CaptureSiteAlternate(MapManager mm, LatLng latLng) {
            super(mm, "the Viral Plant mainframe", latLng);
        }

        @Override
        protected CharSequence getCaptureSiteMapName() {
            return "Mainframe";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameBeforeCapture() {
            return "the Viral Plant mainframe";
        }

        @Override
        protected CharSequence getCaptureSiteSpokenNameAfterCapture() {
            return "a hacked mainframe";
        }

        @Override
        protected CharSequence getCaptureSiteCaptureSpeech() {
            return "You hacked the mainframe. Analyzing plant viral load. Virus entering Oaken water supply. Incoming Oaken military forces in 5 minutes. Get away fast.";
        }

        @Override
        protected CharSequence getCaptureSiteNotEnoughResourcesSpeech() {
            return "You need " + getCaptureSiteCaptureCost() + " Vine Cred to hack the mainframe.";
        }

        @Override
        protected int getCaptureSiteCaptureCost() {
            return CAPTURE_CRED;
        }

        @Override
        protected void setCaptured(boolean b) {
            super.setCaptured(b);
            if (b){
                released_virus = true;
            }
        }
    }

    class MissionStoryManager extends StoryManager{
        private int numberCaptures = 0;
        private boolean winConditionMet = false;
        private boolean loseConditionMet = false;
        private float timeAlarmOut = -1;
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
            disabled_regulator = false;
            released_virus = false;
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
                    addSpeechToQueue("" + (int)(minutesBefore*timeAnnounceResolution/60.0f) + " minutes until mission abort.");
                }
            }
        }

        @Override
        void eventReceived(String event) {
            switch (event) {
                case CaptureSite.EVENT_CAPTURE_SITE_CAPTURED:
                    numberCaptures++;
                    timeAlarmOut = 5.0f*60.0f;
                    break;
                case DropSite.EVENT_DROP_SITE_ACTIVATED:
                    if (numberCaptures >= NUMBER_OF_CAPTURES_WIN){
                        winConditionMet = true;
                        interruptQueueWithSpeech(getEndSpeech() + " Holly Copter inbound for tree-vac. I'm sending the debriefing to your mobile device.");
                    }
                    else interruptQueueWithSpeech("We can't evacuate you yet, Almond. The viral plant must be disabled.");
                    break;
                case Mission5AlarmSite.MISSION5_ALARM_TRIGGERED_EVENT:
                    addSpeechToQueue("I detect multiple alarms triggered in the plant as you enter. Guards are on alert and military forces are inbound.");
                    timeAlarmOut = 30.0f*60.0f;
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

    private String getEndSpeech() {
        if (released_virus && disabled_regulator) {
            return "Viral plant load detected outbound to Oaken Empire. Alert. Viral plant temperatures reaching critical. Unexpected mission result. Analyzing outcome.";
        } else if (released_virus){
            return "Viral plant load detected outbound to Oaken Empire. Estimated casualties. One hundred thousand per day. Oaken Empire estimated total collapse. Two weeks.";
        } else if (disabled_regulator){
            return "Alert. Viral Plant temperatures reaching critical. Prediction. Complete destruction of viral plant and viral load under intense heat.";
        } else {
            throw new RuntimeException("Success Debriefing requested without any success conditions met.");
        }
    }
}

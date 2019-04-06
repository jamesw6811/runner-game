package jamesw6811.secrets.gameworld.chase;

import jamesw6811.secrets.gameworld.difficulty.DifficultySettings;
import jamesw6811.secrets.gameworld.story.StoryManager;
import jamesw6811.secrets.sound.ToneRunner;

public class ChaseManager {
    private boolean chaseHappening = false;
    private boolean chaseFlee = false;
    private double chaseDistance = 0;
    private double chaseDistanceWin = 0;
    private double chaseDistanceLose = 0;
    private double chaseSpeed = 0;
    private ChaseOriginator chaseSite = null;
    private DifficultySettings difficulty;
    private ToneRunner toner;
    private StoryManager story;

    public ChaseManager(DifficultySettings d, StoryManager story, ToneRunner tone){
        difficulty = d;
        toner = tone;
        this.story = story;
    }

    /** Starts a chase.
     * @param flee Sets rather or not the chase is running away or running after something.
     * @param chase_difficulty_mod Sets the difficulty of the chase as a multiplier on average speed (1=average speed)
     * @param chaseSite Sets the callback for chase-related handling
     * @return true if the chase is started successfully, false otherwise
     */
    public boolean startChase(boolean flee, double chase_difficulty_mod, ChaseOriginator chaseSite) {
        if (chaseHappening) return false;

        chaseDistance = 0;
        this.chaseSite = chaseSite;
        chaseFlee = flee;
        chaseSpeed = difficulty.getChaseDefaultSpeedMetersPerSecond()*chase_difficulty_mod;
        chaseDistanceWin = difficulty.getChaseDefaultDistanceMeters();
        chaseDistanceLose = -difficulty.getChaseDefaultDistanceFailMeters();
        chaseHappening = true;
        return true;
    }

    /** Simulates a chase timestep.
     * @param timeDelta The amount of time that passed this timestep.
     * @param lastDistanceTravelled the distance travelled by the chase protagonist.
     * @return true if the chase is started successfully, false otherwise
     */
    public void handleChase(float timeDelta, double lastDistanceTravelled){
        if (!chaseHappening) return;

        chaseDistance += lastDistanceTravelled - timeDelta*chaseSpeed;
        if (chaseDistance > chaseDistanceWin) {
            chaseHappening = false;
            chaseSite.chaseSuccessful();
        } else if (chaseDistance < chaseDistanceLose) {
            chaseHappening = false;
            chaseSite.chaseFailed();
        }

        if (isChaseHappening()) {
            double navDistance = getNavigationDistance();
            toner.playTone(navTonePeriodForDistance(navDistance));
            story.doChaseAnnouncements(getChaseMessage());
        } else {
            story.resetChaseAnnouncement();
            toner.stopTone();
        }
    }

    private int navTonePeriodForDistance(double distance){
        return (int)Math.round(distance*difficulty.getNavBeepPeriodMultiplier());
    }

    public boolean isChaseHappening() {
        return chaseHappening;
    }

    public CharSequence getChaseMessage() {
        return chaseSite.getChaseMessage();
    }

    public double getNavigationDistance(){
        if (!isChaseHappening()) throw new RuntimeException("Chase is not happening, cannot get distance.");

        if (chaseFlee) {
            return chaseDistance - chaseDistanceLose;
        } else {
            return chaseDistanceWin - chaseDistance;
        }
    }
}

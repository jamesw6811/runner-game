package jamesw6811.secrets.gameworld.story;

import android.content.Context;

import java.security.InvalidParameterException;
import java.util.Random;

import jamesw6811.secrets.sound.TextToSpeechRunner;

public abstract class StoryMission {
    public static String EXTRA_MISSION = StoryMission.class.getCanonicalName() + ".EXTRA_MISSION";
    abstract public StoryManager buildStoryManager(Context c, TextToSpeechRunner tts, Random random);
    abstract public String getName();
    abstract public String getBriefing();
    abstract public String getSuccessDebriefing();
    abstract public String getFailureDebriefing();
    abstract public void doRewardsAndUnlocks(Context c);
    public static StoryMission getMission(int x){
        switch (x){
            case 1:
                return new StoryMission1();
            case 2:
                return new StoryMission2();
            case 3:
                return new StoryMission3();
            case 4:
                return new StoryMission4();
            case 5:
                return new StoryMission5();
        }
        throw new InvalidParameterException("No such mission:" + x);
    }
}

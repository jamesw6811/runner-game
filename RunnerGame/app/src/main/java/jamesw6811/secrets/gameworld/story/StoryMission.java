package jamesw6811.secrets.gameworld.story;

import android.content.Context;

import java.util.Random;

import jamesw6811.secrets.sound.TextToSpeechRunner;

public abstract class StoryMission {
    abstract public StoryManager buildStoryManager(Context c, TextToSpeechRunner tts, Random random);
    abstract public String getBriefing();
    abstract public String getSuccessDebriefing();
    abstract public String getFailureDebriefing();
    abstract public void doRewardsAndUnlocks(Context c);
    public static StoryMission getMission(int x){
        switch (x){
            case 1:
                return new StoryMission1();
        }
        return null;
    }
}

package jamesw6811.secrets.gameworld.map;

import jamesw6811.secrets.RunMapActivity;
import jamesw6811.secrets.gameworld.story.GameResult;

public interface GameUIUpdateProcessor {
    boolean processMapUpdate(RunMapActivity.MapUpdate mu);
    void finishAndDebrief(GameResult gr);
}

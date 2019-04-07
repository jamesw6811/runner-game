package jamesw6811.secrets.gameworld.map;

import jamesw6811.secrets.RunMapActivity;

public interface GameUIUpdateProcessor {
    boolean processMapUpdate(RunMapActivity.MapUpdate mu);

    void finishAndDebrief();
}

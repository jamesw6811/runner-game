package jamesw6811.secrets.gameworld.difficulty;

import android.util.Log;

public class DifficultySettings {
    private static final String LOGTAG = DifficultySettings.class.getName();

    private double metersPerRunningResource = 50;
    private double metersInSight = 50;
    private double metersDiscoveryMinimum = 130;
    private double chaseDefaultDistanceMeters = 35; //meters to outrun other runner
    private double chaseDefaultDistanceFailMeters = 35; //meters for other runner to outrun you
    private double chaseDefaultSpeedMetersPerSecond = 3; //meters per second of racer at baseline, 9 mine mile, average jog speed
    private double navBeepPeriodMultiplier = 2500.0 / 300.0; // millis period per meter
    private static final double PACE_BASELINE = 9; //minutes per mile baseline for setting speeds based on pace, all settings scale off of this number.
    private static final double PACE_MINIMUM = 1;

    public DifficultySettings(){

    }

    // Alter defaults based on pace settings
    public void setPaceSettings(double pace) {
        if (pace < PACE_MINIMUM) throw new RuntimeException("Pace too low");
        double paceModifier = pace/PACE_BASELINE; // unitless pace ratio, higher = slower than baseline; lower = faster than baseline
        Log.d(LOGTAG, "Pace modifier:" + paceModifier);
        metersPerRunningResource = metersPerRunningResource /paceModifier;
        metersInSight = metersInSight /paceModifier;
        metersDiscoveryMinimum = metersDiscoveryMinimum /paceModifier;
        chaseDefaultDistanceMeters = chaseDefaultDistanceMeters /paceModifier;
        chaseDefaultDistanceFailMeters = chaseDefaultDistanceFailMeters /paceModifier;
        chaseDefaultSpeedMetersPerSecond = chaseDefaultSpeedMetersPerSecond /paceModifier;
        navBeepPeriodMultiplier = navBeepPeriodMultiplier *paceModifier;
    }

    public double getMetersPerRunningResource() {
        return metersPerRunningResource;
    }

    public double getMetersInSight() {
        return metersInSight;
    }

    public double getMetersDiscoveryMinimum() {
        return metersDiscoveryMinimum;
    }

    public double getChaseDefaultDistanceMeters() {
        return chaseDefaultDistanceMeters;
    }

    public double getChaseDefaultDistanceFailMeters() {
        return chaseDefaultDistanceFailMeters;
    }

    public double getChaseDefaultSpeedMetersPerSecond() {
        return chaseDefaultSpeedMetersPerSecond;
    }

    public double getNavBeepPeriodMultiplier() {
        return navBeepPeriodMultiplier;
    }
}

package jameswrunner.runnergame.gameworld;

/**
 * Created by james on 6/17/2017.
 */

public class GameHeading {
    public float headingDegrees;

    public GameHeading(float headingDegs){
        headingDegrees = headingDegs;
    }

    public float getHeadingRadians(){
        return (float)(headingDegrees/180f*Math.PI);
    }
}

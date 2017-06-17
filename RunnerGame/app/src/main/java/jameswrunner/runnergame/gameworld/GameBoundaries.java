package jameswrunner.runnergame.gameworld;

import android.graphics.PointF;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static jameswrunner.runnergame.maputils.MapUtilities.gameHeadingToMapHeading;
import static jameswrunner.runnergame.maputils.MapUtilities.mapHeadingToGameHeading;

/**
 * Created by james on 6/17/2017.
 */

public class GameBoundaries {
    protected LatLng bottomLeft;
    private List<LatLng> corners;
    protected double offsetAngle;
    public double height;
    public double width;
    public enum DIRECTION
    {
        //You can initialize enums using enumname(value)
        WEST(0),
        NORTH(1),
        EAST(2),
        SOUTH(3);

        private int direction;
        //Constructor which will initialize the enum
        DIRECTION(int dir)
        {
            direction = dir;
        }
        //method to return the direction set by the user which initializing the enum
        public int GetDirection()
        {
            return direction;
        }
    }
    public static final String LOGTAG = "GameBoundaries";

    public GameBoundaries(LatLng center, double offsetAngle, double height, double width){
        this.offsetAngle = offsetAngle;
        this.height = height;
        this.width = width;
        double aspectAngle = Math.atan2(height, width)*180/(Math.PI);
        double cornerDistance = Math.sqrt(Math.pow(height/2, 2) + Math.pow(width/2, 2));
        bottomLeft = SphericalUtil.computeOffset(center, cornerDistance,
                gameHeadingToMapHeading(aspectAngle+180+offsetAngle));
        initializeCorners();
    }

    private void initializeCorners(){
        corners = new LinkedList<LatLng>();
        corners.add(bottomLeft);
        corners.add(gamePointtoLatLng(new GamePoint(0, (float)height)));
        corners.add(gamePointtoLatLng(new GamePoint((float)width, (float)height)));
        corners.add(gamePointtoLatLng(new GamePoint((float)width, 0)));
    }

    public GamePoint latLngtoGamePoint(LatLng ll){
        double distance = SphericalUtil.computeDistanceBetween(bottomLeft, ll);
        double heading = mapHeadingToGameHeading(SphericalUtil.computeHeading(bottomLeft, ll)) - offsetAngle;
        double xpos = distance*Math.cos(heading*Math.PI/180);
        double ypos = distance*Math.sin(heading*Math.PI/180);
        return new GamePoint((float)xpos, (float)ypos);
    }

    public LatLng gamePointtoLatLng(GamePoint gp){
        double distance = gp.length();
        double heading = gameHeadingToMapHeading(Math.atan2(gp.y, gp.x)*180/(Math.PI) + offsetAngle);
        //Log.d(LOGTAG, "Distance: " + distance + " heading:"+heading+" GP:"+gp.toString());
        return SphericalUtil.computeOffset(bottomLeft, distance, heading);
    }

    public GamePoint getRandomPointInBounds(){
        Random r = new Random();
        return new GamePoint((float)(r.nextFloat()*height), (float)(r.nextFloat()*width));
    }

    public GamePoint getRandomPointOnBoundary(DIRECTION d){
        GamePoint gp = getRandomPointInBounds();
        switch(d){
            case EAST:
                gp.x = (float)width;
                break;
            case WEST:
                gp.x = 0;
                break;
            case NORTH:
                gp.y = (float)height;
                break;
            case SOUTH:
                gp.y = 0;
                break;
        }
        return gp;
    }

    public float angleOfDirection(DIRECTION d){
        switch(d){
            case EAST:
                return 0;
            case WEST:
                return 180;
            case NORTH:
                return 90;
            case SOUTH:
                return 270;
            default:
                throw new RuntimeException("No such direction");
        }
    }

    public GameHeading getSemiRandomHeading(DIRECTION d){
        Random rand = new Random();
        return new GameHeading(angleOfDirection(d) - 45 + rand.nextFloat()*90f);
    }

    public boolean withinBounds(GamePoint p){
        if (p.x>=0 && p.x <= width && p.y >= 0 && p.y <= height){
            return true;
        } else {
            return false;
        }
    }

    public Iterable<LatLng> getLatLngCorners() {
        return corners;
    }

    public double crowDistance(GamePoint gp1, GamePoint gp2){
        return Math.sqrt(Math.pow(gp1.x-gp2.x,2) + Math.pow(gp1.y-gp2.y, 2));
    }

}

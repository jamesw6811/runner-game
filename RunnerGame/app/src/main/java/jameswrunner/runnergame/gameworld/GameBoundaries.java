package jameswrunner.runnergame.gameworld;

import com.google.android.gms.maps.model.LatLng;
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
    public static final String LOGTAG = "GameBoundaries";
    public final double height;
    public final double width;
    protected final LatLng bottomLeft;
    protected final double offsetAngle;
    private final List<LatLng> corners;
    private final LatLng center;

    public GameBoundaries(LatLng center, double offsetAngle, double height, double width) {
        this.center = center;
        this.offsetAngle = offsetAngle;
        this.height = height;
        this.width = width;
        double aspectAngle = Math.atan2(height, width) * 180 / (Math.PI);
        double cornerDistance = Math.sqrt(Math.pow(height / 2, 2) + Math.pow(width / 2, 2));
        bottomLeft = SphericalUtil.computeOffset(center, cornerDistance,
                gameHeadingToMapHeading(aspectAngle + 180 + offsetAngle));
        corners = new LinkedList<LatLng>();
        corners.add(bottomLeft);
        corners.add(gamePointtoLatLng(new GamePoint(0, (float) height)));
        corners.add(gamePointtoLatLng(new GamePoint((float) width, (float) height)));
        corners.add(gamePointtoLatLng(new GamePoint((float) width, 0)));
    }


    public GamePoint latLngtoGamePoint(LatLng ll) {
        double distance = SphericalUtil.computeDistanceBetween(bottomLeft, ll);
        double heading = mapHeadingToGameHeading(SphericalUtil.computeHeading(bottomLeft, ll)) - offsetAngle;
        double xpos = distance * Math.cos(heading * Math.PI / 180);
        double ypos = distance * Math.sin(heading * Math.PI / 180);
        return new GamePoint((float) xpos, (float) ypos);
    }

    public LatLng gamePointtoLatLng(GamePoint gp) {
        double distance = gp.length();
        double heading = gameHeadingToMapHeading(Math.atan2(gp.y, gp.x) * 180 / (Math.PI) + offsetAngle);
        //Log.d(LOGTAG, "Distance: " + distance + " heading:"+heading+" GP:"+gp.toString());
        return SphericalUtil.computeOffset(bottomLeft, distance, heading);
    }

    public GamePoint getRandomPointInBounds() {
        Random r = new Random();
        return new GamePoint((float) (r.nextFloat() * height), (float) (r.nextFloat() * width));
    }

    public GamePoint getRandomPointOnBoundary(DIRECTION d) {
        GamePoint gp = getRandomPointInBounds();
        switch (d) {
            case EAST:
                gp.x = (float) width;
                break;
            case WEST:
                gp.x = 0;
                break;
            case NORTH:
                gp.y = (float) height;
                break;
            case SOUTH:
                gp.y = 0;
                break;
        }
        return gp;
    }

    public float angleOfDirection(DIRECTION d) {
        switch (d) {
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

    public DIRECTION directionOfAngle(float d) {
        float d_mod = d%360;
        if (d_mod < 0) d_mod += 360;
        int d_round = (int)Math.round(d_mod/45.0);

        switch (d_round) {
            case 0:
                return DIRECTION.EAST;
            case 1:
                return DIRECTION.NORTHEAST;
            case 2:
                return DIRECTION.NORTH;
            case 3:
                return DIRECTION.NORTHWEST;
            case 4:
                return DIRECTION.WEST;
            case 5:
                return DIRECTION.SOUTHWEST;
            case 6:
                return DIRECTION.SOUTH;
            case 7:
                return DIRECTION.SOUTHEAST;
            case 8:
                return DIRECTION.EAST;
            default:
                throw new RuntimeException("No such direction angle:" + d);
        }
    }

    public float angleBetweenPoints(GamePoint from, GamePoint to){
        return (float)(Math.atan2(to.y-from.y, to.x-from.x) * 180 / (Math.PI));
    }

    public GameHeading getSemiRandomHeading(DIRECTION d) {
        Random rand = new Random();
        return new GameHeading(angleOfDirection(d) - 45 + rand.nextFloat() * 90f);
    }

    public boolean withinBounds(GamePoint p) {
        if (p.x >= 0 && p.x <= width && p.y >= 0 && p.y <= height) {
            return true;
        } else {
            return false;
        }
    }

    public Iterable<LatLng> getLatLngCorners() {
        return corners;
    }

    public double crowDistance(GamePoint gp1, GamePoint gp2) {
        return Math.sqrt(Math.pow(gp1.x - gp2.x, 2) + Math.pow(gp1.y - gp2.y, 2));
    }

    public LatLng getCenter() {
        return center;
    }

    public enum DIRECTION {
        //You can initialize enums using enumname(value)
        WEST(0, "west", 180),
        NORTH(1, "north", 90),
        EAST(2, "east", 0),
        SOUTH(3, "south", 270),
        NORTHWEST(4, "northwest", 135),
        NORTHEAST(5, "northeast", 45),
        SOUTHWEST(6, "southwest", 225),
        SOUTHEAST(7, "southeast", 315);

        private int direction;
        private String name;
        private float angle;

        //Constructor which will initialize the enum
        DIRECTION(int dir, String n, float a) {
            direction = dir;
            name = n;
            angle = a;
        }

        public String getName() {
            return name;
        }
        public float getAngle() {
            return angle;
        }
    }

}

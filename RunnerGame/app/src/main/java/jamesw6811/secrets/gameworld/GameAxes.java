package jamesw6811.secrets.gameworld;


/**
 * Created by james on 6/17/2017.
 */

public class GameAxes {
    public static final String LOGTAG = "GameAxes";

    public GameAxes() {

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

        public DIRECTION directionOfAngle(float d) {
            float d_mod = d % 360;
            if (d_mod < 0) d_mod += 360;
            int d_round = (int) Math.round(d_mod / 45.0);

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
    }

}

package jameswrunner.runnergame.maputils;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.android.SphericalUtil;
import com.google.maps.model.SnappedPoint;

import java.util.Random;

import jameswrunner.runnergame.R;

/**
 * Created by james on 6/17/2017.
 */

public class MapUtilities {

    public static LatLng getRandomDistantPosition(LatLng pos, float distanceMeters) {
        float randomHeading = getRandomHeading();
        LatLng newLatLng = SphericalUtil.computeOffset(pos, distanceMeters, randomHeading);
        return newLatLng;
    }

    public static LatLng locationToLatLng(Location l) {
        LatLng ll = new LatLng(l.getLatitude(), l.getLongitude());
        return ll;
    }

    public static float getRandomHeading() {
        Random r = new Random();
        return r.nextFloat() * 360f;
    }

    public static double mapHeadingToGameHeading(double mapheading) {
        return -mapheading + 90f;
    }

    public static double gameHeadingToMapHeading(double gameheading) {
        return -gameheading + 90f;
    }

}

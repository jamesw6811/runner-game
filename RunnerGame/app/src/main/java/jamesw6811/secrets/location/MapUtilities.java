package jamesw6811.secrets.location;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Random;

/**
 * Created by james on 6/17/2017.
 */

public class MapUtilities {


    public static LatLng locationToLatLng(Location l) {
        LatLng ll = new LatLng(l.getLatitude(), l.getLongitude());
        return ll;
    }

    public static float getRandomHeading() {
        Random r = new Random();
        return r.nextFloat() * 360f;
    }

    public static LatLng getRandomDistantPosition(LatLng pos, float distanceMeters) {
        float randomHeading = getRandomHeading();
        LatLng newLatLng = SphericalUtil.computeOffset(pos, distanceMeters, randomHeading);
        return newLatLng;
    }


}

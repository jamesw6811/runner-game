package jamesw6811.secrets.gameworld.map.site;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import jamesw6811.secrets.gameworld.map.MapManager;

public class TrapSite extends MapManager.GameObject {
    private Marker marker;

    public TrapSite(MapManager mm, LatLng position) {
        super(mm, "a Venus Trap", position);
    }

    @Override
    protected void drawMarker(GoogleMap map) {
        if (marker == null) {
            MarkerOptions mo = new MarkerOptions().position(getPosition()).visible(true);
            marker = map.addMarker(mo);
        } else {
            marker.setPosition(getPosition());
        }

        if (marker != null) {
            IconGenerator ig = new IconGenerator(ctx);
            ig.setColor(Color.RED);
            Bitmap icon = ig.makeIcon("Trap");
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    protected void clearMarkerState() {
        marker = null;
    }

    @Override
    protected void removeMarker() {
        if (marker != null) marker.remove();
    }

    @Override
    protected boolean hasApproachActivity() {
        return true;
    }

    @Override
    protected void approach() {
        if (chase.isChaseHappening()){
            story.interruptQueueWithSpeech("The Venus Trap caught the " + chase.getChaserName() + " and you get some Vine Cred!");
            chase.endChaseTrap();
            player.giveRunningResource(3 + player.getUpgradeLevelTrapSupporter()*3);
        }
    }
}

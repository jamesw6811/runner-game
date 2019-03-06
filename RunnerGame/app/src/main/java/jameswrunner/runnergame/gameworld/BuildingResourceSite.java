package jameswrunner.runnergame.gameworld;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import jameswrunner.runnergame.GameService;

/**
 * Created by james on 6/17/2017.
 */

public class BuildingResourceSite extends GameObject {
    private Marker marker;
    private boolean built = false;

    public BuildingResourceSite(GameWorld gw, LatLng pos) {
        super(gw, "a Spirit Tree", pos);
    }

    protected synchronized void clearMarkerState() {
        marker = null;
    }

    @Override
    protected synchronized void removeMarker() {
        if (marker != null) marker.remove();
    }

    protected synchronized void drawMarker(GameService gs, GoogleMap map) {
        if (marker == null) {
            MarkerOptions mo = new MarkerOptions().position(getPosition()).visible(true);
            marker = map.addMarker(mo);
        } else {
            marker.setPosition(getPosition());
        }

        if (marker != null) {
            IconGenerator ig = new IconGenerator(gs);
            if (built){
                ig.setColor(Color.GREEN);
            } else {
                ig.setColor(Color.BLACK);
            }
            Bitmap icon = ig.makeIcon("Tree");
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    public String getSpokenName(){
        if (!built) return super.getSpokenName();
        else return super.getSpokenName() + " with your Spirit Tap installed";
    }

    public void setBuilt(boolean b){
        built = b;
        updateMarker();
    }

    @Override
    public boolean isUpgradable() {
        if (!built) return true;
        return false;
    }

    @Override
    public void upgrade(Player player) {
        if (built) throw new UnsupportedOperationException("Cannot upgrade further.");
        if (player.getSpirits() >= 10) {
            player.takeSpirits(10);
            setBuilt(true);
            getGameWorld().speakTTS("You installed a Spirit Tap for 10 spirits.");
        } else {
            getGameWorld().speakTTS("You need at least 10 spirits to install a Spirit Tap.");
        }
    }
}

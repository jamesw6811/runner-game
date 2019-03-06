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
    private static final int MAX_RESOURCE = 10;
    private static final float RESOURCE_GENERATION_PERIOD = 30f;
    private Marker marker;
    private boolean built = false;
    private int resource = 0;
    private float timeSinceLastResource = 0;

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
        else return super.getSpokenName() + " with " + resource + " spirits in your Spirit Tap";
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
        if (player.getRunningResource() >= 10) {
            player.takeRunningResource(10);
            setBuilt(true);
            getGameWorld().speakTTS("You installed a Spirit Tap for 10 spirits. The tap will generate Ecto, a powerful spiritual essence, over time.");
        } else {
            getGameWorld().speakTTS("You need at least 10 spirits to install a Spirit Tap.");
        }
    }

    @Override
    public boolean isInteractable() {
        if (built) return true;
        else return false;
    }

    @Override
    public void interact(Player player) {
        if (resource > 0) {
            getGameWorld().speakTTS("You collected " + resource + " Ecto from the tap.");
            player.giveBuildingResource(resource);
            resource = 0;
        } else {
            getGameWorld().speakTTS("This Spirit Tree is tapped out for now. Come back later.");
        }
    }

    @Override
    public void tickTime(float timeDelta) {
        super.tickTime(timeDelta);
        if (built) {
            timeSinceLastResource += timeDelta;
            if (timeSinceLastResource > RESOURCE_GENERATION_PERIOD) {
                timeSinceLastResource -= RESOURCE_GENERATION_PERIOD;
                resource = Math.min(MAX_RESOURCE, resource + 1);
            }
        }
    }
}

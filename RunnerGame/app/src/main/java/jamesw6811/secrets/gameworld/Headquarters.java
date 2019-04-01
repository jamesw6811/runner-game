package jamesw6811.secrets.gameworld;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import jamesw6811.secrets.GameService;
import jamesw6811.secrets.R;

/**
 * Created by james on 6/17/2017.
 */

public class Headquarters extends GameObject implements ChaseOriginator {
    private static final int FIRST_UPGRADE_COST_SUB_RESOURCE = 1;
    private static final double CHASE_DIFFICULTY = 1.1;
    private Marker marker;
    public static final int RUNNING_RESOURCE_BUILD_COST = 5;

    public Headquarters(GameWorld gw, LatLng pos) {
        super(gw, gw.getGameService().getString(R.string.headquarters_spokenName), pos);
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
            ig.setColor(Color.GREEN);
            Bitmap icon = ig.makeIcon(gs.getString(R.string.headquarters_mapName));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    public boolean hasApproachActivity() {
        return true;
    }

    @Override
    public void approach() {
        Player player = getGameWorld().getPlayer();
        if (player.isInjured()) {
            player.fixInjury();
            getGameWorld().addSpeechToQueue(getGameWorld().getGameService().getString(R.string.headquarters_fixInjuries));
        }
    }

    @Override
    public boolean isUpgradable() {
        return true;
    }

    @Override
    public void upgrade() {
        Player player = getGameWorld().getPlayer();
        if (player.getBuildingSubResource() >= FIRST_UPGRADE_COST_SUB_RESOURCE) {
            player.takeBuildingSubResource(FIRST_UPGRADE_COST_SUB_RESOURCE);
            getGameWorld().startChase(false, CHASE_DIFFICULTY, this);
            getGameWorld().interruptQueueWithSpeech(getGameWorld().getGameService().getString(R.string.headquarters_chaseStarted));
        } else {
            getGameWorld().interruptQueueWithSpeech(getGameWorld().getGameService().getString(R.string.headquarters_upgradeNotEnoughResources, FIRST_UPGRADE_COST_SUB_RESOURCE));
        }
    }

    @Override
    public void chaseSuccessful() {
        getGameWorld().interruptQueueWithSpeech(getGameWorld().getGameService().getString(R.string.headquarters_chaseSuccess));
        getGameWorld().winCondition = true;
    }

    @Override
    public void chaseFailed() {
        getGameWorld().interruptQueueWithSpeech(getGameWorld().getGameService().getString(R.string.headquarters_chaseFailed));
    }

    @Override
    public CharSequence getChaseMessage() {
        return getGameWorld().getGameService().getString(R.string.headquarters_chase_message);
    }
}

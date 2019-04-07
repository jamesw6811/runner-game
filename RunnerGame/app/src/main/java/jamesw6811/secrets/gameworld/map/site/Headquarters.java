package jamesw6811.secrets.gameworld.map.site;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import jamesw6811.secrets.R;
import jamesw6811.secrets.gameworld.chase.ChaseOriginator;
import jamesw6811.secrets.gameworld.map.MapManager;

/**
 * Created by james on 6/17/2017.
 */

public class Headquarters extends MapManager.GameObject implements ChaseOriginator {
    public static final int RUNNING_RESOURCE_BUILD_COST = 5;
    private static final int FIRST_UPGRADE_COST_SUB_RESOURCE = 1;
    private static final double CHASE_DIFFICULTY = 1.1;
    private Marker marker;

    public Headquarters(MapManager mm, LatLng pos) {
        super(mm, mm.getContext().getString(R.string.headquarters_spokenName), pos);
    }

    protected synchronized void clearMarkerState() {
        marker = null;
    }

    @Override
    protected synchronized void removeMarker() {
        if (marker != null) marker.remove();
    }

    protected synchronized void drawMarker(GoogleMap map) {
        if (marker == null) {
            MarkerOptions mo = new MarkerOptions().position(getPosition()).visible(true);
            marker = map.addMarker(mo);
        } else {
            marker.setPosition(getPosition());
        }

        if (marker != null) {
            IconGenerator ig = new IconGenerator(ctx);
            ig.setColor(Color.GREEN);
            Bitmap icon = ig.makeIcon(ctx.getString(R.string.headquarters_mapName));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    protected boolean hasApproachActivity() {
        return true;
    }

    @Override
    protected void approach() {
        if (player.isInjured()) {
            player.fixInjury();
            story.addSpeechToQueue(ctx.getString(R.string.headquarters_fixInjuries));
        }
    }

    @Override
    protected boolean isUpgradable() {
        return true;
    }

    @Override
    protected void upgrade() {
        if (player.getBuildingSubResource() >= FIRST_UPGRADE_COST_SUB_RESOURCE) {
            player.takeBuildingSubResource(FIRST_UPGRADE_COST_SUB_RESOURCE);
            chase.startChase(false, CHASE_DIFFICULTY, this);
            story.interruptQueueWithSpeech(ctx.getString(R.string.headquarters_chaseStarted));
        } else {
            story.interruptQueueWithSpeech(ctx.getString(R.string.headquarters_upgradeNotEnoughResources, FIRST_UPGRADE_COST_SUB_RESOURCE));
        }
    }

    @Override
    public void chaseSuccessful() {
        story.interruptQueueWithSpeech(ctx.getString(R.string.headquarters_chaseSuccess));
        story.winCondition = true;
    }

    @Override
    public void chaseFailed() {
        story.interruptQueueWithSpeech(ctx.getString(R.string.headquarters_chaseFailed));
    }

    @Override
    public CharSequence getChaseMessage() {
        return ctx.getString(R.string.headquarters_chase_message);
    }
}

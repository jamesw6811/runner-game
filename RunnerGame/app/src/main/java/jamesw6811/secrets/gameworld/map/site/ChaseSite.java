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

public class ChaseSite extends MapManager.GameObject implements ChaseOriginator {
    private static final double CHASE_DIFFICULTY = 0.66; // 2/3rd the player pace
    private Marker marker;

    public ChaseSite(MapManager mm, LatLng position) {
        super(mm, mm.getContext().getString(R.string.chasesite_spokenName), position);
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
            Bitmap icon = ig.makeIcon(ctx.getString(R.string.chasesite_mapName));
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
        chase.startChase(true, CHASE_DIFFICULTY, this);
        story.interruptQueueWithSpeech(ctx.getString(R.string.chasesite_chaseStarted));
    }

    @Override
    public void chaseSuccessful() {
        story.addSpeechToQueue(ctx.getString(R.string.chasesite_chaseSuccess));
    }

    @Override
    public void chaseFailed() {
        story.interruptQueueWithSpeech(ctx.getString(R.string.chasesite_chaseFailed));
        player.injure();
    }

    @Override
    public CharSequence getChaseMessage() {
        return ctx.getString(R.string.chasesite_chaseMessage);
    }
}

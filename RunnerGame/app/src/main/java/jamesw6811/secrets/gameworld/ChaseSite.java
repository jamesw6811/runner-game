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

class ChaseSite extends GameObject implements ChaseOriginator {
    private static final double CHASE_DIFFICULTY = 0.66; // 2/3rd the player pace
    private Marker marker;
    ChaseSite(GameWorld gw, LatLng position) {
        super(gw, gw.getGameService().getString(R.string.chasesite_spokenName), position);
    }

    @Override
    void drawMarker(GameService gs, GoogleMap map) {
        if (marker == null) {
            MarkerOptions mo = new MarkerOptions().position(getPosition()).visible(true);
            marker = map.addMarker(mo);
        } else {
            marker.setPosition(getPosition());
        }

        if (marker != null) {
            IconGenerator ig = new IconGenerator(gs);
            ig.setColor(Color.RED);
            Bitmap icon = ig.makeIcon(gs.getString(R.string.chasesite_mapName));
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(icon));
        }
    }

    @Override
    void clearMarkerState() {
        marker = null;
    }

    @Override
    void removeMarker() {
        if (marker != null) marker.remove();
    }

    @Override
    boolean hasApproachActivity() {
        return true;
    }

    @Override
    void approach() {
        getGameWorld().startChase(true, CHASE_DIFFICULTY, this);
        getGameWorld().interruptQueueWithSpeech(getGameWorld().getGameService().getString(R.string.chasesite_chaseStarted));
    }

    @Override
    public void chaseSuccessful() {
        getGameWorld().addSpeechToQueue(getGameWorld().getGameService().getString(R.string.chasesite_chaseSuccess));
    }

    @Override
    public void chaseFailed() {
        getGameWorld().interruptQueueWithSpeech(getGameWorld().getGameService().getString(R.string.chasesite_chaseFailed));
        getGameWorld().getPlayer().injure();
    }

    @Override
    public CharSequence getChaseMessage() {
        return getGameWorld().getGameService().getString(R.string.chasesite_chaseMessage);
    }
}

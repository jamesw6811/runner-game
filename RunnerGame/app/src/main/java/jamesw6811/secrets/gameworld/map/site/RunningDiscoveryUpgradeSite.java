package jamesw6811.secrets.gameworld.map.site;

import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.gameworld.map.MapManager;

public class RunningDiscoveryUpgradeSite extends UpgradeSite {
    public static final int UPGRADE_CRED = 3;

    public RunningDiscoveryUpgradeSite(MapManager mm, LatLng latLng) {
        super(mm, "a Mappy Maple upgrade", latLng);
    }

    @Override
    protected CharSequence getUpgradeSiteMapName() {
        return "Mappy";
    }

    @Override
    protected CharSequence getUpgradeSiteSpokenNameBeforeUpgrade() {
        return "a Mappy Maple upgrade";
    }

    @Override
    protected CharSequence getUpgradeSiteSpokenNameAfterUpgrade() {
        return "a used Mappy Maple";
    }

    @Override
    protected CharSequence getUpgradeSiteUpgradeSpeech() {
        return "You upgraded your map with the Mappy Maple. You now get additional Vine Cred for running to new places.";
    }

    @Override
    protected CharSequence getUpgradeSiteNotEnoughResourcesSpeech() {
        return "You need " + UPGRADE_CRED + " Vine Cred to upgrade.";
    }

    @Override
    protected int getUpgradeSiteUpgradeCost() {
        return UPGRADE_CRED;
    }

    @Override
    protected void doUpgrade() {
        player.setUpgradeLevelDiscoverySupporter(player.getUpgradeLevelDiscoverySupporter() + 1);
    }
}

package jamesw6811.secrets.gameworld.map.site;

import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.gameworld.map.MapManager;

public class RunningLapUpgradeSite extends UpgradeSite {
    public static final int UPGRADE_CRED = 3;

    public RunningLapUpgradeSite(MapManager mm, LatLng latLng) {
        super(mm, "a Sappy Shoes upgrade", latLng);
    }

    @Override
    protected CharSequence getUpgradeSiteMapName() {
        return "Shoes";
    }

    @Override
    protected CharSequence getUpgradeSiteSpokenNameBeforeUpgrade() {
        return "a Sappy Shoes upgrade";
    }

    @Override
    protected CharSequence getUpgradeSiteSpokenNameAfterUpgrade() {
        return "used Sappy Shoes";
    }

    @Override
    protected CharSequence getUpgradeSiteUpgradeSpeech() {
        return "You upgraded your roots with the Sappy Shoes. You now get additional Vine Cred for retracing your steps.";
    }

    @Override
    protected int getUpgradeSiteUpgradeCost() {
        return UPGRADE_CRED + player.getUpgradeLevelLapSupporter()*3;
    }

    @Override
    protected void doUpgrade() {
        player.setUpgradeLevelLapSupporter(player.getUpgradeLevelLapSupporter() + 1);
    }
}

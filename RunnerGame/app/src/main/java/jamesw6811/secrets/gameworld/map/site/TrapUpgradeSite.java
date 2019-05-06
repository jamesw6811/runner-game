package jamesw6811.secrets.gameworld.map.site;

import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.gameworld.map.MapManager;

public class TrapUpgradeSite extends UpgradeSite {
    public static final int UPGRADE_CRED = 3;

    public TrapUpgradeSite(MapManager mm, LatLng latLng) {
        super(mm, "a Venus Trap Booster", latLng);
    }

    @Override
    protected CharSequence getUpgradeSiteMapName() {
        return "V-Boost";
    }

    @Override
    protected CharSequence getUpgradeSiteSpokenNameBeforeUpgrade() {
        return "a Venus Trap booster upgrade";
    }

    @Override
    protected CharSequence getUpgradeSiteSpokenNameAfterUpgrade() {
        return "used Venus booster";
    }

    @Override
    protected CharSequence getUpgradeSiteUpgradeSpeech() {
        return "You upgraded your Venus Traps. You now get additional Vine Cred for trapping your pursuers.";
    }

    @Override
    protected int getUpgradeSiteUpgradeCost() {
        return UPGRADE_CRED + player.getUpgradeLevelTrapSupporter()*3;
    }

    @Override
    protected void doUpgrade() {
        player.setUpgradeLevelTrapSupporter(player.getUpgradeLevelTrapSupporter() + 1);
    }
}

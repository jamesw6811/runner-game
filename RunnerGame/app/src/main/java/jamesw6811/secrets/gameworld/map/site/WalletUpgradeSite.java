package jamesw6811.secrets.gameworld.map.site;

import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.gameworld.map.MapManager;

public class WalletUpgradeSite extends UpgradeSite {
    public static final int UPGRADE_CRED = 3;

    public WalletUpgradeSite(MapManager mm, LatLng latLng) {
        super(mm, "a Vine Vault Upgrade", latLng);
    }

    @Override
    protected CharSequence getUpgradeSiteMapName() {
        return "Vault";
    }

    @Override
    protected CharSequence getUpgradeSiteSpokenNameBeforeUpgrade() {
        return "a Vine Vault vine cred upgrade";
    }

    @Override
    protected CharSequence getUpgradeSiteSpokenNameAfterUpgrade() {
        return "used Vine Vault";
    }

    @Override
    protected CharSequence getUpgradeSiteUpgradeSpeech() {
        return "You upgraded a Vine Vault, now you can hold up to " + player.getResourceCapacity() + " Vine Cred.";
    }

    @Override
    protected int getUpgradeSiteUpgradeCost() {
        return UPGRADE_CRED + player.getUpgradeLevelCapacitySupporter()*3;
    }

    @Override
    protected void doUpgrade() {
        player.setUpgradeLevelCapacitySupporter(player.getUpgradeLevelCapacitySupporter() + 1);
    }
}

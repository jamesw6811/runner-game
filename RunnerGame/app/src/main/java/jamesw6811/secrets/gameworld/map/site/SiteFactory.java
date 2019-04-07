package jamesw6811.secrets.gameworld.map.site;

import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.gameworld.map.MapManager;

public class SiteFactory {
    public static MapManager.GameObject getSite(Class cls, MapManager mapManager, LatLng position){
        if (cls == null) return null;

        if (cls == BuildingResourceSite.class){
            return new BuildingResourceSite(mapManager, position);
        } else if (cls == BuildingSubResourceSite.class){
            return new BuildingSubResourceSite(mapManager, position);
        } else if (cls == ChaseSite.class){
            return new ChaseSite(mapManager, position);
        } else if (cls == Headquarters.class){
            return new Headquarters(mapManager, position);
        } else throw new UnsupportedOperationException("That site is not registered with SiteFactory.");
    }
}

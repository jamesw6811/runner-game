package jamesw6811.secrets.gameworld.map.site;

import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.gameworld.map.MapManager;
import jamesw6811.secrets.gameworld.story.StoryMission1;

public class SiteFactory {
    // TODO: Consider instead using self-registering Supplier subclasses in each Site.
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
        } else if (cls == DropSite.class){
            return new DropSite(mapManager, position);
        } else if (cls == EmptySite.class){
            return new EmptySite(mapManager, position);
        } else if (cls == RunningLapUpgradeSite.class){
            return new RunningLapUpgradeSite(mapManager, position);
        } else if (cls == RunningDiscoveryUpgradeSite.class){
            return new RunningDiscoveryUpgradeSite(mapManager, position);
        } else if (cls == StoryMission1.Mission1AlarmCaptureSite.class){
            return new RunningDiscoveryUpgradeSite(mapManager, position);
        } else throw new UnsupportedOperationException("That site is not registered with SiteFactory.");
    }
}

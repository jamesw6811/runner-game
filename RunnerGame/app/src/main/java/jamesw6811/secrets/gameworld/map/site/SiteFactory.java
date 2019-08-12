package jamesw6811.secrets.gameworld.map.site;

import com.google.android.gms.maps.model.LatLng;

import jamesw6811.secrets.gameworld.map.MapManager;
import jamesw6811.secrets.gameworld.story.StoryMission1;
import jamesw6811.secrets.gameworld.story.StoryMission2;
import jamesw6811.secrets.gameworld.story.StoryMission3;
import jamesw6811.secrets.gameworld.story.StoryMission4;
import jamesw6811.secrets.gameworld.story.StoryMission5;

public class SiteFactory {
    // TODO: Consider instead using self-registering Supplier subclasses in each Site.
    public static MapManager.GameObject getSite(Class cls, MapManager mapManager, LatLng position){
        if (cls == null) return null;

        // Standard sites:
        if (cls == BuildingResourceSite.class){
            return new BuildingResourceSite(mapManager, position);

        } else if (cls == DropSite.class){
            return new DropSite(mapManager, position);

        } else if (cls == TrapSite.class){
            return new TrapSite(mapManager, position);

        } else if (cls == EmptySite.class){
            return new EmptySite(mapManager, position);

        } else if (cls == RunningLapUpgradeSite.class){
            return new RunningLapUpgradeSite(mapManager, position);

        } else if (cls == TrapUpgradeSite.class){
            return new TrapUpgradeSite(mapManager, position);

        } else if (cls == RunningDiscoveryUpgradeSite.class){
            return new RunningDiscoveryUpgradeSite(mapManager, position);

        } else if (cls == WalletUpgradeSite.class){
            return new WalletUpgradeSite(mapManager, position);

        } else if (cls == HazardSite.class){
            return new HazardSite(mapManager, position);

        // Mission-specific sites:
        } else if (cls == StoryMission1.Mission1AlarmCaptureSite.class){
            return new StoryMission1.Mission1AlarmCaptureSite(mapManager, position);

        } else if (cls == StoryMission2.Mission2AlarmCaptureSite.class){
            return new StoryMission2.Mission2AlarmCaptureSite(mapManager, position);

        } else if (cls == StoryMission2.Mission2FinalCaptureSite.class){
            return new StoryMission2.Mission2FinalCaptureSite(mapManager, position);

        } else if (cls == StoryMission2.Mission2GuardSite.class){
            return new StoryMission2.Mission2GuardSite(mapManager, position);

        } else if (cls == StoryMission3.Mission3CaptureSite.class){
            return new StoryMission3.Mission3CaptureSite(mapManager, position);

        } else if (cls == StoryMission3.Mission3GuardSite.class){
            return new StoryMission3.Mission3GuardSite(mapManager, position);

        } else if (cls == StoryMission4.Mission4GuardSite.class){
            return new StoryMission4.Mission4GuardSite(mapManager, position);

        } else if (cls == StoryMission4.Mission4CaptureSite.class){
            return new StoryMission4.Mission4CaptureSite(mapManager, position);

        } else if (cls == StoryMission5.Mission5CaptureSite.class){
            return new StoryMission5.Mission5CaptureSite(mapManager, position);

        } else if (cls == StoryMission5.Mission5CaptureSiteAlternate.class){
            return new StoryMission5.Mission5CaptureSiteAlternate(mapManager, position);

        } else if (cls == StoryMission5.Mission5GuardSite.class){
            return new StoryMission5.Mission5GuardSite(mapManager, position);

        } else if (cls == StoryMission5.Mission5AlarmSite.class){
            return new StoryMission5.Mission5AlarmSite(mapManager, position);

        } else throw new UnsupportedOperationException("That site is not registered with SiteFactory. Class:" + cls.getName());
    }
}

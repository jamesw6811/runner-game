package jamesw6811.secrets.gameworld.map.discovery;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jamesw6811.secrets.gameworld.map.site.BuildingResourceSite;
import jamesw6811.secrets.gameworld.map.site.BuildingSubResourceSite;
import jamesw6811.secrets.gameworld.map.site.ChaseSite;

class Mission1DiscoveryScheme extends DiscoveryScheme {
    Mission1DiscoveryScheme(Random r) {
        super(r);
        List<ClassOdds> odds = new LinkedList<>();
        odds.add(new ClassOdds(BuildingResourceSite.class, 1));
        odds.add(new ClassOdds(BuildingSubResourceSite.class, 1));
        odds.add(new ClassOdds(ChaseSite.class, 1));
        updateOdds(odds);
    }
}

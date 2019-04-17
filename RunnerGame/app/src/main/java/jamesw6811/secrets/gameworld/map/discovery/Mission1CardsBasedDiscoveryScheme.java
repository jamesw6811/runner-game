package jamesw6811.secrets.gameworld.map.discovery;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jamesw6811.secrets.gameworld.map.site.BuildingResourceSite;
import jamesw6811.secrets.gameworld.map.site.BuildingSubResourceSite;
import jamesw6811.secrets.gameworld.map.site.ChaseSite;

class Mission1CardsBasedDiscoveryScheme extends CardsBasedDiscoveryScheme {
    Mission1CardsBasedDiscoveryScheme(Random r) {
        super(r);
        List<Class> cards = new LinkedList<>();
        cards.add(BuildingResourceSite.class);
        cards.add(BuildingSubResourceSite.class);
        cards.add(ChaseSite.class);
        cards.add(BuildingResourceSite.class);
        cards.add(BuildingSubResourceSite.class);
        cards.add(ChaseSite.class);
        setDeckAndShuffle(cards);
    }
}

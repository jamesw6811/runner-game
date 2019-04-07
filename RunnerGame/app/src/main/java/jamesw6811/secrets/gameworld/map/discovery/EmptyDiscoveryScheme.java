package jamesw6811.secrets.gameworld.map.discovery;

import java.util.LinkedList;
import java.util.Random;

class EmptyDiscoveryScheme extends DiscoveryScheme {
    EmptyDiscoveryScheme(Random r) {
        super(r);
        updateOdds(new LinkedList<>());
    }
}

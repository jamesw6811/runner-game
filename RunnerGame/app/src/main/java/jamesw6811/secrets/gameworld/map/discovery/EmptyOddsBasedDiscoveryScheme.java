package jamesw6811.secrets.gameworld.map.discovery;

import java.util.LinkedList;
import java.util.Random;

class EmptyOddsBasedDiscoveryScheme extends OddsBasedDiscoveryScheme {
    EmptyOddsBasedDiscoveryScheme(Random r) {
        super(r);
        updateOdds(new LinkedList<>());
    }
}

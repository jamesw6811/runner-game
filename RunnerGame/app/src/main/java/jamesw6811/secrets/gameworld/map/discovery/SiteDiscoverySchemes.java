package jamesw6811.secrets.gameworld.map.discovery;

import java.util.Random;

public class SiteDiscoverySchemes {
    private Random random;
    public final DiscoveryScheme Mission1;
    public final DiscoveryScheme Empty;
    public SiteDiscoverySchemes(Random r){
        random = r;
        Mission1 = new Mission1DiscoveryScheme(r);
        Empty = new EmptyDiscoveryScheme(r);
    }
}

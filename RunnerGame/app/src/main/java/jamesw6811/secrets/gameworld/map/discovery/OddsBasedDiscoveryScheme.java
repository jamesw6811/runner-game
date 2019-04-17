package jamesw6811.secrets.gameworld.map.discovery;

import java.util.List;
import java.util.Random;

public abstract class OddsBasedDiscoveryScheme extends DiscoveryScheme {
    private Random random;
    private double totalOdds;
    private List<ClassOdds> odds;

    OddsBasedDiscoveryScheme(Random r){
        random = r;
    }

    public final Class discover(){
        double randD = random.nextDouble();
        double scaledRand = randD*totalOdds;
        List<ClassOdds> odds = getSiteOdds();
        for (ClassOdds odd : odds){
            if (scaledRand < odd.getOdds()) return odd.getDiscoveryClass();
            else scaledRand -= odd.getOdds();
        }
        return null;
    }

    private double recalculateTotalOdds(){
        double total = 0;
        for (ClassOdds odd : getSiteOdds()){
            total += odd.getOdds();
        }
        return total;
    }

    private List<ClassOdds> getSiteOdds(){
        return odds;
    }

    final void updateOdds(List<ClassOdds> odds){
        this.odds = odds;
        this.totalOdds = recalculateTotalOdds();
    }
}

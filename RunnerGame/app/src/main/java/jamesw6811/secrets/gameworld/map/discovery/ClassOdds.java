package jamesw6811.secrets.gameworld.map.discovery;


class ClassOdds {
    private Class site;
    private double odds;
    ClassOdds(Class site, double odds){
        this.site = site;
        this.odds = odds;
    }

    Class getDiscoveryClass() {
        return site;
    }

    double getOdds() {
        return odds;
    }
}

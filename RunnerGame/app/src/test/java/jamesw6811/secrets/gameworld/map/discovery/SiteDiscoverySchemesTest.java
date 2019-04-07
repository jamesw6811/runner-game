package jamesw6811.secrets.gameworld.map.discovery;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jamesw6811.secrets.gameworld.map.site.BuildingResourceSite;
import jamesw6811.secrets.gameworld.map.site.BuildingSubResourceSite;
import jamesw6811.secrets.gameworld.map.site.ChaseSite;

import static org.junit.Assert.*;

public class SiteDiscoverySchemesTest {
    Random randMock = new Random(){
        private double next;
        public void setNext(double d){
            next = d;
        }

        @Override
        public double nextDouble(){
            return next;
        }
    };
    class TestOddsDiscoveryScheme extends DiscoveryScheme {

        TestOddsDiscoveryScheme(Random r) {
            super(r);
        }
    }
    @Test
    public void discoverySchemesTestOdds(){
        List<ClassOdds> odds = new LinkedList<>();
        odds.add(new ClassOdds(BuildingResourceSite.class, 1));
        odds.add(new ClassOdds(BuildingSubResourceSite.class, 1));
        odds.add(new ClassOdds(ChaseSite.class, 1));
        updateOdds(odds);
    }
}
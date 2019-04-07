package jamesw6811.secrets.gameworld.map.discovery;

import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import jamesw6811.secrets.gameworld.map.MapManager;
import jamesw6811.secrets.gameworld.map.site.BuildingResourceSite;
import jamesw6811.secrets.gameworld.map.site.BuildingSubResourceSite;
import jamesw6811.secrets.gameworld.map.site.ChaseSite;

import static org.junit.Assert.*;

public class SiteDiscoverySchemesTest {
    class RandomMock extends Random {
        private double next;
        public void setNext(double d){
            next = d;
        }

        @Override
        public double nextDouble(){
            return next;
        }
    }
    RandomMock randMock = new RandomMock();

    class TestOddsDiscoveryScheme extends DiscoveryScheme {
        TestOddsDiscoveryScheme(Random r, List<ClassOdds> odds) {
            super(r);
            updateOdds(odds);
        }
    }
    class Class1 {}
    class Class2 {}
    class Class3 {}

    @Test
    public void discoveryScheme_discover_3_choices(){
        List<ClassOdds> odds = new LinkedList<>();
        odds.add(new ClassOdds(Class1.class, 1));
        odds.add(new ClassOdds(Class2.class, 0.5));
        odds.add(new ClassOdds(Class3.class, 0.5));
        DiscoveryScheme scheme = new TestOddsDiscoveryScheme(randMock, odds);

        randMock.setNext(0.0);
        assertEquals(scheme.discover(), Class1.class);
        randMock.setNext(0.6);
        assertEquals(scheme.discover(), Class2.class);
        randMock.setNext(0.8);
        assertEquals(scheme.discover(), Class3.class);
        randMock.setNext(0.6);
        assertEquals(scheme.discover(), Class2.class);
    }

    @Test
    public void discoveryScheme_discover_1_choice(){
        List<ClassOdds> odds = new LinkedList<>();
        odds.add(new ClassOdds(Class1.class, 1));
        DiscoveryScheme scheme = new TestOddsDiscoveryScheme(randMock, odds);

        randMock.setNext(0.9);
        assertEquals(scheme.discover(), Class1.class);
    }

    @Test
    public void discoveryScheme_discover_0_choices(){
        List<ClassOdds> odds = new LinkedList<>();
        DiscoveryScheme scheme = new TestOddsDiscoveryScheme(randMock, odds);

        randMock.setNext(0.5);
        assertNull(scheme.discover());
    }
}
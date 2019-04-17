package jamesw6811.secrets.gameworld.map.discovery;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class CardsBasedDiscoverySchemeTest {
    class TestCardsBasedDiscoveryScheme extends CardsBasedDiscoveryScheme {
        TestCardsBasedDiscoveryScheme(Random r, List<Class> cards) {
            super(r);
            setDeckAndShuffle(cards);
        }
    }
    class Class1 {}
    class Class2 {}
    class Class3 {}

    @Test
    public void discoveryScheme_discover_3_choices(){
        List<Class> cards = new LinkedList<>();
        cards.add(Class1.class);
        cards.add(Class2.class);
        cards.add(Class3.class);
        DiscoveryScheme scheme = new TestCardsBasedDiscoveryScheme(new Random(1), cards);

        assertEquals(Class2.class, scheme.discover());
        assertEquals(Class3.class, scheme.discover());
        assertEquals(Class1.class, scheme.discover());
        assertEquals(Class1.class, scheme.discover());
    }

    @Test
    public void discoveryScheme_discover_1_choice(){
        List<Class> cards = new LinkedList<>();
        cards.add(Class2.class);
        DiscoveryScheme scheme = new TestCardsBasedDiscoveryScheme(new Random(1), cards);

        assertEquals(Class2.class, scheme.discover());
        assertEquals(Class2.class, scheme.discover());
    }

    @Test
    public void discoveryScheme_discover_0_choices(){
        List<Class> cards = new LinkedList<>();
        DiscoveryScheme scheme = new TestCardsBasedDiscoveryScheme(new Random(1), cards);
        assertNull(scheme.discover());
    }
}
package jamesw6811.secrets.gameworld.map.discovery;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CardsBasedDiscoveryScheme extends DiscoveryScheme{
    private List<Class> cardsDeck;
    private List<Class> cardsDiscard;
    private Random random;

    protected CardsBasedDiscoveryScheme(Random random){
        this.random = random;
        cardsDeck = new LinkedList<>();
        cardsDiscard = new LinkedList<>();
    }

    protected final void setDeckAndShuffle(List<Class> cards){
        this.cardsDiscard = cards;
        this.cardsDeck = new LinkedList<>();
        shuffle();
    }

    @Override
    public Class discover() {
        if (cardsDeck.size()==0){
            if (cardsDiscard.size()==0) return null;
            else shuffle();
        }
        Class card = cardsDeck.remove(0);
        cardsDiscard.add(card);
        return card;
    }

    private void shuffle(){
        Collections.shuffle(cardsDiscard, random);
        cardsDeck.addAll(cardsDiscard);
    }
}

package jamesw6811.secrets.gameworld.map.discovery;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MultiStageCardsBasedDiscoveryScheme extends DiscoveryScheme{
    private List<Class> cardsDeck;
    private List<Class> cardsDiscard;
    private List<List<Class>> remainingDecks;
    private Random random;

    protected MultiStageCardsBasedDiscoveryScheme(Random random){
        this.random = random;
        cardsDeck = new LinkedList<>();
        cardsDiscard = new LinkedList<>();
    }

    protected final void setDecksAndShuffle(List<List<Class>> decks){
        remainingDecks = decks;
        nextDeck();
    }

    private void nextDeck(){
        this.cardsDiscard = remainingDecks.remove(0);
        this.cardsDeck = new LinkedList<>();
        shuffle();
    }

    @Override
    public Class discover() {
        if (cardsDeck.size()==0){
            if (remainingDecks.size()==0) shuffle();
            else nextDeck();
        }
        Class card = cardsDeck.remove(0);
        cardsDiscard.add(card);
        return card;
    }

    private void shuffle(){
        if (random != null) Collections.shuffle(cardsDiscard, random);
        cardsDeck.addAll(cardsDiscard);
    }
}

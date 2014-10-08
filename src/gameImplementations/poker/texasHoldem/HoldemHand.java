package gameImplementations.poker.texasHoldem;

import deck.Card;
import game.Hand;
import gameImplementations.poker.PokerHandAnalyzer;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HoldemHand implements Hand, Comparable<HoldemHand>{

    protected List<Card> cards;
    private double strength;
    private boolean isStrengthUpToDate;

    public HoldemHand() {
        cards = new LinkedList<>();
        strength = 0;
        isStrengthUpToDate = false;
    }

    public HoldemHand(Collection<Card>... cardCols){
        this();
        for(Collection<Card> cardCol : cardCols){
            cards.addAll(cardCol);
        }
	}

    @Override
    public boolean addCard(Card card) {
        isStrengthUpToDate = false;
        return cards.add(card);
    }

    public List<Card> getCards() {
        Collections.sort(cards);
        return cards;
	}
	
	public double getStrength(){
        if (!isStrengthUpToDate){
            strength = PokerHandAnalyzer.getStrength(this);
            isStrengthUpToDate = true;
        }
        return strength;
    }

    public String getDescription(){
        return PokerHandAnalyzer.getDescription(getStrength());
    }

	@Override
	public int compareTo(HoldemHand otherHand) {
        return Double.compare(getStrength(), otherHand.getStrength());
    }
}

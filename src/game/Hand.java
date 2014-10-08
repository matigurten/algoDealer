package game;

import deck.Card;

import java.util.List;

public interface Hand {
	public List<Card> getCards();

    public double getStrength();

    public boolean addCard(Card card);
}

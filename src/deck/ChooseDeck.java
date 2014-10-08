package deck;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a deck of cards. A deck starts with 52 cards. The cards
 * in a deck may be dealt one by one from the deck.
 *
 * Revised to include throwing an EmptyDeckException when there is an 
 * attempt to deal from an empty deck.
 *
 * 
 */
public class ChooseDeck {

	private List<Card> deck; 
	
	public ChooseDeck(int deckNumber){
		deck = new LinkedList<>();

		for (int i = 0 ; i < deckNumber ; i++){
			for (Card.Suit suit : Card.Suit.values()) {
	            for (Card.Rank rank : Card.Rank.values()) {
	                deck.add(new Card(rank, suit));
	            }
	        }
		}
        Collections.sort(deck);
    }
	
	/**
	 * Constructs a new Deck object containing 52 cards.
	 */
	public ChooseDeck() {
		this(1);
	}

	////////////////////////////////////////
	// Public Methods
	////////////////////////////////////////

	/**
	 * Returns the number of cards in the deck.
	 * @return number of cards in the deck
	 */
	public int cardsInDeck() { return deck.size(); }

	/**
	 * Removes or "deals" a specified card from the deck
	 */
	public Card deal(Card card) {
        return (deck.remove(card)) ? card : null;
	}

    public List<Card> deal(int nrToDeal) {
        List<Card> dealtCards = new LinkedList<>();
        for (int i = 0; i < nrToDeal; i++){
            dealtCards.add(deal());
        }
        return dealtCards;
    }

    public Card deal() {
        if (deck.size() > 0) {
            return deck.remove((int) (Math.random() * deck.size()));
        }
        return null;
    }

    /**
	 * Returns a String representation of the Deck object. The cards 
	 * currently in the deck are printed out 13 to a row.
	 * 
	 * @return a String representation of the deck
	 */
	public String toString() {

		// check for an empty deck
		if (cardsInDeck() == 0)
			return "Empty Deck";

		String deckString = "";
        int counter = 1;
		for (Card card : deck){
			deckString += (counter++ + ": " + card + "\n");
		}

		return deckString;
	}

}
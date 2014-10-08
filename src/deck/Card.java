package deck;

import gestureListener.ColorHsv;

import java.awt.*;

import static com.googlecode.javacv.cpp.opencv_core.CvScalar;

public class Card implements Comparable<Card> {
    public static enum Suit {
        UNRECOGNIZED("*", null, null, null),
        CLUBS("♣", Color.black, Color.green, ColorHsv.BLACK),
        SPADES("♠", Color.black, Color.black, ColorHsv.BLACK),
        DIAMONDS("♦", Color.red, Color.blue, ColorHsv.RED),
        HEARTS("♥", Color.red, Color.red, ColorHsv.RED);

        protected final String suitSymbol;
        protected final Color suitColor;
        protected Color displayColor;
        protected ColorHsv hsvColor;

        Suit(String cardSymbol, Color suitColor, Color displayColor, ColorHsv hsvColor) {
            this.suitSymbol = cardSymbol;
            this.suitColor = suitColor;
            this.displayColor = displayColor;
            this.hsvColor = hsvColor;
        }

        public Color getSuitColor() { return suitColor; }

        protected String getSuitSymbol() { return suitSymbol; }

        public Color getDisplayColor() { return displayColor; }

        public ColorHsv getHsvColor() { return hsvColor; }

    }

    public static enum Rank {
        UNRECOGNIZED("?", "0", 0),
        TWO("2", "2", 2),
        THREE("3", "3", 3),
        FOUR("4", "4", 4),
        FIVE("5", "5", 5),
        SIX("6", "6", 6),
        SEVEN("7", "7", 7),
        EIGHT("8", "8", 8),
        NINE("9", "9", 9),
        TEN("10", "A", 10),
        JACK("J", "B", 11),
        QUEEN("Q", "C", 12),
        KING("K", "D", 13),
        ACE("A", "E", 14);

        protected final String cardSymbol;
        protected final String hexaDigit;
        protected final int strength;

        Rank(String cardSymbol, String hexaDigit, int strength) {
            this.cardSymbol = cardSymbol;
            this.hexaDigit = hexaDigit;
            this.strength = strength;
        }

        protected String getCardSymbol() {
            return cardSymbol;
        }

        public String getHexaDigit() {
            return hexaDigit;
        }

        public int getStrength() {
            return strength;
        }

    }

    final Rank rank;
    final Suit suit;

    public Card(final Rank rank, final Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return this.rank.getCardSymbol() + this.suit.getSuitSymbol();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Card)) return false;

        Card card = (Card) obj;

        boolean noSuit = (this.suit == null || card.suit == null);

        return this.rank == card.rank && (noSuit || this.suit == card.suit);
    }

    @Override
    public int compareTo(Card card) {
        return -1 * ((rank.getStrength() - card.getRank().getStrength()) * 10 + suit.compareTo(card.getSuit()));
    }
}
package gameImplementations.poker;

import deck.Card;
import deck.Card.Rank;
import deck.Card.Suit;
import gameImplementations.poker.texasHoldem.HoldemHand;
import participants.Player;

import java.util.*;

public class PokerHandAnalyzer {
    private static final int STRAIGHT_LENGTH = 5;
    private static final int FLUSH_SIZE = 5;


    protected enum Strength {
        SINGLE("High Card", 0),
        PAIR("One Pair", 1),
        TWO_PAIRS("Two Pairs", 2),
        TRIO("Three of a Kind", 3),
        STRAIGHT("Straight", 4),
        FLUSH("Flush", 5),
        FULL_HOUSE("Full House", 6),
        QUADS("Four of a Kind", 7),
        STRAIGHT_FLUSH("Straight Flush", 8),
        ROYAL_FLUSH("Royal Flush", 9);

        protected final String handName;
        protected final int strength;

        Strength(final String handName, final int strength) {
            this.handName = handName;
            this.strength = strength;
        }

        protected int getStrength() {
            return strength;
        }

        public String getHandName() {
            return handName;
        }

    }

    public static double getStrength(List<Card> publicCards, HoldemHand hand) {
        return getStrength(new HoldemHand(publicCards, hand.getCards()));
    }

    public static double getStrength(HoldemHand hand) {
        // Process hand
        Map<Suit, List<Card>> suitCounter = new HashMap<>();
        Map<Rank, Integer> rankCounter = new TreeMap<>(Collections.reverseOrder());

        // Retrieved sorted from High to Low strength
        for (Card card : hand.getCards()) {
            if (!rankCounter.containsKey(card.getRank())) {
                rankCounter.put(card.getRank(), 0);
            }
            rankCounter.put(card.getRank(), rankCounter.get(card.getRank()) + 1);

            if (!suitCounter.containsKey(card.getSuit())) {
                suitCounter.put(card.getSuit(), new LinkedList<Card>());
            }
            suitCounter.get(card.getSuit()).add(card);
        }

        // Analyze processed hand
        // Flush + Straight Flush
        for (List<Card> sameSuit : suitCounter.values()) {
            if (sameSuit.size() >= FLUSH_SIZE) {
                // Straight
                List<Card> straight = hasStraight(sameSuit);
                return straight == null ?
                        Strength.FLUSH.getStrength() + getKicker(sameSuit) :
                        Strength.STRAIGHT_FLUSH.getStrength() + getKicker(straight);
            }
        }
        // Straight
        List<Card> straight = hasStraight(hand.getCards());
        if (straight != null) {
            return Strength.STRAIGHT.getStrength() + getKicker(straight);
        }

        // Other Hands
        double handStrength = 0;
        String singles = "", pairs = "", trios = "", quads = "";
        for (Rank rank : rankCounter.keySet()) {
            switch (rankCounter.get(rank)) {
                case 1:
                    handStrength += Strength.SINGLE.getStrength();
                    singles += rank.getHexaDigit();
                    break;
                case 2:
                    handStrength = (handStrength != Strength.TRIO.getStrength()) ?
                            Math.min(handStrength + Strength.PAIR.getStrength(),

                                    Strength.TWO_PAIRS.getStrength()) :
                            Strength.FULL_HOUSE.getStrength();
                    pairs += rank.getHexaDigit() + rank.getHexaDigit();
                    break;
                case 3:
                    handStrength = handStrength < Strength.PAIR.getStrength() ?
                            Strength.TRIO.getStrength() :
                            Strength.FULL_HOUSE.getStrength();
                    trios += rank.getHexaDigit() + rank.getHexaDigit() + rank.getHexaDigit();
                    break;
                case 4:
                    handStrength = Strength.QUADS.getStrength();
                    quads += rank.getHexaDigit() + rank.getHexaDigit() + rank.getHexaDigit() + rank.getHexaDigit();
                    break;
            }
        }

        return handStrength + getKicker(quads + trios + pairs + singles);
    }

    private static List<Card> hasStraight(List<Card> cards) {
        Collections.sort(cards);
        List<Card> straight = new ArrayList<>();
        Card lastCard = cards.get(0);
        straight.add(lastCard);
        Card weakAce = null;
        // Retrieved sorted from High to Low strength
        for (Card card : cards) {
            int step = lastCard.getRank().getStrength() - card.getRank().getStrength();
            if (step > 1) {
                straight.clear();
            }
            if (step != 0) {
                straight.add(card);
            }
            if (Rank.ACE.equals(card.getRank())) {
                weakAce = card;
            }
            if (straight.size() == STRAIGHT_LENGTH) {
                return straight;
            }
            lastCard = card;
        }
        if (weakAce != null && straight.size() == STRAIGHT_LENGTH - 1 &&
                Rank.FIVE.equals(straight.get(0).getRank())) {
            straight.add(weakAce);
        }
        boolean hasBigStraight = straight.size() >= STRAIGHT_LENGTH;
        return hasBigStraight ? straight : null;
    }

    private static double getKicker(List<Card> cards) {
        String kickerStr = "";
        for (Card card : cards) {
            kickerStr += card.getRank().getHexaDigit();
        }
        return getKicker(kickerStr);
    }

    private static double getKicker(String kickerStr) {
        String kicker = kickerStr.substring(0, Math.min(kickerStr.length(), 5));
        return Double.valueOf("0." + Integer.parseInt(kicker, 16));
    }

    public static String getDescription(double strength) {
        Strength strengthEnum = Strength.values()[(int) strength];
        String kicker = getKicker((int) Math.round((strength % 1) * 1e6));

        String desc = "";
        switch (strengthEnum.getStrength()) {
            case 0: // High Card
                desc = kicker;
                break;
            case 1: // One Pair
                // Dealer: Hand #114671608230: TaiSheng wins pot ($16.57) with a pair ofAces - King kicker
                desc = "of " + kicker.charAt(0) + " with " + kicker.substring(4) + " kicker";
                break;
            case 2: // Two Pairs
                // Dealer: Hand #114670367502: 1enov wins pot ($3.45) with two pair, Nines and Threes
                desc = "of " + kicker.charAt(0) + " and " + kicker.charAt(4) + " with " + kicker.substring(8) + " kicker";
                break;
            case 3: // Three of a Kind
                desc = "of " + kicker.charAt(0) + " with " + kicker.substring(6) + " kicker";
                break;
            case 4: // Straight
                desc = kicker.charAt(0) + " high";
                break;
            case 5: // Flush
                desc = kicker + " kicker";
                break;
            case 6: // Full House
                desc = kicker.charAt(0) + " full of " + kicker.charAt(6);
                break;
            case 7: // Four of a Kind
                desc = kicker + " kicker";
                break;
            case 8: // Straight Flush
                desc = kicker + " kicker";
                if (kicker.charAt(0) == 'A') {
                    strengthEnum = Strength.ROYAL_FLUSH;
                }
                break;
        }

        return strengthEnum.getHandName() + " " + desc;
    }

    private static String getKicker(int kicker) {
        String kickerStr = Integer.toHexString(kicker).replace("", " ").trim();
        kickerStr = kickerStr.replaceAll("a", "T");
        kickerStr = kickerStr.replaceAll("b", "J");
        kickerStr = kickerStr.replaceAll("c", "Q");
        kickerStr = kickerStr.replaceAll("d", "K");
        kickerStr = kickerStr.replaceAll("e", "A");

        return kickerStr;
    }

    public static List<Player> rankPlayersByHand(Map<Player, HoldemHand> hands) {
        // Sort player hand strength from high to low
        SortedMap<Double, Player> playersByHand = new TreeMap<>(Collections.reverseOrder());

        for (Player player : hands.keySet()) {
            playersByHand.put(getStrength(hands.get(player)), player);
        }

        List<Player> ranking = new ArrayList<>(hands.size());
        ranking.addAll(playersByHand.values());
        return ranking;
    }

    public static List<Player> rankPlayersByHand(List<Card> holeCards, Set<Player> playersInHand) {
        // Sort player hand strength from high to low
        SortedMap<Double, Player> playersByHand = new TreeMap<>(Collections.reverseOrder());

        for (Player player : playersInHand) {
            playersByHand.put(getStrength(holeCards, (HoldemHand) player.getHand()), player);
        }

        List<Player> ranking = new ArrayList<>(playersInHand.size());
        ranking.addAll(playersByHand.values());
        return ranking;
    }

    public static Set<Player> getWinner(List<Card> holeCards, Set<Player> players) {
        Set<Player> winners = new HashSet<>();
        double maxStrength = 0;
        for (Player player : players) {
            double handStrength = player.getHand().getStrength();
            if (maxStrength < handStrength) {
                maxStrength = handStrength;
                winners.clear();
                winners.add(player);
            } else if (maxStrength == handStrength) {
                winners.add(player);
            }
        }
        return winners;
    }

    public static Set<Player> getWinner(Map<Player, HoldemHand> hands) {
        Set<Player> winners = new HashSet<>();
        double maxStrength = 0;
        for (Player player : hands.keySet()) {
            double handStrength = hands.get(player).getStrength();
            if (maxStrength < handStrength) {
                maxStrength = handStrength;
                winners.clear();
                winners.add(player);
            } else if (maxStrength == handStrength) {
                winners.add(player);
            }
        }
        return winners;
    }
}

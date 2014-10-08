package gameImplementations.poker.texasHoldem;

import deck.Card;
import deck.ChooseDeck;
import game.Game;
import game.GameEnum;
import gameImplementations.poker.Pot;
import participants.Player;
import gameImplementations.poker.PokerHandAnalyzer;

import java.util.*;

/**
 * See http://www.texasholdem-poker.com/holdem_rules
 *     http://www.wikihow.com/Figure-Out-Poker-Side-Pots
 */
public class TexasHoldem implements Game{

    private static enum Street {
        PRE_FLOP(0, "pre flop"),
        FLOP(3, "flop"),
        TURN(1, "turn"),
        RIVER(1, "river");

        private final int cardsToOpen;
        private final String streetName;

        Street(int cardsToOpen, String streetName) {
            this.cardsToOpen = cardsToOpen;
            this.streetName = streetName;
        }

        private int getCardsToOpen() {
            return cardsToOpen;
        }
        private String getStreetName() {
            return streetName;
        }
    }

    public static GameEnum GAME_KIND = GameEnum.TEXAS_HOLDEM;

    private final double ANTE;
    private final double SMALL_BLIND;
    private final double BIG_BLIND;
    private final double RAKE;
    private final double CAP;

    private ChooseDeck deck;
    protected List<Player> playersInHand;
    private List<Pot> pots;
    private Player first;

    public TexasHoldem(List<Player> playersInHand, double ante, double smallBlind, double bigBlind, double rake, double cap){
        this(playersInHand, ante, smallBlind, bigBlind, rake, cap, null);
    }

    private  Map<Player, List<Double>> bets;

    public TexasHoldem(List<Player> playersInHand, double ante, double smallBlind, double bigBlind, double rake, double cap, Map<Player, List<Double>> bets){
        this.playersInHand = playersInHand;

        this.ANTE = ante;
        this.SMALL_BLIND = smallBlind;
        this.BIG_BLIND = bigBlind;
        this.RAKE = rake;
        this.CAP = cap;

        pots = new LinkedList<>();
        first = playersInHand.get(0); //    Player dealer = getDealer(); // eyes.getDealer();

        this.bets = bets;
    }

    @Override
    public List<Player> getPlayersInHand() {
        return playersInHand;
    }

    private Player initHand() {
        deck = new ChooseDeck();
        pots.clear();
        pots.add(new Pot(getPlayersInHand()));
        postAntes();
        dealHand(getActivePot().getPlayers());
        return postBlinds();
    }

    private List<Player> postAntes() {
        Set<Player> notInHand = new HashSet<>();
        for (Player player : getPlayersInHand()){
            if (player.getStack() >= ANTE + BIG_BLIND){
                player.bet(ANTE);
                addToPot(ANTE);
            } else {
                removeFromPots(player);
                notInHand.add(player);
            }
        }
        playersInHand.removeAll(notInHand);
        return playersInHand;
    }

    private boolean dealHand(Collection<Player> players){
        for(Player player : players){
            player.initHand(GAME_KIND);
        }
        for(int i = 0 ; i < 2; i++){
            for(Player player : players){
                if (!player.receiveCard(deal())){
                    return false; // Misdeal
                }
            }
        }
        return true;
    }

    private Card deal(){
        return deck.deal();
    }

    private List<Card> deal(int cardsToDeal){
        return deck.deal(cardsToDeal);
    }

    private void addToPot(double chips) {
        getActivePot().addToPot(chips);
    }

    private Player postBlinds() {
        Player sb = playersInHand.remove(0);
        sb.bet(SMALL_BLIND);
        addToPot(SMALL_BLIND);
        playersInHand.add(sb);
        Player bb = playersInHand.remove(0);
        bb.bet(BIG_BLIND);
        addToPot(BIG_BLIND);
        playersInHand.add(bb);
        return bb;
    }

    /**
     * @return winners and their winnings
     */
    @Override
    public Map<Player, Double> dealGame(){
        List<Card> board = new LinkedList<>();

        Player raiser = initHand(); // Straddle and Stuff
        double toCall = BIG_BLIND;
        for (Street street : Street.values()){
            board.addAll(openStreet(street));
            System.out.println("Street " + street.getStreetName() + ": " + board);

            double minRaise = BIG_BLIND;
            while (playersInHand.get(0) != raiser || playersInHand.get(0).getBet() == null) {
                Player current = playersInHand.remove(0);

                // change to external trigger input
                // double bet = eyes.getBetFromGesture(current); // Single forward motion - Include change giving
                double bet = bets.get(current).isEmpty() ? 0 : bets.get(current).remove(0);
                double currentBet = ((current.getBet() == null) ? 0 : current.getBet()) + Math.min(current.getStack(), bet);

                double raise = currentBet - toCall;
                if (raise < minRaise) { // Non Raise
                    if (raise == 0) { // Check / Call
                        current.bet(currentBet);
                        playersInHand.add(current);

                        System.out.println(current.toString() + ((currentBet == 0) ? " checks" : (" calls " + currentBet)));
                    } else if (bet == 0) { // Fold
                        current.muckHand();
                        removeFromPots(current);
                        if (current == first){
                            first = playersInHand.get(0);
                        }

                        System.out.println(current.toString() + " folds");
                    } else { // Caution
                        if (current.isAllIn()) { // Split Pot
                            System.out.println(current.toString() + " calls " + "?" + " and is all in");
                            current.bet(currentBet);
                        } else { // Bad bet
                            System.out.println(current.toString() + " calls " + toCall + ". " +
                                              (raise > 0 ? "Take back " : "Add ") + raise);
                            bet -= raise;
                            current.bet(toCall); // current.bet(bet)
                            playersInHand.add(current);
                        }
                    }
                } else { // Legal Raise - Original raiser can't re-raise the all in
                    minRaise = currentBet - minRaise;
                    current.bet(currentBet);
                    raiser = current;
                    playersInHand.add(current);

                    System.out.println(current.toString() + ((toCall == 0) ? " bets " : " raises to ") + currentBet);

                    toCall = currentBet;
                }
                addToPot(bet);
            }
            if (playersInHand.size() > 1) { // Hand isn't over
                for (Player player : playersInHand){
                    player.takeBet(); // Take bets to Pot
                }
                toCall = 0;
                validateDealer();
                raiser = first;
            } else { // Hand is over
                System.out.println(playersInHand.get(0) + " wins " + getActivePot().getPot() + " pot.");
                break;
            }
        }
        // SHOWDOWN
        System.out.println("Showdown:");
        for (Player player : playersInHand){
            HoldemHand hand = new HoldemHand(board, player.getHand().getCards());
            System.out.println(player.toString() + ": " + player.getHand().getCards() + " | " +  hand.getDescription() + " | " + hand.getStrength());
        }

        Map<Player, Double> payup = new HashMap<>();

        for(Pot pot : pots){
            double rakeFromPot = getRake(pot);
            pot.takeRake(rakeFromPot);
            payup.put(null, (payup.containsKey(null) ? payup.get(null) : 0) + rakeFromPot);
            Set<Player> potWinners = PokerHandAnalyzer.getWinner(board, new HashSet<>(pot.getPlayers()));
            for (Player potWinner : potWinners){
                payup.put(potWinner, (payup.containsKey(potWinner) ? payup.get(potWinner) : 0) + pot.getWinnings(potWinners.size()));
                System.out.println(potWinner + " wins a " + pot.getPot() + " pot.");
            }
        }
        System.out.println("Dealer takes rake of " + payup.get(null));
        finishHand();
        return payup;
    }

    private void finishHand() {
        for (Player playerInHand : getPlayersInHand()){
            playerInHand.muckHand();
        }
        deck = null;
        pots = null;
    }

    private void removeFromPots(Player folder) {
        for (Pot pot : pots){
            pot.removeFromPot(folder);
        }
    }

    private List<Card> openStreet(Street street) {
        return deal(street.getCardsToOpen());
    }

    private Pot getActivePot() {
        return pots.get(pots.size() - 1);
    }

    private void validateDealer() {
        while (playersInHand.get(0) != first) {
            Player pl = playersInHand.remove(0);
            playersInHand.add(pl);
        }
    }

    public double getRake(Pot pot){
        return Math.min(CAP, pot.getPot() * RAKE);
	}
}

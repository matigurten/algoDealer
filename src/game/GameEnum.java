package game;

import gameImplementations.blackJack.BlackJackHandFactory;
import gameImplementations.poker.texasHoldem.HoldemHandFactory;

public enum GameEnum {
	TEXAS_HOLDEM(new HoldemHandFactory()),
    OMAHA(null),
    BLACK_JACK(new BlackJackHandFactory());

    protected final HandFactory hf;

    GameEnum(HandFactory hf) {
        this.hf = hf;
    }

    public Hand getNewHand() {
        return hf.make();
    }
}

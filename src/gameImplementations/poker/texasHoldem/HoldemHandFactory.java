package gameImplementations.poker.texasHoldem;

import game.Hand;
import game.HandFactory;

/**
 * Created by Mati on 17/04/2014.
 */
public class HoldemHandFactory implements HandFactory {

    @Override
    public Hand make() {
        return new HoldemHand();
    }
}

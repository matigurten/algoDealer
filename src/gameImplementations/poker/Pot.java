package gameImplementations.poker;

import participants.Player;

import java.util.Collection;

/**
 * Created by Mati on 17/04/2014.
 */
public class Pot {
    private double pot;

    private Collection<Player> playersInPot;

    public Pot(Collection<Player> playersInPot){
        this.playersInPot = playersInPot;
        pot = 0;
    }

    public Collection<Player> getPlayers(){
        return playersInPot;
    }

    public Collection<Player> removeFromPot(Player player){
        playersInPot.remove(player);
        return playersInPot;
    }

    public double getPot(){
        return addToPot(0);
    }

    public double addToPot(double sum){ return pot += sum; }

    public double takeRake(double rake) { return addToPot( -rake); }

    public double getWinnings(int winners) { return pot / winners; }
}

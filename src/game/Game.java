package game;

import participants.Player;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public interface Game {
	
	GameEnum GAME_KIND = null;
	
	int tableId = -1;
	
    public List<Player> getPlayersInHand();

    public Map<Player, Double> dealGame();

}

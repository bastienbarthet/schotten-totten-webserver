package server;

import com.boradgames.bastien.schotten_totten.core.controllers.SimpleGameManager;
import com.boradgames.bastien.schotten_totten.core.exceptions.GameCreationException;
import com.boradgames.bastien.schotten_totten.core.model.Game;

public class ServerGameManager extends SimpleGameManager {

	public ServerGameManager(String player1Name, String player2Name, String uid) throws GameCreationException {
		super(player1Name, player2Name, uid);
	}
	
	public Game getGame() {
		return game;
	}

	
}

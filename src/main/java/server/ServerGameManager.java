package server;

import com.boardgames.bastien.schotten_totten.controllers.SimpleGameManager;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.model.Game;

public class ServerGameManager extends SimpleGameManager {

	public ServerGameManager(String player1Name, String player2Name, String uid) throws GameCreationException {
		super(player1Name, player2Name, uid);
	}
	
	public Game getGame() {
		return game;
	}

	
}

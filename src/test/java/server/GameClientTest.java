package server;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpException;

import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;

public class GameClientTest {

	public static void main(final String[] args) {
//		final GameClient client = new GameClient("https://schotten-totten.herokuapp.com");
		final GameClient client = new GameClient("http://localhost:8000");
		try {
			final Game game = new Game("P1", "P2");
			client.createdGame("test1" + System.currentTimeMillis(), game);
			System.out.println(game.getPlayingPlayer().getName());
		} catch (final HttpException | IOException | GameAlreadyExistsException | NoPlayerException | GameCreationException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		try {
			
			final String gameName = client.listGame().get(0);
			
			Game game = client.getGame(gameName);
			game = client.getGame(gameName);
			
			client.updateGame(gameName, game);
			
			game = client.getGame(gameName);
			
			final ArrayList<String> list = client.listGame();
			System.out.println(list.get(0));
			
			client.deleteGame(gameName);
			
		} catch (final IOException | GameDoNotExistException | HttpException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			client.getGame("test1");
			System.exit(-1);
		} catch (final IOException | GameDoNotExistException | HttpException e) {
			e.printStackTrace();
			System.out.println("test ok");
		}
		
	}
}

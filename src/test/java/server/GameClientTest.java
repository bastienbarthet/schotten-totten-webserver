package server;

import java.io.IOException;

import org.apache.http.HttpException;

import com.boardgames.bastien.schotten_totten.model.Game;

public class GameClientTest {

	public static void main(final String[] args) {
		final GameClient client = new GameClient();
		try {
			client.createdGame("test1");
		} catch (final HttpException | IOException | GameAlreadyExistsException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		try {
			
			final String gameName = client.listGame().get(0);
			
			Game game = client.getGame(gameName);
			game = client.getGame(gameName);
			
			client.updateGame(gameName, game);
			
			game = client.getGame(gameName);
			
			client.deleteGame(gameName);
			
		} catch (final IOException | GameDoNotExistException | HttpException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		
		try {
			final Game game = client.getGame("test1");
			
			System.exit(-1);
			
		} catch (final IOException | GameDoNotExistException | HttpException e) {
			e.printStackTrace();
			System.out.println("test ok");
		}
		
	}
}

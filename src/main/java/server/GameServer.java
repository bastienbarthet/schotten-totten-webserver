package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class GameServer {

	private final static Map<String, Game> gameList = new HashMap<>();

	public static void main(String[] args) throws Exception {
		final int port = 8000;//Integer.valueOf(System.getenv("$PORT"));
		final HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
		server.createContext("/getGame", new GetGameHandler());
		server.createContext("/createGame", new CreateGameHandler());
		server.createContext("/updateGame", new UpdateGameHandler());
		server.createContext("/deleteGame", new DeleteGameHandler());
		server.createContext("/listGame", new ListGameHandler());
		server.createContext("/", new PingHandler());
		server.start();
	}

	private static class PingHandler implements HttpHandler {
		public void handle(final HttpExchange t) throws IOException {
			try (final OutputStream os = t.getResponseBody()) {
				final byte[] byteArray = "alive".getBytes();
				System.out.println("Ping Alive.");
				t.sendResponseHeaders(HttpStatus.SC_OK, byteArray.length);
				os.write(byteArray);
			} catch (final IOException e) {
				t.sendResponseHeaders(HttpStatus.SC_METHOD_FAILURE, 0);
			}
		}
	}
	
	private static class GetGameHandler implements HttpHandler {
		public void handle(final HttpExchange t) throws IOException {
			try (final OutputStream os = t.getResponseBody()) {
				final String gameName = t.getRequestHeaders().getFirst("gameName");
				if (gameList.containsKey(gameName)) {
					System.out.println("Game " + gameName + " requested.");
					final byte[] byteArray = ByteArrayUtils.gameToByteArray(gameList.get(gameName));
					t.sendResponseHeaders(HttpStatus.SC_OK, byteArray.length);
					os.write(byteArray);
				} else {
					t.sendResponseHeaders(HttpStatus.SC_NO_CONTENT, 0);
				}
			} catch (final IOException e) {
				t.sendResponseHeaders(HttpStatus.SC_METHOD_FAILURE, 0);
			}
		}
	}

	private static class ListGameHandler implements HttpHandler {
		public void handle(final HttpExchange t) throws IOException {
			try (final OutputStream os = t.getResponseBody()) {
				final byte[] byteArray = ByteArrayUtils.gameListToByteArray(new ArrayList<>(gameList.keySet()));
				System.out.println("Game listed.");
				t.sendResponseHeaders(HttpStatus.SC_OK, byteArray.length);
				os.write(byteArray);
			} catch (final IOException e) {
				t.sendResponseHeaders(HttpStatus.SC_METHOD_FAILURE, 0);
			}
		}
	}

	private static class CreateGameHandler implements HttpHandler {
		public void handle(final HttpExchange t) throws IOException {
			try (final OutputStream os = t.getResponseBody()) {
				final String gameName = t.getRequestHeaders().getFirst("gameName");
				if (!gameList.containsKey(gameName)) {
					final Game g = new Game("p1", "p2");
					gameList.put(gameName, g);
					System.out.println("Game " + gameName + " created.");
					final byte[] byteArray = ByteArrayUtils.gameToByteArray(g);
					t.sendResponseHeaders(HttpStatus.SC_OK, byteArray.length);
					os.write(byteArray);
				} else {
					t.sendResponseHeaders(HttpStatus.SC_FORBIDDEN, 0);
				}
			} catch (final GameCreationException | IOException e) {
				t.sendResponseHeaders(HttpStatus.SC_METHOD_FAILURE, 0);
			}
		}
	}

	private static class UpdateGameHandler implements HttpHandler {
		public void handle(final HttpExchange t) throws IOException {
			try {
				final Game g = ByteArrayUtils.inputStreamToGame(t.getRequestBody());
				final String gameName = t.getRequestHeaders().getFirst("gameName");
				gameList.put(gameName, g);
				System.out.println("Game " + gameName + " updated.");
				t.sendResponseHeaders(HttpStatus.SC_OK, 0);
			} catch (final IOException e) {
				t.sendResponseHeaders(HttpStatus.SC_METHOD_FAILURE, 0);
			}
		}
	}

	private static class DeleteGameHandler implements HttpHandler {
		public void handle(final HttpExchange t) throws IOException {
			try {
				final String gameName = t.getRequestHeaders().getFirst("gameName");
				gameList.remove(gameName);
				System.out.println("Game " + gameName + " deleted.");
				t.sendResponseHeaders(HttpStatus.SC_OK, 0);
			} catch (final IOException e) {
				t.sendResponseHeaders(HttpStatus.SC_METHOD_FAILURE, 0);
			}
		}
	}

}

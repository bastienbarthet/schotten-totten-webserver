package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;


public class GameServer {

	private final static Map<String, byte[]> gameList = new HashMap<>();

	public static void main(String[] args) throws Exception {
		final int port = Integer.valueOf(System.getenv("PORT"));
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
				final String responseContent = new Date().toString() + " - it is time to SCHOTTEN !!!!";
				final byte[] byteArray = responseContent.getBytes();
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
					final byte[] byteArray = gameList.get(gameName);
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
		
		private byte[] gameListToByteArray(final ArrayList<String> gameList) throws IOException {
			try (final ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
				final ObjectOutput out = new ObjectOutputStream(bos);   
				out.writeObject(gameList);
				out.flush();
				return bos.toByteArray();
			}
		}	
			
		public void handle(final HttpExchange t) throws IOException {
			try (final OutputStream os = t.getResponseBody()) {
				final byte[] byteArray = gameListToByteArray(new ArrayList<>(gameList.keySet()));
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
			try {
				final String gameName = t.getRequestHeaders().getFirst("gameName");
				if (!gameList.containsKey(gameName)) {
					final byte[] game = IOUtils.toByteArray(t.getRequestBody());					
					gameList.put(gameName, game);
					System.out.println("Game " + gameName + " created.");
					t.sendResponseHeaders(HttpStatus.SC_OK, 0);
				} else {
					t.sendResponseHeaders(HttpStatus.SC_FORBIDDEN, 0);
				}
			} catch (final IOException e) {
				t.sendResponseHeaders(HttpStatus.SC_METHOD_FAILURE, 0);
			}
		}
	}

	private static class UpdateGameHandler implements HttpHandler {
		public void handle(final HttpExchange t) throws IOException {
			try {
				final byte[] game = IOUtils.toByteArray(t.getRequestBody());
				final String gameName = t.getRequestHeaders().getFirst("gameName");
				gameList.put(gameName, game);
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

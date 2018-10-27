package server;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.boradgames.bastien.schotten_totten.core.exceptions.GameCreationException;
import com.boradgames.bastien.schotten_totten.core.model.Game;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fi.iki.elonen.NanoHTTPD;

public class LanGameServer extends NanoHTTPD {
	
	private static Map<String, Game> gameMap = new HashMap<>();

    public LanGameServer(int port) {
        super(port);
    }
    
    private Response serializeObjectToResponse(final Object o) {
    	try {
			final String jsonObject = new ObjectMapper().writeValueAsString(o);
			return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", jsonObject);
		} catch (final JsonProcessingException e) {
			return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
		}
    }

    @Override
    public Response serve(IHTTPSession session) {

        final Method method = session.getMethod();
        switch (method) {
        case GET:
        	switch (session.getUri()) {
			case "/ping":
				return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, NanoHTTPD.MIME_PLAINTEXT, new Date().toString() + " - it is time to SCHOTTEN !!!!");
			case "/createGame":
				final String gamename = session.getParameters().get("gamename").get(0);
				if (gameMap.containsKey(gamename)) {
					return serializeObjectToResponse(Boolean.FALSE);
				} else {
					try {
						final Game game = new Game("Player 1", "Player 2");
						gameMap.put(gamename, game);
						return serializeObjectToResponse(Boolean.TRUE);
					} catch (GameCreationException e) {
						return serializeObjectToResponse(Boolean.FALSE);
					}
				}
			case "/getGame":
				return serializeObjectToResponse(gameMap.get(session.getParameters().get("gamename").get(0)));
			case "/listGames":
				return serializeObjectToResponse(new ArrayList<String>(gameMap.keySet()));
			case "/deleteGame":
				return serializeObjectToResponse(gameMap.remove(session.getParameters().get("gamename").get(0)) != null);
			case "/getPlayingPlayer":
				return serializeObjectToResponse(gameMap.get(session.getParameters().get("gamename").get(0)).getPlayingPlayer());
			default:
				return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, NanoHTTPD.MIME_PLAINTEXT, session.getUri() + " not supported.");
			}
		case POST:
			switch (session.getUri()) {
			case "/updateGame":
				final String gamename = session.getParameters().get("gamename").get(0);
				// TODO
				final Game game = null;//session.getParameters().get("game").get(0);
				if (!gameMap.containsKey(gamename)) {
					return serializeObjectToResponse(Boolean.FALSE);
				} else {
					gameMap.put(gamename, game);
					return serializeObjectToResponse(Boolean.TRUE);
				}
			default:
				return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, NanoHTTPD.MIME_PLAINTEXT, session.getUri() + " not supported.");
			}
		default:
			return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, NanoHTTPD.MIME_PLAINTEXT, method + " not supported.");
		}
        
    }

}

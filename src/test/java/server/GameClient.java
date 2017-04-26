package server;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClients;

import com.boardgames.bastien.schotten_totten.model.Game;

public class GameClient {

	private final String serverUrl;

	public GameClient(final String url) {
		serverUrl = url;
	}


	public void createdGame(final String gameName, final Game game) throws HttpException, IOException, GameAlreadyExistsException {
		final HttpClient client = HttpClients.createDefault();
		// post
		final HttpPost post = new HttpPost(serverUrl + "/createGame");
		post.addHeader("gameName", gameName);
		post.setEntity(ByteArrayUtils.gameToByteArrayEntity(game));
		final HttpResponse response = client.execute(post);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_FORBIDDEN) {
			// game already exist
			throw new GameAlreadyExistsException();
		} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_METHOD_FAILURE) {
			// other error
			throw new HttpException(response.getStatusLine().getStatusCode() + "");
		}
	}

	public Game getGame(final String gameName) throws IOException, GameDoNotExistException, HttpException {
		final HttpClient client = HttpClients.createDefault();
		final HttpUriRequest requestGet = new HttpGet(serverUrl + "/getGame");
		requestGet.addHeader("gameName", gameName);
		final HttpResponse responseGet = client.execute(requestGet);
		if (responseGet.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return ByteArrayUtils.inputStreamToGame(responseGet.getEntity().getContent()); 
		} else if (responseGet.getStatusLine().getStatusCode() == HttpStatus.SC_NO_CONTENT) {
			// game does not exist
			throw new GameDoNotExistException();
		} else {
			// other error
			throw new HttpException(responseGet.getStatusLine().getStatusCode() + "");
		}
	}
	
	public ArrayList<String> listGame() throws IOException, HttpException{
		final HttpClient client = HttpClients.createDefault();
		final HttpUriRequest requestGet = new HttpGet(serverUrl + "/listGame");
		final HttpResponse responseGet = client.execute(requestGet);
		if (responseGet.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return ByteArrayUtils.inputStreamToSet(responseGet.getEntity().getContent()); 
		} else {
			// other error
			throw new HttpException();
		}
	}
	
	public void updateGame(final String gameName, final Game game) throws IOException, HttpException {	
		final HttpClient client = HttpClients.createDefault();
		// post
		final HttpPost post = new HttpPost(serverUrl + "/updateGame");
		post.addHeader("gameName", gameName);
		post.setEntity(ByteArrayUtils.gameToByteArrayEntity(game));
		final HttpResponse response = client.execute(post);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			// error
			throw new HttpException(response.getStatusLine().getStatusCode() + "");
		}
	}
	
	public void deleteGame(final String gameName) throws IOException, HttpException {
		final HttpClient client = HttpClients.createDefault();
		final HttpUriRequest request = new HttpGet(serverUrl + "/deleteGame");
		request.addHeader("gameName", gameName);
		final HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			// other error
			throw new HttpException(response.getStatusLine().getStatusCode() + "");
		}
	}
		

}

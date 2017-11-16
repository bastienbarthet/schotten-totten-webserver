package server;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.boardgames.bastien.schotten_totten.model.Player;

public class QuickClientTest {
	
//	@Test
	public void TestPing() {
		
		final RestTemplate rest = new RestTemplate();
		System.out.println(rest.getForObject("http://localhost:8080/ping", String.class).toString());
	}
	
//	@Test
	public void TestList() {
		final RestTemplate rest = new RestTemplate();
		final String gamename = "test-2" + System.currentTimeMillis();
		final String url = "http://localhost:8080/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		System.out.println(result);
		
		// get the game
		final String getPlayingPlayer = "http://localhost:8080/getPlayingPlayer?gamename=" + gamename;
		final Player player = rest.getForObject(getPlayingPlayer, Player.class);
		System.out.println(player.getName() + "-" + player.getPlayerType().toString());
	}

}

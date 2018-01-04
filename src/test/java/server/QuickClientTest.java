package server;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Card.COLOR;
import com.boradgames.bastien.schotten_totten.core.model.Card.NUMBER;
import com.boradgames.bastien.schotten_totten.core.model.Game;
import com.boradgames.bastien.schotten_totten.core.model.Hand;
import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QuickClientTest {

	@Before
	public void Before() {
		final String[] nothing = {};
		RestGameServer.main(nothing);
		Assert.assertTrue(RestGameServer.isActive());
	}

	@After
	public void After() {
		RestGameServer.stop();
		Assert.assertFalse(RestGameServer.isActive());
	}

	@Test
	public void TestPing() {
		final RestTemplate rest = new RestTemplate();
		final String result = rest.getForObject("http://localhost:8080/ping", String.class).toString();
		System.out.println(result);
		Assert.assertTrue(result.contains("it is time to SCHOTTEN !!!!"));
	}

	@Test
	public void TestCreateGame() {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-2" + System.currentTimeMillis();
		final String url = "http://localhost:8080/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		//		System.out.println(result);
		Assert.assertTrue(result);
	}
	
	@Test
	public void TestListGame() {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String url = "http://localhost:8080/createGame?gamename=" + "test-1";
		Assert.assertTrue(rest.getForObject(url, Boolean.class));
		final String url2 = "http://localhost:8080/createGame?gamename=" + "test-2";
		Assert.assertTrue(rest.getForObject(url2, Boolean.class));

		// list
		final String urlList = "http://localhost:8080/listGames";
		final ResponseEntity<String[]> list = rest.getForEntity(urlList, String[].class);
        final List<String> resultAsList =  Arrays.asList(list.getBody());
		Assert.assertTrue(resultAsList.contains("test-1"));
		Assert.assertTrue(resultAsList.contains("test-2"));
	}
	
	@Test
	public void TestDeleteGame() {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-2" + System.currentTimeMillis();
		final String url = "http://localhost:8080/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		Assert.assertTrue(result);
		
		// delete
		final String urlDelete = "http://localhost:8080/deleteGame?gamename=" + gamename;
		Assert.assertTrue(rest.getForObject(urlDelete, Boolean.class));
	}
	
	@Test
	public void TestGetGame() {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-gest" + System.currentTimeMillis();
		final String url = "http://localhost:8080/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		Assert.assertTrue(result);
		
		// get game
		final String urlGet = "http://localhost:8080/getGame?gamename=" + gamename;
		final Game game = rest.getForObject(urlGet, Game.class);
		Assert.assertNotNull(game);
		Assert.assertEquals(PlayingPlayerType.ONE, game.getPlayingPlayer().getPlayerType());
	}
	
	@Test
	public void TestGetPlayingPlayer() throws JsonProcessingException {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-gest" + System.currentTimeMillis();
		final String url = "http://localhost:8080/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		Assert.assertTrue(result);
		
		// get game
		final String urlGet = "http://localhost:8080/getPlayingPlayer?gamename=" + gamename;
		final Player player = rest.getForObject(urlGet, Player.class);
		Assert.assertNotNull(player);
		Assert.assertEquals(PlayingPlayerType.ONE, player.getPlayerType());
	}
	
	@Test
	public void TestUpdateGame() throws JsonProcessingException {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-gest" + System.currentTimeMillis();
		final String url = "http://localhost:8080/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		Assert.assertTrue(result);
		
		// get game
		final String urlGet = "http://localhost:8080/getGame?gamename=" + gamename;
		final Game game = rest.getForObject(urlGet, Game.class);
		Assert.assertNotNull(game);
		Assert.assertEquals(PlayingPlayerType.ONE, game.getPlayingPlayer().getPlayerType());
		
		// swap
		game.swapPlayingPlayerType();
		// send
		final String writeValueAsString = new ObjectMapper().writeValueAsString(game);
		//System.out.println("JSON: " + writeValueAsString);
		final String urlUpdate = "http://localhost:8080/updateGame?gamename=" + gamename;
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		final HttpEntity<String> entity = new HttpEntity<String>(writeValueAsString, headers);
		Assert.assertTrue(rest.postForObject(urlUpdate, entity, Boolean.class));
		
		// get once again
		final Game game2 = rest.getForObject(urlGet, Game.class);
		Assert.assertNotNull(game2);
		Assert.assertEquals(PlayingPlayerType.TWO, game2.getPlayingPlayer().getPlayerType());
		
	}

	@Test
	public void TestJackson() throws IOException, HandFullException {
		final ObjectMapper mapper = new ObjectMapper();
		final String card = mapper.writeValueAsString(new Card(NUMBER.NINE, COLOR.CYAN));
		//		System.out.println(card);
		final Card c = mapper.readValue(card, Card.class);
		//		System.out.println(c.getColor().name() + "-" + c.getNumber().name());
		Assert.assertEquals(NUMBER.NINE, c.getNumber());
		Assert.assertEquals(COLOR.CYAN, c.getColor());

		final Hand handForTest = new Hand();
		handForTest.addCard(c, 0);
		final String hand = mapper.writeValueAsString(handForTest);
		//		System.out.println(hand);
		final Hand h = mapper.readValue(hand, Hand.class);
		//		System.out.println(h.getHandSize());
		Assert.assertEquals(1, h.getHandSize());
		final Card hcard = h.getCards().get(0);
		//		System.out.println(hcard.getColor().name() + "-" + hcard.getNumber().name());
		Assert.assertEquals(NUMBER.NINE, hcard.getNumber());
		Assert.assertEquals(COLOR.CYAN, hcard.getColor());

		final String player = mapper.writeValueAsString(new Player("player1", PlayingPlayerType.ONE));
		System.out.println(player);
		final Player p = mapper.readValue(player, Player.class);
		System.out.println(p.getName() + "-" + p.getPlayerType().toString());
		Assert.assertEquals(PlayingPlayerType.ONE, p.getPlayerType());
		Assert.assertEquals("player1", p.getName());
	}

}

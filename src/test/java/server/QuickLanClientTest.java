package server;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

public class QuickLanClientTest {

	private final String baseUrl = "http://localhost:8080";
	private final LanGameServer lGameServer = new LanGameServer(8080);
	
	@Before
	public void Before() throws IOException {
		lGameServer.start();
		Assert.assertTrue(lGameServer.isAlive());
	}

	@After
	public void After() {
		lGameServer.stop();
		Assert.assertFalse(lGameServer.isAlive());
	}

	@Test
	public void TestPing() {
		final RestTemplate rest = new RestTemplate();
		final String forObject = rest.getForObject(baseUrl + "/ping", String.class);
		System.out.println(forObject);
		Assert.assertTrue(forObject.contains("it is time to SCHOTTEN !!!!"));
	}

	@Test
	public void TestCreateGame() {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-2" + System.currentTimeMillis();
		final String url = baseUrl + "/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		//		System.out.println(result);
		Assert.assertTrue(result);
	}
	
	@Test
	public void TestListGame() {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String url = baseUrl + "/createGame?gamename=" + "test-1";
		Assert.assertTrue(rest.getForObject(url, Boolean.class));
		final String url2 = baseUrl + "/createGame?gamename=" + "test-2";
		Assert.assertTrue(rest.getForObject(url2, Boolean.class));

		// list
		final String urlList = baseUrl + "/listGames";
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
		final String url = baseUrl + "/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		Assert.assertTrue(result);
		
		// delete
		final String urlDelete = url + "/deleteGame?gamename=" + gamename;
		Assert.assertTrue(rest.getForObject(urlDelete, Boolean.class));
	}
	
	@Test
	public void TestGetGame() {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-gest" + System.currentTimeMillis();
		final String url = baseUrl + "/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		Assert.assertTrue(result);
		
		// get game
		final String urlGet = baseUrl + "/getGame?gamename=" + gamename;
		final Game game = rest.getForObject(urlGet, Game.class);
		Assert.assertNotNull(game);
		Assert.assertEquals(PlayingPlayerType.ONE, game.getPlayingPlayer().getPlayerType());
	}
	
	@Test
	public void TestGetPlayingPlayer() throws JsonProcessingException {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-gest" + System.currentTimeMillis();
		final String url = baseUrl + "/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		Assert.assertTrue(result);
		
		// get game
		final String urlGet = baseUrl + "/getPlayingPlayer?gamename=" + gamename;
		final Player player = rest.getForObject(urlGet, Player.class);
		Assert.assertNotNull(player);
		Assert.assertEquals(PlayingPlayerType.ONE, player.getPlayerType());
	}
	
	@Test
	public void TestUpdateGame() throws JsonProcessingException {
		final RestTemplate rest = new RestTemplate();
		rest.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		final String gamename = "test-gest" + System.currentTimeMillis();
		final String url = baseUrl + "/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		Assert.assertTrue(result);
		
		// get game
		final String urlGet = baseUrl + "/getGame?gamename=" + gamename;
		final Game game = rest.getForObject(urlGet, Game.class);
		Assert.assertNotNull(game);
		Assert.assertEquals(PlayingPlayerType.ONE, game.getPlayingPlayer().getPlayerType());
		
		// swap
		game.swapPlayingPlayerType();
		// send
//		final String writeValueAsString = new ObjectMapper().writeValueAsString(game);
//		System.out.println("JSON: " + writeValueAsString);
		final String urlUpdate = baseUrl + "/updateGame?gamename=" + gamename;
//		final HttpHeaders headers = new HttpHeaders();
//		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//		headers.setContentType(MediaType.APPLICATION_JSON);
//		headers.setContentLength(writeValueAsString.length());
//		headers.setAcceptCharset(new ArrayList<Charset>(Charset.availableCharsets().values()));
//		final HttpEntity<String> entity = new HttpEntity<String>(writeValueAsString, headers);
		Assert.assertTrue(rest.postForObject(urlUpdate, game, Boolean.class));
		
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

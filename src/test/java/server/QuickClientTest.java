package server;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Card.COLOR;
import com.boradgames.bastien.schotten_totten.core.model.Card.NUMBER;
import com.boradgames.bastien.schotten_totten.core.model.Hand;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.MilestonePlayerType;
import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;
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

		// get milestones
		final String getMilestones = "http://localhost:8080/getMilestones?gamename=" + gamename;

		final List<Milestone> milestones = Arrays.asList(rest.getForEntity(getMilestones, Milestone[].class).getBody());
		for (final Milestone m : milestones) {
			//			System.out.println(m.getCaptured().toString());
			Assert.assertEquals(MilestonePlayerType.NONE, m.getCaptured());
		}

		//		// get the last played card
		//		final String getlastPlayedCard = "http://localhost:8080/getLastPlayedCard?gamename=" + gamename;
		//		final Card card = rest.getForObject(getlastPlayedCard, Card.class);
		//		System.out.println(card.getColor().toString() + "-" + card.getNumber().toString());

		// get the player
		final String getPlayingPlayer = "http://localhost:8080/getPlayingPlayer?gamename=" + gamename;
		final Player player = rest.getForObject(getPlayingPlayer, Player.class);
		//		System.out.println(player.getName() + " - " + player.getPlayerType().toString());
		Assert.assertEquals(PlayingPlayerType.ONE, player.getPlayerType());
		Assert.assertEquals("Player 1", player.getName());

		final ResponseEntity<Boolean> resultPlay = rest.getForEntity("http://localhost:8080/playerPlays?"
				+ "gamename=" + gamename
				+ "&p=" + PlayingPlayerType.ONE.toString()
				+ "&indexInPlayingPlayerHand=" + 0
				+ "&milestoneIndex=" + 0, Boolean.class);

		Assert.assertEquals(HttpStatus.OK, resultPlay.getStatusCode());
		Assert.assertTrue(resultPlay.getBody().booleanValue());
		
//		final ResponseEntity<Boolean> resultPlay2 = rest.getForEntity("http://localhost:8080/playerPlays?"
//				+ "gamename=" + gamename
//				+ "&p=" + PlayingPlayerType.TWO.toString()
//				+ "&indexInPlayingPlayerHand=" + 0
//				+ "&milestoneIndex=" + 0, Boolean.class);
//
//		Assert.assertEquals(HttpStatus.UNAUTHORIZED, resultPlay2.getStatusCode());
//		Assert.assertFalse(resultPlay2.getBody().booleanValue());
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

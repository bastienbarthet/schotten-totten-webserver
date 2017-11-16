package server;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Card.COLOR;
import com.boardgames.bastien.schotten_totten.model.Card.NUMBER;
import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.Player;
import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class QuickClientTest {

	public void TestPing() {
		final RestTemplate rest = new RestTemplate();
		System.out.println(rest.getForObject("http://localhost:8042/ping", String.class).toString());
	}

	public void TestCreateGame() {
		final RestTemplate rest = new RestTemplate();
		final String gamename = "test-2" + System.currentTimeMillis();
		final String url = "http://localhost:8042/createGame?gamename=" + gamename;
		final Boolean result = rest.getForObject(url, Boolean.class);
		System.out.println(result);

		// get milestones
		final String getMilestones = "http://localhost:8042/getMilestones?gamename=" + gamename;
		final List<Milestone> milestones = rest.getForObject(getMilestones, List.class);
		for (final Milestone m : milestones) {
			System.out.println(m.getCaptured().toString());
		}

		// get the last played card
		final String getlastPlayedCard = "http://localhost:8042/getLastPlayedCard?gamename=" + gamename;
		final Card card = rest.getForObject(getlastPlayedCard, Card.class);
		System.out.println(card.getColor().toString() + "-" + card.getNumber().toString());

		// get the player
		final String getPlayingPlayer = "http://localhost:8042/getPlayingPlayer?gamename=" + gamename;
		final Player player = rest.getForObject(getPlayingPlayer, Player.class);
		System.out.println(player.getName() + "-" + player.getPlayerType().toString());
	}
	
	@Test
	public void TestJackson() throws IOException, HandFullException {
		final ObjectMapper mapper = new ObjectMapper();
		final String card = mapper.writeValueAsString(new Card(NUMBER.NINE, COLOR.CYAN));
		System.out.println(card);
		final Card c = mapper.readValue(card, Card.class);
		System.out.println(c.getColor().name() + "-" + c.getNumber().name());

		final Hand handForTest = new Hand();
		handForTest.addCard(c);
		final String hand = mapper.writeValueAsString(handForTest);
		System.out.println(hand);
		final Hand h = mapper.readValue(hand, Hand.class);
		System.out.println(h.getHandSize());
		System.out.println(h.getCards().get(0).getColor().name() + "-" + h.getCards().get(0).getNumber().name());
		
		final String player = mapper.writeValueAsString(new Player("player1", PlayingPlayerType.ONE));
		System.out.println(player);
		final Player p = mapper.readValue(player, Player.class);
		System.out.println(p.getName() + "-" + p.getPlayerType().toString());
	}
	
}

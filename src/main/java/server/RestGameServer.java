package server;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.utils.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.utils.bastien.schotten_totten.exceptions.GameCreationException;
import com.utils.bastien.schotten_totten.exceptions.HandFullException;
import com.utils.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.utils.bastien.schotten_totten.exceptions.NoPlayerException;
import com.utils.bastien.schotten_totten.exceptions.NotYourTurnException;
import com.utils.bastien.schotten_totten.model.Card;
import com.utils.bastien.schotten_totten.model.Game;
import com.utils.bastien.schotten_totten.model.Milestone;
import com.utils.bastien.schotten_totten.model.Player;
import com.utils.bastien.schotten_totten.model.PlayingPlayerType;

@SpringBootApplication
public class RestGameServer {
	
	private static Map<String, ServerGameManager> gameManagerMap;

	public static void main(String[] args) {
		gameManagerMap = new HashMap<>();
		SpringApplication.run(RestGameServer.class, args);
	}

	@RestController
	private class Controlers {

		@RequestMapping("/ping")
		public String ping() {
			return new Date().toString() + " - it is time to SCHOTTEN !!!!";
		}
		
		@RequestMapping("/createGame")
		public boolean createGame(@RequestParam(value="gamename") String gamename) {
			if (gameManagerMap.containsKey(gamename)) {
				return false;
			} else {
				try {
					final ServerGameManager gameManager = new ServerGameManager("Player 1", "Player 2", gamename);
					gameManagerMap.put(gamename, gameManager);
					return true;
				} catch (GameCreationException e) {
					return false;
				}
			}
		}
		
		@RequestMapping("/getLastPlayedCard")
	    public Card getLastPlayedCard(@RequestParam(value="gamename") String gamename) {
	        return gameManagerMap.get(gamename).getGame().getGameBoard().getLastPlayedCard();
	    }

	    @RequestMapping("/reclaimMilestone")
	    public boolean reclaimMilestone(
	    		@RequestParam(value="gamename") String gamename, 
	    		@RequestParam(value="p") PlayingPlayerType p, 
	    		@RequestParam(value="milestoneIndex") int milestoneIndex) throws NotYourTurnException {
	        final Game game = gameManagerMap.get(gamename).getGame();
			if (game.getPlayingPlayerType() == p) {
	            final Milestone milestone = game.getGameBoard().getMilestones().get(milestoneIndex);
	            return milestone.reclaim(p, game.getGameBoard().getCardsNotYetPlayed());
	        } else {
	            throw new NotYourTurnException(p);
	        }
	    }

	    @RequestMapping("/getPlayer")
	    public Player getPlayer(
	    		@RequestParam(value="gamename") String gamename, 
	    		@RequestParam(value="p") PlayingPlayerType p) {
	    	final Game game = gameManagerMap.get(gamename).getGame();
	        return game.getPlayer(p);
	    }

	    @RequestMapping("/getPlayingPlayer")
	    public Player getPlayingPlayer(@RequestParam(value="gamename") String gamename) {
	    	final Game game = gameManagerMap.get(gamename).getGame();
	        return game.getPlayingPlayer();
	    }

	    @RequestMapping("/getWinner")
	    public Player getWinner(@RequestParam(value="gamename") String gamename) throws NoPlayerException {
	    	final Game game = gameManagerMap.get(gamename).getGame();
	        return game.getWinner();
	    }

	    @RequestMapping("/getMilestones")
	    public List<Milestone> getMilestones(@RequestParam(value="gamename") String gamename) {
	    	final Game game = gameManagerMap.get(gamename).getGame();
	        return game.getGameBoard().getMilestones();
	    }

	    @RequestMapping("/playerPlays")
	    public void playerPlays(
	    		@RequestParam(value="gamename") String gamename, 
	    		@RequestParam(value="p") PlayingPlayerType p, 
	    		@RequestParam(value="indexInPlayingPlayerHand") int indexInPlayingPlayerHand, 
	    		@RequestParam(value="milestoneIndex") int milestoneIndex)
	            throws NotYourTurnException, EmptyDeckException, HandFullException, MilestoneSideMaxReachedException {

	    	final Game game = gameManagerMap.get(gamename).getGame();
	        if (game.getPlayingPlayerType() == p) {
	            final Card cardToPlay = game.getPlayingPlayer().getHand().playCard(indexInPlayingPlayerHand);
	            try {
	                game.getGameBoard().getMilestones().get(milestoneIndex).addCard(cardToPlay, p);
	                game.getGameBoard().updateLastPlayedCard(cardToPlay);
	                game.getPlayingPlayer().getHand().addCard(
	                		game.getGameBoard().getDeck().drawCard(), indexInPlayingPlayerHand);
	                game.swapPlayingPlayerType();
	            } catch (final MilestoneSideMaxReachedException e) {
	                game.getPlayingPlayer().getHand().addCard(cardToPlay, indexInPlayingPlayerHand);
	                throw e;
	            }

	        } else {
	            throw new NotYourTurnException(p);
	        }
	    }
		
	}

}

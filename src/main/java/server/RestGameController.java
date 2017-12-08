package server;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.boradgames.bastien.schotten_totten.core.controllers.AbstractGameManager;
import com.boradgames.bastien.schotten_totten.core.controllers.SimpleGameManager;
import com.boradgames.bastien.schotten_totten.core.exceptions.EmptyDeckException;
import com.boradgames.bastien.schotten_totten.core.exceptions.GameCreationException;
import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NoPlayerException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NotYourTurnException;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

/**
 * Created by Bastien on 21/11/2017.
 */

@RestController
public class RestGameController {
	
	private static Map<String, AbstractGameManager> gameManagerMap = new HashMap<>();

	@RequestMapping("/ping")
	public String ping() {
		return new Date().toString() + " - it is time to SCHOTTEN !!!!";
	}
	
	@RequestMapping("/createGame")
	public Boolean createGame(@RequestParam(value="gamename") String gamename) {
		if (gameManagerMap.containsKey(gamename)) {
			return false;
		} else {
			try {
				final AbstractGameManager gameManager = new SimpleGameManager("Player 1", "Player 2", gamename);
				gameManagerMap.put(gamename, gameManager);
				return true;
			} catch (GameCreationException e) {
				return false;
			}
		}
	}
	
    @RequestMapping("/reclaimMilestone")
    public boolean reclaimMilestone(
    		@RequestParam(value="gamename") String gamename, 
    		@RequestParam(value="p") PlayingPlayerType p, 
    		@RequestParam(value="milestoneIndex") int milestoneIndex) throws NotYourTurnException {
    	 return new Boolean(gameManagerMap.get(gamename).reclaimMilestone(p, milestoneIndex));
    }

    @RequestMapping("/getPlayer")
    public Player getPlayer(
    		@RequestParam(value="gamename") String gamename, 
    		@RequestParam(value="p") PlayingPlayerType p) {
        return gameManagerMap.get(gamename).getPlayer(p);
    }

    @RequestMapping("/getPlayingPlayer")
    public Player getPlayingPlayer(@RequestParam(value="gamename") String gamename) {
        return gameManagerMap.get(gamename).getPlayingPlayer();
    }

    @RequestMapping("/getWinner")
    public Player getWinner(@RequestParam(value="gamename") String gamename) throws NoPlayerException {
        return gameManagerMap.get(gamename).getWinner();
    }

    @RequestMapping("/getMilestones")
    public List<Milestone> getMilestones(@RequestParam(value="gamename") String gamename) {
        return gameManagerMap.get(gamename).getMilestones();
    }

    @RequestMapping("/playerPlays")
    public Boolean playerPlays(
    		@RequestParam(value="gamename") String gamename, 
    		@RequestParam(value="p") PlayingPlayerType p, 
    		@RequestParam(value="indexInPlayingPlayerHand") int indexInPlayingPlayerHand, 
    		@RequestParam(value="milestoneIndex") int milestoneIndex)
            throws NotYourTurnException, MilestoneSideMaxReachedException {

    	final AbstractGameManager gameManager = gameManagerMap.get(gamename);
    	try {
            gameManager.playerPlays(p, indexInPlayingPlayerHand, milestoneIndex);
            return true;
        } catch (final EmptyDeckException e) {
            // nothing to send to the client
            e.printStackTrace();
            return false;
        } catch (final HandFullException e) {
            // nothing to send to the client
            e.printStackTrace();
            return false;
        }
    }

}

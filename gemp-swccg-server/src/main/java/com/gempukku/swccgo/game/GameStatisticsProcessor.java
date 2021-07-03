
package com.gempukku.swccgo.game;

import java.util.Random;

import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.communication.InGameStatisticsListener;
import com.gempukku.swccgo.db.InGameStatisticsDAO;
import com.gempukku.swccgo.game.state.GameState;

public class GameStatisticsProcessor implements InGameStatisticsListener {
	private InGameStatisticsDAO _pileCountByTurnDAO;
	private int _currentGameID;

	public GameStatisticsProcessor(InGameStatisticsDAO pileCountByTurnDAO, String currentGameID) {
		_pileCountByTurnDAO = pileCountByTurnDAO;
		
		try {
			_currentGameID = Integer.valueOf(currentGameID);;
		} catch(NumberFormatException e) {
			//if the current game id isn't an integer then generate a random integer to use
			Random r = new Random();
			_currentGameID = r.nextInt((int)Math.pow(2, 20));
		}
	}

	@Override
	public void writePileCounts(final SwccgGame game,boolean gameComplete) {
		GameState gameState = game.getGameState();
		String dark = gameState.getPlayer(Side.DARK);
		String light = gameState.getPlayer(Side.LIGHT);

		int darkReserveDeck = gameState.getReserveDeckSize(dark);
		int lightReserveDeck = gameState.getReserveDeckSize(light);
		int darkHand = gameState.getHand(dark).size();
		int lightHand = gameState.getHand(light).size();
		int darkForcePile = gameState.getForcePileSize(dark);
		int lightForcePile = gameState.getForcePileSize(light);
		int darkUsedPile = gameState.getUsedPile(dark).size();
		int lightUsedPile = gameState.getUsedPile(light).size();
		int darkLostPile = gameState.getLostPile(dark).size();
		int lightLostPile = gameState.getLostPile(light).size();
		int darkOutOfPlay = gameState.getOutOfPlayPile(dark).size();
		int lightOutOfPlay = gameState.getOutOfPlayPile(light).size();
		
		int darkTurn = gameState.getPlayersLatestTurnNumber(dark);
		int lightTurn = gameState.getPlayersLatestTurnNumber(light);
		
		int sequence = darkTurn+lightTurn;
		
		String side;
		if(dark.equals(gameState.getPlayerOrder().getAllPlayers().get(0))) {
			//dark side first
			if(darkTurn>lightTurn)
				side = "Dark";
			else
				side = "Light";
		} else {
			if(lightTurn>darkTurn)
				side = "Light";
			else 
				side = "Dark";
		}
		
		if(gameComplete) {
			sequence++;
			side = "End";
		}

		Integer darkSeconds = game.getSecondsElapsed(dark);
		Integer lightSeconds = game.getSecondsElapsed(light);
		int darkSecondsElapsed = (darkSeconds==null?0:darkSeconds.intValue());
		int lightSecondsElapsed = (lightSeconds==null?0:lightSeconds.intValue());

		_pileCountByTurnDAO.addPileCounts(_currentGameID, sequence, Math.max(darkTurn, lightTurn), side, darkHand, darkReserveDeck, darkForcePile, darkUsedPile, darkLostPile, darkOutOfPlay, lightHand, lightReserveDeck, lightForcePile, lightUsedPile, lightLostPile, lightOutOfPlay, darkSecondsElapsed, lightSecondsElapsed);
		
		if(gameComplete) {
			int updatedGameId = _pileCountByTurnDAO.findGameIDinGameHistory(gameState.getGame().getWinner(),dark,light);
			_pileCountByTurnDAO.updatePileCountsEndOfGame(_currentGameID, updatedGameId);
		}
	
	}

	@Override
	public void writeActivationTotals(SwccgGame game) {
		GameState gameState = game.getGameState();
		String dark = gameState.getPlayer(Side.DARK);
		String light = gameState.getPlayer(Side.LIGHT);

		int darkTurn = gameState.getPlayersLatestTurnNumber(dark);
		int lightTurn = gameState.getPlayersLatestTurnNumber(light);
		
		int sequence = darkTurn+lightTurn;

		int darkActivation = (int) gameState.getPlayersTotalForceGeneration(dark);
		int lightActivation = (int) gameState.getPlayersTotalForceGeneration(light);
		
		
		_pileCountByTurnDAO.updateActivationCounts(_currentGameID, sequence, darkActivation, lightActivation);
	}
}
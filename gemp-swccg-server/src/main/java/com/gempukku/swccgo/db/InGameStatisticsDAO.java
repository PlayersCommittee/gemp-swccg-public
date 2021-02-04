package com.gempukku.swccgo.db;

import com.gempukku.swccgo.db.vo.GameHistoryEntry;
import com.gempukku.swccgo.game.Player;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface InGameStatisticsDAO {
	public void addPileCounts(int gameId, int sequence, int turnNumber, String side, int darkHand, int darkReserveDeck, int darkForcePile, int darkUsedPile, int darkLostPile, int darkOutOfPlay, int lightHand, int lightReserveDeck, int lightForcePile, int lightUsedPile, int lightLostPile, int lightOutOfPlay, int darkSecondsElapsed, int lightSecondsElapsed);
	public void updatePileCountsEndOfGame(int gameId, int updatedGameId);
	public void updateActivationCounts(int gameId, int sequence, int darkActivation, int lightActivation);
	
    public List<GameHistoryEntry> getPileCountByTurn();
	public int findGameIDinGameHistory(String winner, String dark, String light);
}

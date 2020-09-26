package com.gempukku.swccgo.hall;

import com.gempukku.swccgo.game.SwccgCardBlueprintLibrary;
import com.gempukku.swccgo.game.SwccgGameParticipant;

import java.util.List;
import java.util.Map;

public interface HallInfoVisitor {
    enum TableStatus {
        WAITING, PLAYING, FINISHED
    }

    void serverTime(String time);

    void motd(String motd);

    void visitTable(String tableId, String gameId, boolean watchable, TableStatus status, String statusDescription, String formatName, String tournamentName, String tableDesc, List<SwccgGameParticipant> playerIds, Map<String, String> deckArchetypeMap, boolean playing, String winner, boolean hidePlayer, SwccgCardBlueprintLibrary library, boolean hideDesc, boolean hideDecks);

    void visitTournamentQueue(String tournamentQueueKey, int cost, String collectionName, String formatName, String tournamentQueueName, String tournamentPrizes,
                                     String pairingDescription, String startCondition, int playerCount, boolean playerSignedUp, boolean joinable);

    void visitTournament(String tournamentKey, String collectionName, String formatName, String tournamentName, String pairingDescription, String tournamentStage, int round, int playerCount, boolean playerInCompetition);

    void runningPlayerGame(String gameId);
}

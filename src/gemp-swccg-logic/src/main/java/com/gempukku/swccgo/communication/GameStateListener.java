package com.gempukku.swccgo.communication;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.timing.GameStats;

import java.util.Collection;
import java.util.List;

public interface GameStateListener {
    String getPlayerId();

    void cardCreated(PhysicalCard card, GameState gameState, boolean restoreSnapshot);

    void cardReplaced(PhysicalCard card, GameState gameState);

    void locationsRemoved(Collection<Integer> locationIndexes);

    void cardMoved(PhysicalCard card, GameState gameState);

    void cardRotated(PhysicalCard card, GameState gameState);

    void cardFlipped(PhysicalCard card, GameState gameState);

    void cardTurnedOver(PhysicalCard card, GameState gameState);

    void cardsRemoved(String playerPerforming, Collection<PhysicalCard> cards);

    void setPlayerOrder(List<String> playerIds);

    void startBattle(PhysicalCard location, Collection<PhysicalCard> cards);

    void addToBattle(PhysicalCard card, GameState gameState);

    void removeFromBattle(PhysicalCard card, GameState gameState);

    void finishBattle();

    void startAttack(PhysicalCard location, String playerAttacking, String playerDefending, Collection<PhysicalCard> attackingCards, Collection<PhysicalCard> defendingCards);

    void finishAttack();

    void startDuel(PhysicalCard location, Collection<PhysicalCard> cards);

    void finishDuel();

    void startLightsaberCombat(PhysicalCard location, Collection<PhysicalCard> cards);

    void finishLightsaberCombat();

    void startSabacc();

    void revealSabaccHands();

    void finishSabacc();

    void setCurrentPlayerId(String playerId);

    void setCurrentPhase(String currentPhase);

    void sendMessage(String message);

    void sendGameStats(GameStats gameStats);

    void cardAffectedByCard(String playerPerforming, PhysicalCard card, Collection<PhysicalCard> affectedCard, GameState gameState);

    void interruptPlayed(PhysicalCard card, GameState gameState);

    void destinyDrawn(PhysicalCard card, GameState gameState, String destinyText);

    void cardActivated(String playerPerforming, PhysicalCard card, GameState gameState);

    void decisionRequired(String playerId, AwaitingDecision awaitingDecision);
}

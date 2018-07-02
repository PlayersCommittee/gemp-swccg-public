package com.gempukku.swccgo.game.state;

import com.gempukku.polling.LongPollableResource;
import com.gempukku.polling.WaitingRequest;
import com.gempukku.swccgo.communication.GameStateListener;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;
import com.gempukku.swccgo.logic.timing.GameStats;

import java.util.*;

import static com.gempukku.swccgo.game.state.GameEvent.Type.*;

public class GameCommunicationChannel implements GameStateListener, LongPollableResource {
    private List<GameEvent> _events = Collections.synchronizedList(new LinkedList<GameEvent>());
    private String _self;
    private long _lastConsumed = System.currentTimeMillis();
    private int _channelNumber;
    private volatile WaitingRequest _waitingRequest;

    public GameCommunicationChannel(String self, int channelNumber) {
        _self = self;
        _channelNumber = channelNumber;
    }

    @Override
    public String getPlayerId() {
        return _self;
    }

    public int getChannelNumber() {
        return _channelNumber;
    }

    @Override
    public void setPlayerOrder(List<String> participants) {
        List<String> participantIds = new LinkedList<String>(participants);
        appendEvent(new GameEvent(P).participantId(_self).allParticipantIds(participantIds));
    }

    @Override
    public synchronized void unregisterRequest(WaitingRequest waitingRequest) {
        _waitingRequest = null;
    }

    @Override
    public synchronized boolean registerRequest(WaitingRequest waitingRequest) {
        if (!_events.isEmpty())
            return true;

        _waitingRequest = waitingRequest;
        return false;
    }

    private synchronized void appendEvent(GameEvent event) {
        _events.add(event);
        if (_waitingRequest != null) {
            _waitingRequest.processRequest();
            _waitingRequest = null;
        }
    }

    private int[] getCardIds(Collection<PhysicalCard> cards) {
        int[] result = new int[cards.size()];
        int index = 0;
        for (PhysicalCard card : cards) {
            result[index] = card.getCardId();
            index++;
        }
        return result;
    }

    @Override
    public void setCurrentPhase(String phase) {
        appendEvent(new GameEvent(GPC).phase(phase));
    }

    @Override
    public void cardCreated(PhysicalCard card, GameState gameState, boolean restoreSnapshot) {
        if (!card.isNotShownOnUserInterface() && (card.getZone().isPublic() || (card.getZone().isVisibleByOwner() && card.getOwner().equals(_self))))
            appendEvent(new GameEvent(restoreSnapshot ? PCIPAR : PCIP).card(card, gameState, false));
    }

    @Override
    public void cardReplaced(PhysicalCard card, GameState gameState) {
        if (!card.isNotShownOnUserInterface() && (card.getZone().isPublic() || (card.getZone().isVisibleByOwner() && card.getOwner().equals(_self))))
            appendEvent(new GameEvent(RCIP).card(card, gameState, false));
    }

    @Override
    public void locationsRemoved(Collection<Integer> locationIndexes) {
        appendEvent(new GameEvent(RLFP).locationIndexes(locationIndexes));
    }

    @Override
    public void cardMoved(PhysicalCard card, GameState gameState) {
        appendEvent(new GameEvent(MCIP).card(card, gameState, false));
    }

    @Override
    public void cardRotated(PhysicalCard card, GameState gameState) {
        appendEvent(new GameEvent(ROCIP).card(card, gameState, false));
    }

    @Override
    public void cardFlipped(PhysicalCard card, GameState gameState) {
        appendEvent(new GameEvent(FCIP).card(card, gameState, false));
    }

    @Override
    public void cardsRemoved(String zoneOwner, Collection<PhysicalCard> cards) {
        Set<PhysicalCard> removedCardsVisibleByPlayer = new HashSet<PhysicalCard>();
        for (PhysicalCard card : cards) {
            if (!card.isNotShownOnUserInterface() && (card.getZone().isPublic() || (card.getZone().isVisibleByOwner() && card.getOwner().equals(_self))))
                removedCardsVisibleByPlayer.add(card);
        }
        if (!removedCardsVisibleByPlayer.isEmpty())
            appendEvent(new GameEvent(RCFP).otherCardIds(getCardIds(removedCardsVisibleByPlayer)).participantId(zoneOwner));
    }

    @Override
    public void startBattle(PhysicalCard location, Collection<PhysicalCard> cards) {
        GameEvent gameEvent = new GameEvent(SB).locationIndex(location.getLocationZoneIndex()).otherCardIds(getCardIds(cards));
        appendEvent(gameEvent);
    }

    @Override
    public void addToBattle(PhysicalCard card, GameState gameState) {
        appendEvent(new GameEvent(ATB).card(card, gameState, false));
    }

    @Override
    public void removeFromBattle(PhysicalCard card, GameState gameState) {
        appendEvent(new GameEvent(RFB).card(card, gameState, false));
    }

    @Override
    public void finishBattle() {
        appendEvent(new GameEvent(EB));
    }

    @Override
    public void startAttack(PhysicalCard location, String playerAttacking, String playerDefending, Collection<PhysicalCard> attackingCards, Collection<PhysicalCard> defendingCards) {
        GameEvent gameEvent = new GameEvent(SA).locationIndex(location.getLocationZoneIndex()).otherCardIds(getCardIds(attackingCards)).otherCardIds2(getCardIds(defendingCards));
        gameEvent.playerAttacking(playerAttacking).playerDefending(playerDefending);
        appendEvent(gameEvent);
    }

    @Override
    public void finishAttack() {
        appendEvent(new GameEvent(EA));
    }

    @Override
    public void startDuel(PhysicalCard location, Collection<PhysicalCard> cards) {
        GameEvent gameEvent = new GameEvent(SD).locationIndex(location.getLocationZoneIndex()).otherCardIds(getCardIds(cards));
        appendEvent(gameEvent);
    }

    @Override
    public void finishDuel() {
        appendEvent(new GameEvent(ED));
    }

    @Override
    public void startLightsaberCombat(PhysicalCard location, Collection<PhysicalCard> cards) {
        GameEvent gameEvent = new GameEvent(SLC).locationIndex(location.getLocationZoneIndex()).otherCardIds(getCardIds(cards));
        appendEvent(gameEvent);
    }

    @Override
    public void finishLightsaberCombat() {
        appendEvent(new GameEvent(ELC));
    }

    @Override
    public void startSabacc() {
        appendEvent(new GameEvent(SS));
    }

    @Override
    public void revealSabaccHands() {
        appendEvent(new GameEvent(RSH));
    }

    @Override
    public void finishSabacc() {
        appendEvent(new GameEvent(ES));
    }

    @Override
    public void setCurrentPlayerId(String currentPlayerId) {
        appendEvent(new GameEvent(TC).participantId(currentPlayerId));
    }

    @Override
    public void sendMessage(String message) {
        appendEvent(new GameEvent(M).message(message));
    }

    @Override
    public void sendGameStats(GameStats gameStats) {
        appendEvent(new GameEvent(GS).gameStats(gameStats.makeACopy(_self)));
    }

    @Override
    public void cardAffectedByCard(String playerPerforming, PhysicalCard card, Collection<PhysicalCard> affectedCards, GameState gameState) {
        appendEvent(new GameEvent(CAC).card(card, gameState, true).participantId(playerPerforming).otherCardIds(getCardIds(affectedCards)));
    }

    @Override
    public void interruptPlayed(PhysicalCard card, GameState gameState) {
        appendEvent(new GameEvent(IP).card(card, gameState, true));
    }

    @Override
    public void destinyDrawn(PhysicalCard card, GameState gameState, String destinyText) {
        appendEvent(new GameEvent(DD).card(card, gameState, false).destinyText(destinyText));
    }

    @Override
    public void cardActivated(String playerPerforming, PhysicalCard card, GameState gameState) {
        appendEvent(new GameEvent(CA).card(card, gameState, false).participantId(playerPerforming));
    }

    @Override
    public void decisionRequired(String playerId, AwaitingDecision decision) {
        if (playerId.equals(_self))
            appendEvent(new GameEvent(D).awaitingDecision(decision).participantId(playerId));
    }

    public List<GameEvent> consumeGameEvents() {
        updateLastAccess();
        List<GameEvent> result = _events;
        _events = Collections.synchronizedList(new LinkedList<GameEvent>());
        return result;
    }

    private void updateLastAccess() {
        _lastConsumed = System.currentTimeMillis();
    }

    public long getLastAccessed() {
        return _lastConsumed;
    }
}

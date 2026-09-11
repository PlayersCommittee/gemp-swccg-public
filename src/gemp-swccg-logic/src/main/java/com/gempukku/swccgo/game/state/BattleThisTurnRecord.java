package com.gempukku.swccgo.game.state;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Snapshot of a battle that occurred this turn, retained for cards that care about
 * battles after the BattleState has been cleared (e.g. Apology Accepted).
 */
public class BattleThisTurnRecord {
    private final int _locationCardId;
    private final String _winner;
    private final String _loser;
    private final Map<String, Float> _battleDamageByPlayer;
    private final Set<Integer> _participantCardIds;

    public BattleThisTurnRecord(int locationCardId, String winner, String loser,
                                Map<String, Float> battleDamageByPlayer, Set<Integer> participantCardIds) {
        _locationCardId = locationCardId;
        _winner = winner;
        _loser = loser;
        _battleDamageByPlayer = new HashMap<String, Float>(battleDamageByPlayer);
        _participantCardIds = new HashSet<Integer>(participantCardIds);
    }

    public int getLocationCardId() {
        return _locationCardId;
    }

    public String getWinner() {
        return _winner;
    }

    public String getLoser() {
        return _loser;
    }

    public boolean wasLostBy(String playerId) {
        return playerId != null && playerId.equals(_loser);
    }

    public float getBattleDamageFor(String playerId) {
        Float damage = _battleDamageByPlayer.get(playerId);
        return damage != null ? damage : 0f;
    }

    public boolean wasParticipant(int cardId) {
        return _participantCardIds.contains(cardId);
    }

    public Set<Integer> getParticipantCardIds() {
        return Collections.unmodifiableSet(_participantCardIds);
    }
}

package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.communication.UserFeedback;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DefaultUserFeedback implements UserFeedback {
    private SwccgGame _game;
    private Map<String, AwaitingDecision> _awaitingDecisionMap = new HashMap<String, AwaitingDecision>();
    private Map<String, String> _warnings = new HashMap<String, String>();
    private boolean _darkPlayerMadeAtLeastOneDecision;
    private boolean _lightPlayerMadeAtLeastOneDecision;

    public void setGame(SwccgGame game) {
        _game = game;
    }

    public void participantDecided(String playerId) {
        _awaitingDecisionMap.remove(playerId);
        _game.getGameState().playerDecisionFinished(playerId);

        if (_game.getDarkPlayer().equals(playerId))
            _darkPlayerMadeAtLeastOneDecision = true;
        else
            _lightPlayerMadeAtLeastOneDecision = true;
    }

    public AwaitingDecision getAwaitingDecision(String playerId) {
        return _awaitingDecisionMap.get(playerId);
    }

    @Override
    public void sendAwaitingDecision(String playerId, AwaitingDecision awaitingDecision) {
        _awaitingDecisionMap.put(playerId, awaitingDecision);
        _game.getGameState().playerDecisionStarted(playerId, awaitingDecision);
    }

    @Override
    public void sendWarning(String playerId, String warning) {
        _warnings.put(playerId, warning);
    }

    @Override
    public boolean hasPendingDecisions() {
        return !_awaitingDecisionMap.isEmpty();
    }

    public String consumeWarning(String playerId) {
        return _warnings.remove(playerId);
    }

    public boolean hasWarning(String playerId) {
        return _warnings.containsKey(playerId);
    }

    public boolean haveBothPlayersMadeAtLeastOneDecision() {
        return _darkPlayerMadeAtLeastOneDecision && _lightPlayerMadeAtLeastOneDecision;
    }

    public Set<String> getUsersPendingDecision() {
        return _awaitingDecisionMap.keySet();
    }
}

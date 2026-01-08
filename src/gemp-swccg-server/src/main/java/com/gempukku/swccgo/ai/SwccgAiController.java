package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;

public interface SwccgAiController {
    String decide(String playerId, AwaitingDecision decision, GameState gameState);
}

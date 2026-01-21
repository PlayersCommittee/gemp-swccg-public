package com.gempukku.swccgo.ai;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.AwaitingDecision;

public interface SwccgAiController {
    String decide(String playerId, AwaitingDecision decision, GameState gameState);

    /**
     * Set the current game reference for advanced AI features.
     * Default implementation does nothing for backward compatibility.
     */
    default void setGame(SwccgGame game) {
        // Default: no-op for AIs that don't need the full game reference
    }

    /**
     * Get the next chat message to send, if any.
     * Default implementation returns null (no chat).
     *
     * @return message to send, or null if no message
     */
    default String getChatMessage() {
        return null;
    }
}

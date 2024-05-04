package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during Force retrieval when the Force retrieval is initiated.
 */
public class ForceRetrievalInitiatedResult extends EffectResult {

    /**
     * Creates an effect result that is triggered during Force retrieval when the Force retrieval is initiated.
     * @param performingPlayerId the player to retrieve Force
     */
    public ForceRetrievalInitiatedResult(String performingPlayerId) {
        super(Type.FORCE_RETRIEVAL_INITIATED, performingPlayerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Force retrieval initiated";
    }
}

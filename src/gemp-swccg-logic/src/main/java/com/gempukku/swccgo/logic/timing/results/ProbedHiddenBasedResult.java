package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when a 'Hidden Base' system is 'probed'.
 */
public class ProbedHiddenBasedResult extends EffectResult {

    /**
     * Creates an effect result that is emitted when a 'Hidden Base' system is 'probed' by the specified player.
     * @param playerId the player
     */
    public ProbedHiddenBasedResult(String playerId) {
        super(Type.PROBED_HIDDEN_BASE, playerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "'Hidden Base' just 'probed'";
    }
}

package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a player recirculates.
 */
public class RecirculateResult extends EffectResult {

    /**
     * Creates an effect result that is emitted when a player recirculates.
     * @param performingPlayerId the performing player
     */
    public RecirculateResult(String performingPlayerId) {
        super(Type.RECIRCULATED, performingPlayerId);
    }
}

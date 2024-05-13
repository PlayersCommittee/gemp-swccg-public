package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when the initial attrition amounts are calculated.
 */
public class InitialAttritionCalculatedResult extends EffectResult {

    /**
     * Creates an effect result that is emitted when the initial attrition amounts are calculated.
     * @param battleInitiatorPlayerId the player that initiated the battle
     */
    public InitialAttritionCalculatedResult(String battleInitiatorPlayerId) {
        super(Type.INITIAL_ATTRITION_CALCULATED, battleInitiatorPlayerId);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Initial attrition calculated";
    }
}

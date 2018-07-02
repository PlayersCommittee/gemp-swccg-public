package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when an attribute value (power, forfeit, hyperspeed, etc.) of a card is reset or modified by an action.
 */
public class ResetOrModifyCardAttributeResult extends EffectResult {

    /**
     * Creates an effect result that is emitted when an attribute value (power, forfeit, hyperspeed, etc.) of a card is
     * reset or modified by an action.
     * @param performingPlayer the player that performed the action
     * @param card the card whose attribute value was reset or modified
     */
    public ResetOrModifyCardAttributeResult(String performingPlayer, PhysicalCard card) {
        super(Type.ATTRIBUTE_RESET_OR_MODIFIED, performingPlayer);
    }
}

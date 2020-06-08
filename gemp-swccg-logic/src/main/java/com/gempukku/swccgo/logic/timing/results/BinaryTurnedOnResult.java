package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a binary droid is turned on.
 */
public class BinaryTurnedOnResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is emitted when a binary droid is turned on.
     * @param performingPlayer the player that performed the action
     * @param card the card whose game text is canceled
     */
    public BinaryTurnedOnResult(String performingPlayer, PhysicalCard card) {
        super(Type.TURN_ON_BINARY_DROID, performingPlayer);
        _card = card;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just turned on " + GameUtils.getCardLink(_card);
    }
}

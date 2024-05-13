package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card is suspended.
 */
public class SuspendCardResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is emitted when a card is suspended.
     * @param performingPlayer the player that performed the action
     * @param card the card that is suspended
     */
    public SuspendCardResult(String performingPlayer, PhysicalCard card) {
        super(Type.SUSPEND_CARD, performingPlayer);
        _card = card;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just suspended " + GameUtils.getCardLink(_card);
    }
}

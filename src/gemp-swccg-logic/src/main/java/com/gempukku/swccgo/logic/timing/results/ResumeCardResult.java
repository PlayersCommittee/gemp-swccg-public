package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a suspended card is resumed.
 */
public class ResumeCardResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is emitted when a suspended card is resumed.
     * @param performingPlayer the player that performed the action
     * @param card the suspended card that is resumed
     */
    public ResumeCardResult(String performingPlayer, PhysicalCard card) {
        super(Type.RESUME_CARD, performingPlayer);
        _card = card;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just resumed " + GameUtils.getCardLink(_card);
    }
}

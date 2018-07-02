package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card's forfeit is reduced to zero.
 */
public class ForfeitReducedToZeroResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is emitted a card's forfeit is reduced to zero.
     * @param performingPlayer the player that performed the action
     * @param card the card whose forfeit value was reduced to zero
     */
    public ForfeitReducedToZeroResult(String performingPlayer, PhysicalCard card) {
        super(Type.FORFEIT_REDUCED_TO_ZERO, performingPlayer);
        _card = card;
    }

    /**
     * Gets the card.
     * @return the card
     */
    public PhysicalCard getCard() {
        return _card;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_card) + "'s forfeit reduced to 0";
    }
}

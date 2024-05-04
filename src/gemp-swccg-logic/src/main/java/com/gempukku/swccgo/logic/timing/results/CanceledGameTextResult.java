package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a card's game text is canceled.
 */
public class CanceledGameTextResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is emitted when a card's game text is canceled.
     * @param performingPlayer the player that performed the action
     * @param card the card whose game text is canceled
     */
    public CanceledGameTextResult(String performingPlayer, PhysicalCard card) {
        super(Type.CANCELED_GAME_TEXT, performingPlayer);
        _card = card;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just canceled " + GameUtils.getCardLink(_card) + "'s game text";
    }
}

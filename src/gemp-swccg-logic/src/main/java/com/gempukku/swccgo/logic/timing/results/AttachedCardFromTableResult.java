package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when a card from table is attached to another card.
 */
public class AttachedCardFromTableResult extends EffectResult {
    private PhysicalCard _cardToAttach;
    private PhysicalCard _attachToCard;

    /**
     * Creates an effect result that is emitted when a card from table is attached to another card.
     * @param performingPlayerId the performing player
     * @param cardToAttach the card attached
     * @param attachToCard the card it is attached to
     */
    public AttachedCardFromTableResult(String performingPlayerId, PhysicalCard cardToAttach, PhysicalCard attachToCard) {
        super(Type.ATTACH_FROM_TABLE, performingPlayerId);
        _cardToAttach = cardToAttach;
        _attachToCard = attachToCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Attached " + GameUtils.getCardLink(_cardToAttach) + " to " + GameUtils.getCardLink(_attachToCard);
    }
}

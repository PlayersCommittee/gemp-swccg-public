package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * An effect result that is emitted when an 'insert' card is revealed.
 */
public class InsertCardRevealedResult extends EffectResult {
    private PhysicalCard _physicalCard;

    /**
     * Creates an effect result that is emitted when an 'insert' card is revealed.
     * @param physicalCard the 'insert' card
     */
    public InsertCardRevealedResult(PhysicalCard physicalCard) {
        super(Type.INSERT_CARD_REVEALED, null);
        _physicalCard = physicalCard;
    }

    /**
     * Gets the 'insert' card that was revealed.
     * @return the 'insert' card
     */
    public PhysicalCard getCard() {
        return _physicalCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "'Insert' card, " + GameUtils.getCardLink(_physicalCard) + ", is revealed";
    }
}

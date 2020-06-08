package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a card is 'bullseyed'.
 */
public class BullseyedResult extends EffectResult {
    private PhysicalCard _cardBullseyed;
    private PhysicalCard _bullseyedByCard;

    /**
     * Creates the effect result that is emitted when a card is 'bullseyed'.
     * @param cardBullseyed the card that was 'bullseyed'
     * @param bullseyedByCard the card that 'bullseyed'
     */
    public BullseyedResult(PhysicalCard cardBullseyed, PhysicalCard bullseyedByCard) {
        super(Type.BULLSEYED, bullseyedByCard.getOwner());
        _cardBullseyed = cardBullseyed;
        _bullseyedByCard = bullseyedByCard;
    }

    /**
     * Gets the card that was 'bullseyed'.
     * @return the card
     */
    public PhysicalCard getCardBullseyed() {
        return _cardBullseyed;
    }

    /**
     * Gets the card that 'bullseyed'.
     * @return the card
     */
    public PhysicalCard getBullseyedByCard() {
        return _bullseyedByCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_cardBullseyed) + " just 'bullseyed by " + GameUtils.getCardLink(_bullseyedByCard);
    }
}

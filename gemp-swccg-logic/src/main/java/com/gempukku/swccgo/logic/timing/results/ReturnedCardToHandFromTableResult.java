package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a card is returned to hand from table.
 */
public class ReturnedCardToHandFromTableResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is triggered when a card is returned to hand from table.
     * @param action the action performing this effect result
     * @param card the card
     */
    public ReturnedCardToHandFromTableResult(Action action, PhysicalCard card) {
        this(action, action.getPerformingPlayer(), card);
        _card = card;
    }

    /**
     * Creates an effect result that is triggered when a card is returned to hand from table.
     * @param action the action performing this effect result
     * @param performingPlayerId the performing player
     * @param card the card
     */
    public ReturnedCardToHandFromTableResult(Action action, String performingPlayerId, PhysicalCard card) {
        super(Type.RETURNED_TO_HAND_FROM_TABLE, performingPlayerId);
        _card = card;
    }

    /**
     * Gets the card returned to hand from table.
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
        return "Just returned card to hand";
    }
}

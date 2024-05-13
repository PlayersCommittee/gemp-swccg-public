package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a card is placed in Used Pile from table.
 */
public class PutCardInUsedPileFromTableResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is triggered when a card is place in Used Pile from table.
     * @param action the action performing this effect result
     * @param card the card
     */
    public PutCardInUsedPileFromTableResult(Action action, PhysicalCard card) {
        this(action, action.getPerformingPlayer(), card);
    }

    /**
     * Creates an effect result that is triggered when a card is place in Used Pile from table.
     * @param action the action performing this effect result
     * @param performingPlayerId the performing player
     * @param card the card
     */
    public PutCardInUsedPileFromTableResult(Action action, String performingPlayerId, PhysicalCard card) {
        super(Type.PUT_IN_USED_PILE_FROM_TABLE, performingPlayerId);
        _card = card;
    }

    /**
     * Gets the card placed in the Used Pile from table.
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
        return "Just placed card in Used Pile";
    }
}

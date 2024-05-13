package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a card is lost that was not on the table (e.g. in a card pile, in hand, etc.).
 */
public class LostCardFromOffTableResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is triggered when a card is lost that was not on the table (e.g. in a card pile,
     * in hand, etc.).
     * @param action the action performing this effect result
     * @param card the card
     */
    public LostCardFromOffTableResult(Action action, PhysicalCard card) {
        this(action, action.getPerformingPlayer(), card);
    }

    /**
     * Creates an effect result that is triggered when a card is place is lost that was not on the table (e.g. in a card
     * pile, in hand, etc.).
     * @param action the action performing this effect result
     * @param performingPlayerId the performing player
     * @param card the card
     */
    public LostCardFromOffTableResult(Action action, String performingPlayerId, PhysicalCard card) {
        super(Type.LOST_FROM_OFF_TABLE, performingPlayerId);
        _card = card;
    }

    /**
     * Gets the card lost from that was not on the table (e.g. in a card pile, in hand, etc.).
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
        return "Just lost card";
    }
}

package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a card is placed out of play that was not on the table (e.g. in a card pile,
 * in hand, etc.).
 */
public class PlacedCardOutOfPlayFromOffTableResult extends EffectResult {
    private PhysicalCard _card;

    /**
     * Creates an effect result that is triggered when a card is placed out of play that was not on the table (e.g. in
     * a card pile, in hand, etc.).
     * @param action the action performing this effect result
     * @param card the card
     */
    public PlacedCardOutOfPlayFromOffTableResult(Action action, PhysicalCard card) {
        this(action, action.getPerformingPlayer(), card);
    }

    /**
     * Creates an effect result that is triggered when a card is placed out of play that was not on the table (e.g. in
     * a card pile, in hand, etc.).
     * @param action the action performing this effect result
     * @param performingPlayerId the performing player
     * @param card the card
     */
    public PlacedCardOutOfPlayFromOffTableResult(Action action, String performingPlayerId, PhysicalCard card) {
        super(Type.PLACED_OUT_OF_PLAY_FROM_OFF_TABLE, performingPlayerId);
        _card = card;
    }

    /**
     * Gets the card placed out of play that was not on the table (e.g. in a card pile, in hand, etc.).
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
        return "Placed " + GameUtils.getCardLink(_card) + " out of play";
    }
}

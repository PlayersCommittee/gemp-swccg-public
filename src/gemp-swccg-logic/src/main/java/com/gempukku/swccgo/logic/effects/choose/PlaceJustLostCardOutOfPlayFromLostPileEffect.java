package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * Places a card that was just lost, forfeited, or canceled from table out of play from Lost Pile.
 * Emits an off-table out-of-play result marked as while-just-lost-from-table so cards such as
 * I Can't Believe He's Gone can respond without inspecting the action stack.
 */
public class PlaceJustLostCardOutOfPlayFromLostPileEffect extends PlaceCardOutOfPlayFromLostPileEffect {

    /**
     * Creates an effect that places a just-lost-from-table card out of play from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param cardPileOwner the card pile owner
     * @param filters the filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public PlaceJustLostCardOutOfPlayFromLostPileEffect(Action action, String playerId, String cardPileOwner, Filterable filters, boolean reshuffle) {
        super(action, playerId, cardPileOwner, filters, reshuffle);
        setWhileJustLostFromTable(true);
    }
}

package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect to place a stacked card out of play.
 */
public class PlaceStackedCardOutOfPlayEffect extends PlaceStackedCardsOutOfPlayEffect {

    /**
     * Creates an effect that causes the player to place a card accepted by the specified filter that is stacked on the specified
     * card out of play.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOn the card that the stacked cards are stacked on
     * @param filters the filter
     */
    public PlaceStackedCardOutOfPlayEffect(Action action, String playerId, PhysicalCard stackedOn, Filterable filters) {
        super(action, playerId, 1, 1, stackedOn, filters);
    }

    /**
     * Creates an effect that causes the player to place a card accepted by the specified filter that is stacked on a
     * card accepted by the stacked on filter out of play.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOnFilters the stackedOn filter
     * @param filters the filter
     */
    public PlaceStackedCardOutOfPlayEffect(Action action, String playerId, Filterable stackedOnFilters, Filterable filters) {
        super(action, playerId, 1, 1, stackedOnFilters, filters);
    }
}

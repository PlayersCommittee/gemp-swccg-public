package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to take a stacked card into hand.
 */
public class TakeStackedCardIntoHandEffect extends TakeStackedCardsIntoHandEffect {

    /**
     * Creates an effect that causes the player to take a card stacked on the specified card into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOn the card that the stacked cards are stacked on
     */
    public TakeStackedCardIntoHandEffect(Action action, String playerId, PhysicalCard stackedOn) {
        super(action, playerId, 1, 1, stackedOn);
    }

    /**
     * Creates an effect that causes the player to take a card accepted by the specified filter that are stacked on the specified
     * card into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOn the card that the stacked cards are stacked on
     * @param filters the filter
     */
    public TakeStackedCardIntoHandEffect(Action action, String playerId, PhysicalCard stackedOn, Filterable filters) {
        super(action, playerId, 1, 1, stackedOn, filters);
    }

    /**
     * Creates an effect that causes the player to take a card stacked on a card accepted by the specified stackedOn filter
     * into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOnFilters the stackedOn filter
     */
    public TakeStackedCardIntoHandEffect(Action action, String playerId, Filterable stackedOnFilters) {
        super(action, playerId, 1, 1, stackedOnFilters);
    }

    /**
     * Creates an effect that causes the player to take a card accepted by the specified filter that are stacked on the specified
     * card into hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOnFilters the stackedOn filter
     * @param filters the filter
     */
    public TakeStackedCardIntoHandEffect(Action action, String playerId, Filterable stackedOnFilters, Filterable filters) {
        super(action, playerId, 1, 1, stackedOnFilters, filters);
    }
}

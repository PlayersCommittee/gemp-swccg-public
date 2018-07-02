package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to choose cards from Used Pile.
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the cards selected or any targeting
 * reasons. This is just choosing cards, and calling the cardsSelected method with the collection of cards chosen.
 */
public abstract class ChooseCardsFromUsedPileEffect extends ChooseCardsFromPileEffect {

    /**
     * Creates an effect that causes the player to choose cards from Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     */
    public ChooseCardsFromUsedPileEffect(Action action, String playerId, int minimum, int maximum) {
        super(action, playerId, Zone.USED_PILE, playerId, minimum, maximum, maximum, false, false, Filters.any);
    }

    /**
     * Creates an effect that causes the player to choose cards accepted by the specified filter from Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseCardsFromUsedPileEffect(Action action, String playerId, int minimum, int maximum, Filterable filters) {
        super(action, playerId, Zone.USED_PILE, playerId, minimum, maximum, maximum, false, false, filters);
    }
}

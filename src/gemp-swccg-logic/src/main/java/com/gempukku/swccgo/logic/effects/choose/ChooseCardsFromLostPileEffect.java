package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to choose cards from Lost Pile.
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the cards selected or any targeting
 * reasons. This is just choosing cards, and calling the cardsSelected method with the collection of cards chosen.
 */
public abstract class ChooseCardsFromLostPileEffect extends ChooseCardsFromPileEffect {

    /**
     * Creates an effect that causes the player to choose cards from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     */
    public ChooseCardsFromLostPileEffect(Action action, String playerId, int minimum, int maximum) {
        super(action, playerId, Zone.LOST_PILE, playerId, minimum, maximum, maximum, false, false, Filters.any);
    }

    /**
     * Creates an effect that causes the player to choose cards accepted by the specified filter from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseCardsFromLostPileEffect(Action action, String playerId, int minimum, int maximum, Filterable filters) {
        super(action, playerId, Zone.LOST_PILE, playerId, minimum, maximum, maximum, false, false, filters);
    }

    /**
     * Creates an effect that causes the player to choose cards accepted by the specified filter from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param filters the filter
     */
    public ChooseCardsFromLostPileEffect(Action action, String playerId, String zoneOwner, int minimum, int maximum, Filterable filters) {
        super(action, playerId, Zone.LOST_PILE, zoneOwner, minimum, maximum, maximum, false, false, filters);
    }

    /**
     * Creates an effect that causes the player to choose cards accepted by the specified filter from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param topmost true if only the topmost cards should be chosen from, otherwise false
     * @param filters the filter
     */
    protected ChooseCardsFromLostPileEffect(Action action, String playerId, int minimum, int maximum, boolean topmost, Filterable filters) {
        super(action, playerId, Zone.LOST_PILE, playerId, minimum, maximum, maximum, false, topmost, filters);
    }

    /**
     * Creates an effect that causes the player to choose cards accepted by the specified filter from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param minimum the minimum number of cards to choose
     * @param maximum the maximum number of cards to choose
     * @param maximumAcceptsCount the maximum number of times cards may be accepted by the filter, which will further limit
     *                            cards that can be selected when cards with multiple model types accept filter multiple times
     * @param matchPartialModelType true if card with multiple model types (i.e. squadrons) match if any model type
     *                              matches the filter otherwise card only matches if all model types match the filter
     * @param topmost true if only the topmost cards should be chosen from, otherwise false
     * @param filters the filter
     */
    protected ChooseCardsFromLostPileEffect(Action action, String playerId, int minimum, int maximum, int maximumAcceptsCount, boolean matchPartialModelType, boolean topmost, Filterable filters) {
        super(action, playerId, Zone.LOST_PILE, playerId, minimum, maximum, maximumAcceptsCount, matchPartialModelType, topmost, filters);
    }
}

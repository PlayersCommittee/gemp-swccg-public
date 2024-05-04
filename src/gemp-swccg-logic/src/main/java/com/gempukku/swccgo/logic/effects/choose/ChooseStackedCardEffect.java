package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes the specified player to choose a card accepted by the specified filter that is stacked on the
 * specified card (or stacked on a card accepted by the specified stackedOn filter).
 *
 * Note: The choosing of a card provided by this effect does not involve persisting the card selected or any targeting
 * reasons. This is just choosing a card, and calling the cardSelected method with the card chosen.
 */
public abstract class ChooseStackedCardEffect extends ChooseStackedCardsEffect {

    /**
     * Creates an effect that causes the player to choose a card stacked on the specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOn the card that cards are stacked on
     */
    public ChooseStackedCardEffect(Action action, String playerId, PhysicalCard stackedOn) {
        super(action, playerId, stackedOn, 1, 1);
    }

    /**
     * Creates an effect that causes the player to choose a card accepted by the specified filter stacked on the specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOn the card that cards are stacked on
     * @param filters the filter
     */
    public ChooseStackedCardEffect(Action action, String playerId, PhysicalCard stackedOn, Filterable filters) {
        super(action, playerId, stackedOn, 1, 1, filters);

    }

    /**
     * Creates an effect that causes the player to choose a card accepted by the specified filter stacked on the specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOn the card that cards are stacked on
     * @param filters the filter
     * @param doNotShowCardFront true if the card fronts are not to be shown, otherwise false
     */
    public ChooseStackedCardEffect(Action action, String playerId, PhysicalCard stackedOn, Filterable filters, boolean doNotShowCardFront) {
        super(action, playerId, stackedOn, 1, 1, filters, doNotShowCardFront);
    }

    /**
     * Creates an effect that causes the player to choose a card stacked on a card accepted by the specified stackedOn filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOnFilters the stackedOn filter
     */
    public ChooseStackedCardEffect(Action action, String playerId, Filterable stackedOnFilters) {
        super(action, playerId, stackedOnFilters, 1, 1);
    }

    /**
     * Creates an effect that causes the player to choose a card accepted by the specified filter that are stacked
     * on a card accepted by the specified stackedOn filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOnFilters the stackedOn filter
     * @param filters the filter
     */
    public ChooseStackedCardEffect(Action action, String playerId, Filterable stackedOnFilters, Filterable filters) {
        super(action, playerId, stackedOnFilters, 1, 1, filters);
    }

    /**
     * Creates an effect that causes the player to choose a card accepted by the specified filter that are stacked
     * on a card accepted by the specified stackedOn filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackedOnFilters the stackedOn filter
     * @param filters the filter
     */
    public ChooseStackedCardEffect(Action action, String playerId, Filterable stackedOnFilters, Filterable filters, boolean doNotShowCardFront) {
        super(action, playerId, stackedOnFilters, 1, 1, filters, doNotShowCardFront);
    }

    @Override
    protected final void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
        if (selectedCards.size() == 1)
            cardSelected(selectedCards.iterator().next());
    }

    protected abstract void cardSelected(PhysicalCard selectedCard);
}

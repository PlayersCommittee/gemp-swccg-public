package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes the specified player to choose a card from hand or on table.
 *
 * Note: The choosing of a card provided by this effect does not involve persisting the card selected or any targeting
 * reasons. This is just choosing a card, and calling the cardSelected method with the card chosen.
 */
public abstract class ChooseCardFromHandOrOnTableEffect extends ChooseCardsFromHandOrOnTableEffect {

    /**
     * Creates an effect that causes the player to choose a card from hand or on table.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public ChooseCardFromHandOrOnTableEffect(Action action, String playerId) {
        super(action, playerId, 1, 1);
    }

    /**
     * Creates an effect that causes the player to choose a card from hand or on table accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     */
    public ChooseCardFromHandOrOnTableEffect(Action action, String playerId, Filterable filters) {
        super(action, playerId, playerId, 1, 1, filters);
    }

    @Override
    protected final void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
        if (selectedCards.size() == 1)
            cardSelected(game, selectedCards.iterator().next());
    }

    protected abstract void cardSelected(SwccgGame game, PhysicalCard selectedCard);
}

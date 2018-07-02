package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;


/**
 * An effect that causes the specified player to choose a card from the specified card pile.
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the card selected or any targeting
 * reasons. This is just choosing a card, and calling the cardSelected method with the collection of cards chosen.
 */
public abstract class ChooseCardFromPileEffect extends ChooseCardsFromPileEffect {

    /**
     * Creates an effect that causes the player to choose a card from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param zone the card pile
     * @param zoneOwner the player owning the card pile
     */
    public ChooseCardFromPileEffect(Action action, String playerId, Zone zone, String zoneOwner) {
        super(action, playerId, zone, zoneOwner, 1, 1, 1, false, false, Filters.any);
    }

    /**
     * Creates an effect that causes the player to choose a card accepted by the specified filter from the specified card
     * pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param zone the card pile
     * @param zoneOwner the player owning the card pile
     * @param filters the filter
     */
    public ChooseCardFromPileEffect(Action action, String playerId, Zone zone, String zoneOwner, Filterable filters) {
        super(action, playerId, zone, zoneOwner, 1, 1, 1, false, false, filters);
    }

    @Override
    protected final void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
        if (selectedCards.size() == 1)
            cardSelected(game, selectedCards.iterator().next());
    }

    protected abstract void cardSelected(SwccgGame game, PhysicalCard selectedCard);
}

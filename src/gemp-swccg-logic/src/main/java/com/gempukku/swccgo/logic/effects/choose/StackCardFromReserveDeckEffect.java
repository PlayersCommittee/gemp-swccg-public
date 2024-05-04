package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and stack a card from Reserve Deck.
 */
public class StackCardFromReserveDeckEffect extends StackCardFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and stack a card accepted by the card filter
     * from Reserve Deck on the specified card.
     * @param action the action performing this effect
     * @param stackOn the card to stack a card on
     * @param cardFilter the card filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     */
    public StackCardFromReserveDeckEffect(Action action, PhysicalCard stackOn, Filter cardFilter, boolean reshuffle) {
        this(action, action.getPerformingPlayer(), stackOn, cardFilter, reshuffle, false);
    }

    /**
     * Creates an effect that causes the player performing the action to choose and stack a card accepted by the card filter
     * from Reserve Deck on the specified card.
     * @param action the action performing this effect
     * @param playerId the player to stack the card
     * @param stackOn the card to stack a card on
     * @param cardFilter the card filter
     * @param reshuffle true if pile is reshuffled, otherwise false
     * @param viaJediTest5 true if stacked upside-down to be used as substitute destiny via Jedi Test #5, otherwise false
     */
    public StackCardFromReserveDeckEffect(Action action, String playerId, PhysicalCard stackOn, Filter cardFilter, boolean reshuffle, boolean viaJediTest5) {
        super(action, playerId, stackOn, Zone.RESERVE_DECK, cardFilter, reshuffle, viaJediTest5);
    }
}

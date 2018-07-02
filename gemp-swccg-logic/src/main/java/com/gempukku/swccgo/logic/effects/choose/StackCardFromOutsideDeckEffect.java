package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that stacks cards from outside of deck on a card.
 */
public class StackCardFromOutsideDeckEffect extends StackCardsFromOutsideDeckEffect {

    /**
     * Creates an effect that stacks a card from outside of deck on a specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackOn the card to stack on
     * @param cardFilter the filter for the card to be stacked
     */
    public StackCardFromOutsideDeckEffect(Action action, String playerId, PhysicalCard stackOn, Filterable cardFilter) {
        super(action, playerId, 1, 1, stackOn, cardFilter);
    }
}
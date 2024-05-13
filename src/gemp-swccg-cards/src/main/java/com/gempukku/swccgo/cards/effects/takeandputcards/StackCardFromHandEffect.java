package com.gempukku.swccgo.cards.effects.takeandputcards;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to stack a card from hand.
 */
public class StackCardFromHandEffect extends StackCardsFromHandEffect {

    /**
     * Creates an effect that causes the player to stack a card from hand on the specified card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackOn the card to stack the cards on
     * @param faceDown true if cards are to be stacked face down, otherwise false
     */
    public StackCardFromHandEffect(Action action, String playerId, PhysicalCard stackOn, boolean faceDown) {
        super(action, playerId, 1, 1, stackOn, faceDown);
    }

    /**
     * Creates an effect that causes the player to stack a card accepted by the specified filter from hand on the specified
     * card.
     * @param action the action performing this effect
     * @param playerId the player
     * @param stackOn the card to stack the cards on
     * @param faceDown true if cards are to be stacked face down, otherwise false
     * @param filters the filter
     */
    public StackCardFromHandEffect(Action action, String playerId, PhysicalCard stackOn, boolean faceDown, Filterable filters) {
        super(action, playerId, 1, 1, stackOn, faceDown, filters);
    }
}

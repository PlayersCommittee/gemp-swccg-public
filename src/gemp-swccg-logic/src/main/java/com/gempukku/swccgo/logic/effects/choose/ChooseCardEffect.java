package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;

/**
 * An effect that causes the specified player to choose a specific card (using card selection).
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the cards selected or any targeting
 * reasons. This is just choosing cards, and calling the cardsSelected method with the card chosen.
 */
public abstract class ChooseCardEffect extends ChooseCardsEffect {
    /**
     * Creates an effect that causes the player to choose a card from the specified collection of cards (using card selection).
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the card
     * @param cards the cards to choose from
     */
    public ChooseCardEffect(Action action, String playerId, String choiceText, Collection<PhysicalCard> cards) {
        super(action, playerId, choiceText, 1, 1, cards);
    }

    @Override
    protected final void cardsSelected(Collection<PhysicalCard> selectedCards) {
        if (selectedCards.size() == 1)
            cardSelected(selectedCards.iterator().next());
    }

    protected abstract void cardSelected(PhysicalCard selectedCard);
}

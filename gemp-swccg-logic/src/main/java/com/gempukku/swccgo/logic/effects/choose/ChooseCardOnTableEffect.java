package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Map;

/**
 * An effect that causes the specified player to choose a card on the table.
 *
 * Note: The choosing of a card provided by this effect does not involve persisting the card selected or any targeting
 * reasons. This is just choosing a card, and calling the cardSelected method with the card chosen.
 */
public abstract class ChooseCardOnTableEffect extends ChooseCardsOnTableEffect {

    /**
     * Creates an effect that causes the player to choose a card from the specified collection of cards on the table.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the card
     * @param cards the cards to choose from
     */
    public ChooseCardOnTableEffect(Action action, String playerId, String choiceText, Collection<PhysicalCard> cards) {
        super(action, playerId, choiceText, 1, 1, cards);
    }


    /**
     * Creates an effect that causes the player to choose a card from the specified collection of cards on the table accepted
     * by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the card
     * @param cards the cards to choose from
     * @param filters the filter
     */
    public ChooseCardOnTableEffect(Action action, String playerId, String choiceText, Collection<PhysicalCard> cards, Filterable filters) {
        super(action, playerId, choiceText, 1, 1, cards, filters);
    }

    /**
     * Creates an effect that causes the player to choose a card on the table accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the card
     * @param filters the filter
     */
    public ChooseCardOnTableEffect(Action action, String playerId, String choiceText, Filterable filters) {
        super(action, playerId, choiceText, 1, 1, filters);
    }

    /**
     * Creates an effect that causes the player to choose a card on the table accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the card
     * @param filters the filter
     */
    public ChooseCardOnTableEffect(Action action, String playerId, String choiceText, Filterable filters, Integer minimum) {
        super(action, playerId, choiceText, minimum, 1, filters);
    }


    /**
     * Creates an effect that causes the player to choose a card on the table accepted by the specified filter.
     * @param action the action performing this effect
     * @param playerId the player
     * @param choiceText the text shown to the player choosing the card
     * @param spotOverrides overrides for which inactive cards are visible
     * @param filters the filter
     */
    public ChooseCardOnTableEffect(Action action, String playerId, String choiceText, Map<InactiveReason, Boolean> spotOverrides, Filterable filters) {
        super(action, playerId, choiceText, 1, 1, spotOverrides, filters);
    }

    @Override
    protected final void cardsSelected(Collection<PhysicalCard> selectedCards) {
        if (selectedCards.size() == 1)
            cardSelected(selectedCards.iterator().next());
    }

    protected abstract void cardSelected(PhysicalCard selectedCard);
}

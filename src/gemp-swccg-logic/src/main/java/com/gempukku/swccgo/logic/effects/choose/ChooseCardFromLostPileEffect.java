package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;


/**
 * An effect that causes the specified player to choose a card from Lost Pile.
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the card selected or any targeting
 * reasons. This is just choosing a card, and calling the cardSelected method with the collection of cards chosen.
 */
public abstract class ChooseCardFromLostPileEffect extends ChooseCardsFromLostPileEffect {

    /**
     * Creates an effect that causes the player to choose a card from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public ChooseCardFromLostPileEffect(Action action, String playerId) {
        super(action, playerId, 1, 1);
    }

    /**
     * Creates an effect that causes the player to choose a card accepted by the specified filter from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     */
    public ChooseCardFromLostPileEffect(Action action, String playerId, Filterable filters) {
        super(action, playerId, 1, 1, filters);
    }

    /**
     * Creates an effect that causes the player to choose a card accepted by the specified filter from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     */
    public ChooseCardFromLostPileEffect(Action action, String playerId, boolean topmost, Filterable filters) {
        super(action, playerId, 1, 1, topmost, filters);
    }

    /**
     * Creates an effect that causes the player to choose a card accepted by the specified filter from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param zoneOwner the owner of the Lost Pile
     * @param filters the filter
     */
    public ChooseCardFromLostPileEffect(Action action, String playerId, String zoneOwner, Filterable filters) {
        super(action, playerId, zoneOwner, 1, 1, filters);
    }

    @Override
    protected final void cardsSelected(SwccgGame game, Collection<PhysicalCard> selectedCards) {
        if (selectedCards.size() == 1)
            cardSelected(game, selectedCards.iterator().next());
    }

    protected abstract void cardSelected(SwccgGame game, PhysicalCard selectedCard);
}

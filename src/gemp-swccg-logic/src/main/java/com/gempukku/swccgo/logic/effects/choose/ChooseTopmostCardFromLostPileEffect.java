package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.logic.timing.Action;


/**
 * An effect that causes the specified player to choose a card from Lost Pile.
 *
 * Note: The choosing of cards provided by this effect does not involve persisting the card selected or any targeting
 * reasons. This is just choosing a card, and calling the cardSelected method with the collection of cards chosen.
 */
public abstract class ChooseTopmostCardFromLostPileEffect extends ChooseCardsFromLostPileEffect {

    /**
     * Creates an effect that causes the player to choose the topmost card accepted by the specified filter from Lost Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     */
    public ChooseTopmostCardFromLostPileEffect(Action action, String playerId, Filterable filters) {
        super(action, playerId, 1, 1, true, filters);
    }
}

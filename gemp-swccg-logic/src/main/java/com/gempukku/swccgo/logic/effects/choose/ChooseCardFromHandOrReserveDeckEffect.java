package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that causes the specified player to choose a card from hand and/or Reserve Deck.
 *
 * Note: The choosing of a card provided by this effect does not involve persisting the card selected or any targeting
 * reasons. This is just choosing a card, and calling the cardsSelected method with the card chosen.
 */
public abstract class ChooseCardFromHandOrReserveDeckEffect extends ChooseCardFromHandOrCardPilesEffect {

    /**
     * Creates an effect that causes the player to choose a card accepted by the specified filter from hand or Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     * @param forDeployment true if include deployable as if from hand, otherwise false
     * @param isOptional true if choosing a card is optional, otherwise false
     */
    public ChooseCardFromHandOrReserveDeckEffect(Action action, String playerId, Filterable filters, boolean forDeployment, boolean isOptional) {
        super(action, playerId, Collections.singletonList(Zone.RESERVE_DECK), playerId, filters, forDeployment, isOptional);
    }
}

package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect to put a card from hand on bottom of Used Pile.
 */
public class PutCardFromHandOnBottomOfUsedPileEffect extends PutCardsFromHandOnBottomOfUsedPileEffect {

    /**
     * Creates an effect that causes the player to put a card from hand on bottom of Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public PutCardFromHandOnBottomOfUsedPileEffect(Action action, String playerId) {
       super(action, playerId, 1, 1);
    }

    /**
     * Creates an effect that causes the player to put a card accepted by the specified filter from hand on bottom of
     * Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     * @param hidden true if cards are not revealed, otherwise false
     */
    public PutCardFromHandOnBottomOfUsedPileEffect(Action action, String playerId, Filterable filters, boolean hidden) {
        super(action, playerId, 1, 1, filters, hidden);
    }
}

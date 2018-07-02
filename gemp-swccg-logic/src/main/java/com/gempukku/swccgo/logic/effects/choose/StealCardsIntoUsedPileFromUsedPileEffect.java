package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

;

/**
 * An effect to steal cards into Used Pile from the opponent's Used Pile.
 */
public class StealCardsIntoUsedPileFromUsedPileEffect extends StealCardsIntoPileFromPileEffect {

    /**
     * Creates an effect that causes the player to steal cards accepted by the specified filter into specified card pile
     * from the specified card pile.
     * @param action the action performing this effect
     * @param playerId the player
     * @param filters the filter
     */
    public StealCardsIntoUsedPileFromUsedPileEffect(Action action, String playerId, Filterable filters) {
        super(action, playerId, Integer.MAX_VALUE, Integer.MAX_VALUE, Zone.USED_PILE, Zone.USED_PILE, false, filters, false);
    }
}

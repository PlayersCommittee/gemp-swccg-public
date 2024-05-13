package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to lose the top card of Used Pile.
 */
public class LoseTopCardOfUsedPileEffect extends LoseTopCardOfCardPileEffect {

    /**
     * Creates an effect that causes the specified player to lose the top card of Used Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public LoseTopCardOfUsedPileEffect(Action action, String playerId) {
        super(action, playerId, Zone.USED_PILE);
    }
}

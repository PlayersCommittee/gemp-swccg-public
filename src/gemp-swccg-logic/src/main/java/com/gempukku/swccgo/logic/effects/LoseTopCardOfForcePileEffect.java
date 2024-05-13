package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to lose the top card of Force Pile.
 */
public class LoseTopCardOfForcePileEffect extends LoseTopCardOfCardPileEffect {

    /**
     * Creates an effect that causes the specified player to lose the top card of Force Pile.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public LoseTopCardOfForcePileEffect(Action action, String playerId) {
        super(action, playerId, Zone.FORCE_PILE);
    }
}

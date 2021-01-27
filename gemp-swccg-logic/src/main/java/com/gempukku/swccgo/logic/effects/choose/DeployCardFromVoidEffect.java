package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.game.DeploymentRestrictionsOption;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy a card from Void
 */
public class DeployCardFromVoidEffect extends DeployCardFromPileEffect {
    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from Reserve Deck.
     * @param action the action performing this effect
     * @param cardFilter the card filter
     * @param forFree true if deploying for free, otherwise false
     */
    public DeployCardFromVoidEffect(Action action, Filter cardFilter, boolean forFree) {
        super(action, Zone.VOID, cardFilter, null, null, forFree, 0, null, false, false);
    }
}

package com.gempukku.swccgo.logic.effects.choose;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the player performing the action to choose and deploy a card from outside the game.
 */
public class DeployCardFromOutsideTheGameEffect extends DeployCardFromPileEffect {

    /**
     * Creates an effect that causes the player performing the action to choose and deploy a card accepted by the card filter
     * from outside the game.
     *
     * @param action       the action performing this effect
     * @param cardFilter   the card filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    public DeployCardFromOutsideTheGameEffect(Action action, Filter cardFilter, float changeInCost) {
        super(action, Zone.OUTSIDE_OF_DECK, cardFilter, null, null, false, changeInCost, null, false, false);
    }
}

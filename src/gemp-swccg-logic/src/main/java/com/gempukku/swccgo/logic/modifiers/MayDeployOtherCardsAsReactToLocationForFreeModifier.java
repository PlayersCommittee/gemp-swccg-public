package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which allows the source card to deploy other specified cards as a 'react' to specified locations for free.
 */
public class MayDeployOtherCardsAsReactToLocationForFreeModifier extends MayDeployOtherCardsAsReactToLocationModifier {

    /**
     * Creates a modifier which allows the source card to deploy other specified cards as a 'react' to locations accepted
     * by the location filter for free.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param playerId the player that may deploy cards as a 'react', or null if either player may deploy cards as a 'react'
     * @param deployFilter the deploy filter
     * @param locationFilter the location filter
     */
    public MayDeployOtherCardsAsReactToLocationForFreeModifier(PhysicalCard source, String actionText, String playerId, Filterable deployFilter, Filterable locationFilter) {
        this(source, actionText, null, playerId, deployFilter, locationFilter, 0);
    }

    /**
     * Creates a modifier which allows the source card to deploy other specified cards as a 'react' to locations accepted
     * by the location filter for free.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may deploy cards as a 'react', or null if either player may deploy cards as a 'react'
     * @param deployFilter the deploy filter
     * @param locationFilter the location filter
     * @param changeInCost change in amount of Force (can be positive or negative) required
     */
    private MayDeployOtherCardsAsReactToLocationForFreeModifier(PhysicalCard source, String actionText, Condition condition, String playerId, Filterable deployFilter, Filterable locationFilter, float changeInCost) {
        super(source, actionText, condition, playerId, deployFilter, locationFilter, changeInCost);
    }

    @Override
    public boolean isReactForFree() {
        return true;
    }
}

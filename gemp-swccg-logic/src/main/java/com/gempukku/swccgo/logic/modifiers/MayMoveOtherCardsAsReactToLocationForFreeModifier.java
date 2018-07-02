package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier which causes the source card to allow other specified cards to move as a 'react' to specified locations.
 */
public class MayMoveOtherCardsAsReactToLocationForFreeModifier extends MayMoveOtherCardsAsReactToLocationModifier {

    /**
     * Creates a modifier which causes the source card to allow other cards accepted by the card filter to move as a 'react'
     * to locations accepted by the target filter for free.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param playerId the player that may move cards as a 'react', or null if either player may move cards as a 'react'
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     */
    public MayMoveOtherCardsAsReactToLocationForFreeModifier(PhysicalCard source, String actionText, String playerId, Filterable cardFilter, Filterable locationFilter) {
        this(source, actionText, null, playerId, cardFilter, locationFilter);
    }

    /**
     * Creates a modifier which causes the source card to allow other cards accepted by the card filter to move as a 'react'
     * to locations accepted by the target filter for free.
     * @param source the source of the modifier
     * @param actionText the text to show for the action on the User Interface
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may move cards as a 'react', or null if either player may move cards as a 'react'
     * @param cardFilter the card filter
     * @param locationFilter the location filter
     */
    private MayMoveOtherCardsAsReactToLocationForFreeModifier(PhysicalCard source, String actionText, Condition condition, String playerId, Filterable cardFilter, Filterable locationFilter) {
        super(source, actionText, condition, playerId, cardFilter, locationFilter, 0);
    }

    @Override
    public boolean isReactForFree() {
        return true;
    }
}

package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to have deploy cost modified.
 */
public class MayNotHaveDeployCostModifiedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents the source card's deploy cost from being modified by the specified player.
     * @param source the source of the modifier
     * @param playerId the player that may not modify deploy cost
     */
    public MayNotHaveDeployCostModifiedModifier(PhysicalCard source, String playerId) {
        this(source, source, null, playerId);
    }

    /**
     * Creates a modifier for not being able to have deploy cost modified.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not have deploy cost modified
     */
    public MayNotHaveDeployCostModifiedModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null, null);
    }

    /**
     * Creates a modifier for not being able to have deploy cost modified.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not have deploy cost modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not modify deploy cost
     */
    public MayNotHaveDeployCostModifiedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, "May not have deploy cost modified", affectFilter, condition, ModifierType.MAY_NOT_HAVE_DEPLOY_COST_MODIFIED, true);
        _playerId = playerId;
    }
}

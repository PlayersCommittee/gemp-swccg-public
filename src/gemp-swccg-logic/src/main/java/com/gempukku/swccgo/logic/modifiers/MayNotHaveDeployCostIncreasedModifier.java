package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to have deploy cost increased.
 */
public class MayNotHaveDeployCostIncreasedModifier extends AbstractModifier {

    /**
     * Creates a modifier for not being able to have deploy cost increased.
     * @param source the source of the modifier and card affected by modifier
     */
    public MayNotHaveDeployCostIncreasedModifier(PhysicalCard source) {
        this(source, source, null, null);
    }

    /**
     * Creates a modifier for not being able to have deploy cost increased.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not have deploy cost increased
     * @param playerId the player that may not increase deploy cost
     */
    public MayNotHaveDeployCostIncreasedModifier(PhysicalCard source, Filterable affectFilter, String playerId) {
        this(source, affectFilter, null, playerId);
    }

    /**
     * Creates a modifier for not being able to have deploy cost increased.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not have deploy cost increased
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player that may not increase deploy cost
     */
    private MayNotHaveDeployCostIncreasedModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, "May not have deploy cost increased", affectFilter, condition, ModifierType.MAY_NOT_HAVE_DEPLOY_COST_INCREASED, true);
        _playerId = playerId;
    }
}

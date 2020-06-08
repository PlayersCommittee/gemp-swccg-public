package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows specified cards to deploy as if from hand.
 */
public class MayDeployAsIfFromHandModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy as if from hand.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayDeployAsIfFromHandModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy as if from hand.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayDeployAsIfFromHandModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May deploy as if from hand", affectFilter, condition, ModifierType.MAY_DEPLOY_AS_IF_FROM_HAND);
    }
}

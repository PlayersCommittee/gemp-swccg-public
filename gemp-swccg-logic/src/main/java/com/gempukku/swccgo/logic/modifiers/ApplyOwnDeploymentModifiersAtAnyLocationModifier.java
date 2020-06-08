package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for causing specified cards to apply their deployment modifiers at any location.
 */
public class ApplyOwnDeploymentModifiersAtAnyLocationModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affects cards to apply their deployment modifiers at any location.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public ApplyOwnDeploymentModifiersAtAnyLocationModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes affects cards to apply their deployment modifiers at any location.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ApplyOwnDeploymentModifiersAtAnyLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Applies own deployment modifiers at any location", affectFilter, condition, ModifierType.APPLIES_OWN_DEPLOYMENT_MODIFIERS_AT_ANY_LOCATION);
    }
}

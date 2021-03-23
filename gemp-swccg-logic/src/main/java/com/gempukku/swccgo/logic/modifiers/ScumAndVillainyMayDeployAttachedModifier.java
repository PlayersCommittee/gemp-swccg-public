package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows Scum And Villainy to be deployed attached to the affected card
 */
public class ScumAndVillainyMayDeployAttachedModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows Scum And Villainy to be deployed attached to the affected card
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public ScumAndVillainyMayDeployAttachedModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Scum And Villainy may deploy here", affectFilter, ModifierType.SCUM_AND_VILLAINY_MAY_DEPLOY_HERE);
    }

    /**
     * Creates a modifier that allows Scum And Villainy to be deployed attached to the affected card
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ScumAndVillainyMayDeployAttachedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Scum And Villainy may deploy here", affectFilter, condition, ModifierType.SCUM_AND_VILLAINY_MAY_DEPLOY_HERE);
    }
}

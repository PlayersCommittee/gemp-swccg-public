package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to ignore location deployment restrictions in their game text.
 */
public class IgnoresLocationDeploymentRestrictionsInGameTextModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes cards accepted by the filter to ignore location deployment restrictions in their game text.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public IgnoresLocationDeploymentRestrictionsInGameTextModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public IgnoresLocationDeploymentRestrictionsInGameTextModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Ignores location deployment restrictions in game text", affectFilter, condition, ModifierType.IGNORES_LOCATION_DEPLOYMENT_RESTRICTIONS_IN_GAME_TEXT);
    }
}

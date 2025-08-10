package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes the affected locations to be rotated.
 * Note: The affected locations are actually rotated by an action triggered by the EffectsOfRevolutionRule class.
 */
public class RotateLocationModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the affected locations to be rotated.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations to be rotated
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public RotateLocationModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.ROTATE_LOCATION);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return self.isInverted() ? "Rotates location" : null;
    }
}

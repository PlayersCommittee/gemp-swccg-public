package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that cancels the effects of Revolution.
 */
public class CancelEffectsOfRevolutionModifier extends AbstractModifier {

    /**
     * Creates a modifier that cancels the effects of Revolution.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public CancelEffectsOfRevolutionModifier(PhysicalCard source, Condition condition) {
        super(source, "Effects of Revolution canceled", Filters.and(Filters.in_play, Filters.Revolution), condition, ModifierType.EFFECTS_OF_REVOLUTION_CANCELED, true);
    }
}

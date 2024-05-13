package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to have defense value increased beyond printed value.
 */
public class MayNotHaveDefenseValueIncreasedAbovePrintedModifier extends AbstractModifier {

    /**
     * Creates a modifier for not being able to have defense value increased beyond printed value.
     *
     * @param source       the source of the modifier
     * @param affectFilter the filter for cards that may not have defense value increased above printed
     */
    public MayNotHaveDefenseValueIncreasedAbovePrintedModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier for not being able to have defense value increased beyond printed value.
     *
     * @param source       the source of the modifier
     * @param affectFilter the filter for cards that may not have defense value increased above printed
     * @param condition    the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotHaveDefenseValueIncreasedAbovePrintedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not have defense increased above printed value", affectFilter, condition, ModifierType.MAY_NOT_HAVE_DEFENSE_VALUE_INCREASED_ABOVE_PRINTED, true);
    }
}

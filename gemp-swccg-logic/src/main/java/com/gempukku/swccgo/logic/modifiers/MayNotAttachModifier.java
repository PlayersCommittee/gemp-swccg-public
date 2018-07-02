package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to 'attach'.
 */
public class MayNotAttachModifier extends AbstractModifier {

    /**
     * Creates a modifier that prohibits cards accepted by the input filter from 'attaching'.
     * @param source the source card of the modifier
     * @param affectFilter the filter
     */
    public MayNotAttachModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prohibits cards accepted by the input filter from 'attaching'.
     * @param source the source card of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotAttachModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not 'attach'", affectFilter, condition, ModifierType.MAY_NOT_ATTACH, true);
    }
}

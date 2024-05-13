package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents specified cards from attempting Jedi Tests.
 */
public class MayNotAttemptJediTestsModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from attempting Jedi Tests.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotAttemptJediTestsModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from attempting Jedi Tests.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotAttemptJediTestsModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not attempt Jedi Tests", affectFilter, condition, ModifierType.MAY_NOT_ATTEMPT_JEDI_TESTS, true);
    }
}

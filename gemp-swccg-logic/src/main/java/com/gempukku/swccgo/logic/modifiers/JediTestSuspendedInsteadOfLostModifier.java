package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected Jedi Tests to be suspended instead of lost when target not on table.
 */
public class JediTestSuspendedInsteadOfLostModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected Jedi Tests to be suspended instead of lost when target not on table.
     * @param source the source of the modifier
     * @param jediTestFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public JediTestSuspendedInsteadOfLostModifier(PhysicalCard source, Filterable jediTestFilter, Condition condition) {
        super(source, "Suspended instead of lost when target not on table", Filters.and(Filters.Jedi_Test, jediTestFilter), condition, ModifierType.JEDI_TEST_SUSPENDED_INSTEAD_OF_LOST, true);
    }
}

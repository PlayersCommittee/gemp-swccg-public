package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected Jedi Tests to be placed on table when completed.
 */
public class PlaceJediTestOnTableWhenCompletedModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected Jedi Tests to be placed on table when completed.
     * @param source the source of the modifier
     * @param jediTestFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public PlaceJediTestOnTableWhenCompletedModifier(PhysicalCard source, Filterable jediTestFilter, Condition condition) {
        super(source, "Placed on table when completed", Filters.and(Filters.Jedi_Test, jediTestFilter), condition, ModifierType.PLACE_JEDI_TEST_ON_TABLE_WHEN_COMPLETED, true);
    }
}

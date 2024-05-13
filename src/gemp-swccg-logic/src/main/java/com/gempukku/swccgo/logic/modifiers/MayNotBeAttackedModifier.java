package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class MayNotBeAttackedModifier extends AbstractModifier {
    private Filter _attackedByFilter;

    public MayNotBeAttackedModifier(PhysicalCard source) {
        this(source, null, source, Filters.any);
    }

    public MayNotBeAttackedModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, null, affectFilter, Filters.any);
    }

    protected MayNotBeAttackedModifier(PhysicalCard source, Condition condition, Filterable affectFilter, Filterable attackedByFilter) {
        super(source, "May not be attacked", affectFilter, condition, ModifierType.MAY_NOT_BE_ATTACKED);
        _attackedByFilter = Filters.and(attackedByFilter);
    }
}

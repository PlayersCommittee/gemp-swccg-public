package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class IsPoweredModifier extends AbstractModifier {
    public IsPoweredModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Is powered", affectFilter, ModifierType.IS_POWERED);
    }

    public IsPoweredModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "Is powered", affectFilter, condition, ModifierType.IS_POWERED);
    }
}

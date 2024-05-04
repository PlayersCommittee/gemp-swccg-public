package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class MayNotTargetToBeTorturedModifier extends AbstractModifier {
    public MayNotTargetToBeTorturedModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not target to be tortured", affectFilter, ModifierType.MAY_NOT_TARGET_TO_BE_TORTURED);
    }

    public MayNotTargetToBeTorturedModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "May not target to be tortured", affectFilter, condition, ModifierType.MAY_NOT_TARGET_TO_BE_TORTURED);
    }
}

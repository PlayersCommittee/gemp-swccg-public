package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class MayNotTargetToBeFrozenModifier extends AbstractModifier {
    public MayNotTargetToBeFrozenModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not target to be frozen", affectFilter, ModifierType.MAY_NOT_TARGET_TO_BE_FROZEN);
    }

    public MayNotTargetToBeFrozenModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "May not target to be frozen", affectFilter, condition, ModifierType.MAY_NOT_TARGET_TO_BE_FROZEN);
    }
}

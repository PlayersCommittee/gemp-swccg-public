package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class RemovePermanentPilotsModifier extends AbstractModifier {
    public RemovePermanentPilotsModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Permanent pilots removed", affectFilter, ModifierType.SUSPEND_PERMANENT_PILOT);
    }

    public RemovePermanentPilotsModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "Permanent pilots removed", affectFilter, condition, ModifierType.SUSPEND_PERMANENT_PILOT);
    }
}

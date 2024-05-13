package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class RemovePermanentAstromechsModifier extends AbstractModifier {
    public RemovePermanentAstromechsModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Permanent astromechs removed", affectFilter, ModifierType.SUSPEND_PERMANENT_ASTROMECH);
    }

    public RemovePermanentAstromechsModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "Permanent astromechs removed", affectFilter, condition, ModifierType.SUSPEND_PERMANENT_ASTROMECH);
    }
}

package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class MayNotAttackModifier extends AbstractModifier {

    public MayNotAttackModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, null, affectFilter);
    }

    public MayNotAttackModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "May not attack", affectFilter, condition, ModifierType.MAY_NOT_ATTACK);
    }
}

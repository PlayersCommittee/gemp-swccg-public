package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class MayNotParticipateInBattleModifier extends AbstractModifier {

    public MayNotParticipateInBattleModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    public MayNotParticipateInBattleModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not participate in battles", affectFilter, condition, ModifierType.MAY_NOT_PARTICIPATE_IN_BATTLE);
    }
}

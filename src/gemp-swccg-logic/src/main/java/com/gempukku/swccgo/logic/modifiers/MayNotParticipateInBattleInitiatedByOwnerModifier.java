package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

public class MayNotParticipateInBattleInitiatedByOwnerModifier extends AbstractModifier {

    public MayNotParticipateInBattleInitiatedByOwnerModifier(PhysicalCard source) {
        this(source, source);
    }

    public MayNotParticipateInBattleInitiatedByOwnerModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, null, affectFilter);
    }

    public MayNotParticipateInBattleInitiatedByOwnerModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, "May not participate in battles initiated by owner", affectFilter, condition, ModifierType.MAY_NOT_PARTICIPATE_IN_BATTLE_INITIATED_BY_OWNER);
    }
}

package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to not satisfy battle damage when forfeited.
 */
public class MayNotBeUsedToSatisfyBattleDamageModifier extends AbstractModifier {

    public MayNotBeUsedToSatisfyBattleDamageModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    public MayNotBeUsedToSatisfyBattleDamageModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be used to satisfy battle damage", affectFilter, condition, ModifierType.MAY_NOT_SATISFY_BATTLE_DAMAGE);
    }
}

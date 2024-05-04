package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to add battle destiny draws.
 */
public class MayNotAddBattleDestinyDrawsModifier extends AbstractModifier {

    /**
     * Creates a modifier for not being able to add battle destiny draws.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public MayNotAddBattleDestinyDrawsModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier for not being able to add battle destiny draws.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotAddBattleDestinyDrawsModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not add battle destiny draws", affectFilter, condition, ModifierType.MAY_NOT_ADD_BATTLE_DESTINY_DRAWS);
    }
}

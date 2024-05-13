package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to apply ability toward drawing battle destiny.
 */
public class MayNotApplyAbilityForBattleDestinyModifier extends AbstractModifier {

    /**
     * Creates a modifier for not being able to apply ability toward drawing battle destiny.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public MayNotApplyAbilityForBattleDestinyModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier for not being able to apply ability toward drawing battle destiny.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotApplyAbilityForBattleDestinyModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not apply ability toward drawing battle destiny", affectFilter, condition, ModifierType.MAY_NOT_APPLY_ABILITY_FOR_BATTLE_DESTINY);
    }
}

package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for permanent pilots not being able to apply ability toward drawing battle destiny.
 */
public class PermanentPilotsMayNotApplyAbilityForBattleDestinyModifier extends AbstractModifier {

    /**
     * Creates a modifier for permanent pilots not being able to apply ability toward drawing battle destiny.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not ability toward drawing battle destiny
     */
    public PermanentPilotsMayNotApplyAbilityForBattleDestinyModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier for permanent pilots not being able to apply ability toward drawing battle destiny.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not ability toward drawing battle destiny
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public PermanentPilotsMayNotApplyAbilityForBattleDestinyModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Permanent pilots may not apply ability toward drawing battle destiny", affectFilter, condition, ModifierType.PERMANENT_PILOTS_MAY_NOT_APPLY_ABILITY_FOR_BATTLE_DESTINY);
    }
}

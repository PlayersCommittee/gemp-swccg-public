package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for not being able to apply ability toward drawing destiny for Sense or Alter.
 */
public class MayNotApplyAbilityForSenseAlterDestinyModifier extends AbstractModifier {

    /**
     * Creates a modifier for not being able to apply ability toward drawing destiny for Sense or Alter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not ability toward drawing destiny for Sense or Alter
     */
    public MayNotApplyAbilityForSenseAlterDestinyModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier for not being able to apply ability toward drawing destiny for Sense or Alter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not ability toward destiny for Sense or Alter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotApplyAbilityForSenseAlterDestinyModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not apply ability toward Sense or Alter destiny", affectFilter, condition, ModifierType.MAY_NOT_APPLY_ABILITY_FOR_SENSE_ALTER_DESTINY);
    }
}

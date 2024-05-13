package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier for being able to apply ability toward drawing battle destiny when a passenger.
 */
public class PassengerAppliesAbilityForBattleDestinyModifier extends AbstractModifier {

    /**
     * Creates a modifier for being able to apply ability toward drawing battle destiny when a passenger.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not ability toward drawing battle destiny
     */
    public PassengerAppliesAbilityForBattleDestinyModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier for being able to apply ability toward drawing battle destiny when a passenger.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not ability toward drawing battle destiny
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public PassengerAppliesAbilityForBattleDestinyModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Applies ability toward drawing battle destiny", affectFilter, condition, ModifierType.PASSENGER_APPLIES_ABILITY_FOR_BATTLE_DESTINY);
    }
}

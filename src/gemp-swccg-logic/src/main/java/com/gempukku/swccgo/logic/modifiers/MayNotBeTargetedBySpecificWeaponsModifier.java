package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for not being able to be targeted by specified cards.
 */
public class MayNotBeTargetedBySpecificWeaponsModifier extends MayNotBeTargetedByWeaponsModifier {
    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be targeted
     * @param weaponFilter the filter for weapons that may not target
     */
    public MayNotBeTargetedBySpecificWeaponsModifier(PhysicalCard source, Filterable affectFilter, Filterable weaponFilter) {
        this(source, affectFilter, null, weaponFilter);
    }

    /**
     * Creates a modifier for not being able to be targeted by specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param weaponFilter the filter for weapons that may not target
     */
    public MayNotBeTargetedBySpecificWeaponsModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable weaponFilter) {
        super(source, affectFilter, condition, weaponFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not be targeted by specified weapons";
    }
}

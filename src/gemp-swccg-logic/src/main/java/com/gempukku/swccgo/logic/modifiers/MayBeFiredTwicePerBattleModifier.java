package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allowed affected weapons to be fired twice per battle.
 */
public class MayBeFiredTwicePerBattleModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows weapons accepted by the filter to be fired twice per battle.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     */
    public MayBeFiredTwicePerBattleModifier(PhysicalCard source, Filterable weaponFilter) {
        this(source, weaponFilter, null);
    }

    /**
     * Creates a modifier that allows weapons accepted by the filter to be fired twice per battle.
     * @param source the source of the modifier
     * @param weaponFilter the weapon filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayBeFiredTwicePerBattleModifier(PhysicalCard source, Filterable weaponFilter, Condition condition) {
        super(source, "May be fired twice per battle", Filters.and(Filters.weapon, weaponFilter), condition, ModifierType.MAY_FIRE_TWICE_PER_BATTLE, true);
    }
}

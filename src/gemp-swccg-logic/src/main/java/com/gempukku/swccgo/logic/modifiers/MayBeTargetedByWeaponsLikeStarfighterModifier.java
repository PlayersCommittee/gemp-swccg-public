package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows cards to be targeted by weapons like a starfighter.
 */
public class MayBeTargetedByWeaponsLikeStarfighterModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows source card to be targeted by weapons like a starfighter.
     * @param source the source of the modifier
     */
    public MayBeTargetedByWeaponsLikeStarfighterModifier(PhysicalCard source) {
        this(source, null);
    }

    /**
     * Creates a modifier that allows source card to be targeted by weapons like a starfighter.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayBeTargetedByWeaponsLikeStarfighterModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that allows cards accepted by filter to be targeted by weapons like a starfighter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may be targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayBeTargetedByWeaponsLikeStarfighterModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May be targeted by weapons like a starfighter", affectFilter, condition, ModifierType.TARGETED_BY_WEAPONS_LIKE_A_STARFIGHTER);
    }
}

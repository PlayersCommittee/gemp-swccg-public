package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to be considered out of play
 */
public class ConsideredOutOfPlayModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected cards to be considered out of play
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public ConsideredOutOfPlayModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Considered out of play", affectFilter, ModifierType.CONSIDERED_OUT_OF_PLAY);
    }

    /**
     * Creates a modifier that causes affected cards to be considered out of play
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ConsideredOutOfPlayModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Considered out of play", affectFilter, condition, ModifierType.CONSIDERED_OUT_OF_PLAY);
    }
}

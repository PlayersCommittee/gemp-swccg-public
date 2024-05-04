package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents cards affected from being placed out of play.
 */
public class MayNotBePlacedOutOfPlayModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from being placed out of play.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBePlacedOutOfPlayModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being placed out of play.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBePlacedOutOfPlayModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be placed out of play", affectFilter, condition, ModifierType.MAY_NOT_BE_PLACED_OUT_OF_PLAY);
    }
}

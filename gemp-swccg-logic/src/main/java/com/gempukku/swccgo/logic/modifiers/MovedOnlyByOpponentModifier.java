package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to be moved by opponent instead of owner.
 */
public class MovedOnlyByOpponentModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes cards accepted by the filter to be moved by opponent instead of owner.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MovedOnlyByOpponentModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to be moved by opponent instead of owner.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MovedOnlyByOpponentModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Moved only be opponent", affectFilter, condition, ModifierType.MOVED_ONLY_BY_OPPONENT);
    }
}

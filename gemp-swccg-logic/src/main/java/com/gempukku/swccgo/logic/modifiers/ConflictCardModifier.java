package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to be 'conflict' cards
 */
public class ConflictCardModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected cards to be 'conflict' cards
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public ConflictCardModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Is a 'conflict' card", affectFilter, ModifierType.CONFLICT_CARD);
    }

    /**
     * Creates a modifier that causes affected cards to be 'conflict' cards
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ConflictCardModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Is a 'conflict' card", affectFilter, condition, ModifierType.CONFLICT_CARD);
    }
}

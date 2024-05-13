package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to be 'credit' cards
 */
public class CreditCardModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected cards to be 'credit' cards
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     */
    public CreditCardModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "Is a 'conflict' card", affectFilter, ModifierType.CREDIT_CARD);
    }

    /**
     * Creates a modifier that causes affected cards to be 'credit' cards
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public CreditCardModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Is a 'credit' card", affectFilter, condition, ModifierType.CREDIT_CARD);
    }
}

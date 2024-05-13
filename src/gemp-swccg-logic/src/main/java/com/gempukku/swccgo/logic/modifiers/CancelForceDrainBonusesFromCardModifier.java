package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes Force drain bonuses from specified cards to be canceled.
 */
public class CancelForceDrainBonusesFromCardModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes Force drain bonuses from cards accepted by the filter to be canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public CancelForceDrainBonusesFromCardModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes Force drain bonuses from cards accepted by the filter to be canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public CancelForceDrainBonusesFromCardModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Force drain bonuses canceled", affectFilter, condition, ModifierType.CANCEL_FORCE_DRAIN_BONUSES_FROM_CARD, true);
    }
}

package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents Force drain modifiers from specified cards from being canceled.
 */
public class ForceDrainModifiersMayNotBeCanceledModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents Force drain modifiers from cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public ForceDrainModifiersMayNotBeCanceledModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, null, affectFilter);
    }

    /**
     * Creates a modifier that prevents Force drain modifiers from cards accepted by the filter from being canceled.
     * @param source the source of the modifier
     * @param condition the condition
     * @param affectFilter the filter
     */
    private ForceDrainModifiersMayNotBeCanceledModifier(PhysicalCard source, Condition condition, Filterable affectFilter) {
        super(source, null, affectFilter, condition, ModifierType.FORCE_DRAIN_MODIFIERS_MAY_NOT_BE_CANCELED, true);
    }
}

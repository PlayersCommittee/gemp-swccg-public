package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows affected cards to initiate a Force drain.
 */
public class MayForceDrainModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows the source card to initiate a Force drain.
     * @param source the card that is the source of the modifier and that is allowed to initiate a Force drain
     */
    public MayForceDrainModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that allows the source card to initiate a Force drain.
     * @param source the card that is the source of the modifier and that is allowed to initiate a Force drain
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayForceDrainModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to be initiate a Force drain.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayForceDrainModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to be initiate a Force drain.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayForceDrainModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May Force drain", affectFilter, condition, ModifierType.MAY_FORCE_DRAIN);
    }
}

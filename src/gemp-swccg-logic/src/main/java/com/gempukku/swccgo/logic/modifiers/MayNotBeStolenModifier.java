package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents specified cards from being stolen.
 */
public class MayNotBeStolenModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents the source card from being stolen.
     * @param source the source of the modifier
     */
    public MayNotBeStolenModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being stolen.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeStolenModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents the source card from being stolen.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeStolenModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being stolen.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeStolenModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be stolen", affectFilter, condition, ModifierType.MAY_NOT_BE_STOLEN, true);
    }
}

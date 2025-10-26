package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected cards from being 'choked'.
 */
public class MayNotBeChokedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'choked'.
     * @param source the card that is the source of the modifier and that may not be 'choked'
     */
    public MayNotBeChokedModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'choked'.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeChokedModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'choked'.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotBeChokedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not be 'choked'", affectFilter, condition, ModifierType.MAY_NOT_BE_CHOKED);
    }
}

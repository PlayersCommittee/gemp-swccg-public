package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prohibits affected cards from contributing to Force retrieval.
 */
public class MayNotContributeToForceRetrievalModifier extends AbstractModifier {

    /**
     * Creates a modifier that prohibits affected cards from contributing to Force retrieval.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to fire weapons
     */
    public MayNotContributeToForceRetrievalModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents the source card from contributing to Force retrieval.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to fire weapons
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    private MayNotContributeToForceRetrievalModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not contribute to Force retrieval", affectFilter, condition, ModifierType.MAY_NOT_CONTRIBUTE_TO_FORCE_RETRIEVAL);
    }
}

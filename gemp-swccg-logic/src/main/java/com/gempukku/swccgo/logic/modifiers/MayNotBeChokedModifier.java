package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that prevents affected cards from being 'choked'.
 */
public class MayNotBeChokedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'choked'.
     * @param source the card that is the source of the modifier and that may not be 'choked'
     */
    public MayNotBeChokedModifier(PhysicalCard source) {
        this(source, source);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'choked'.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeChokedModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not be 'choked'", affectFilter, ModifierType.MAY_NOT_BE_CHOKED);
    }
}

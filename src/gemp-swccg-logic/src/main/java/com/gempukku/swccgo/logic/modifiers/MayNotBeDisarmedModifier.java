package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that prevents affected cards from being 'Disarmed'.
 */
public class MayNotBeDisarmedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'Disarmed'.
     * @param source the card that is the source of the modifier and that may not be 'Disarmed'
     */
    public MayNotBeDisarmedModifier(PhysicalCard source) {
        this(source, source);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from being 'Disarmed'.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotBeDisarmedModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not be Disarmed", affectFilter, ModifierType.MAY_NOT_BE_DISARMED);
    }
}

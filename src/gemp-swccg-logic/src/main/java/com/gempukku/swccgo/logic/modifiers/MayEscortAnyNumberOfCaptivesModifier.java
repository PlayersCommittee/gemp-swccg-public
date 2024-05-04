package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that allows affected cards to escort any number of captives.
 */
public class MayEscortAnyNumberOfCaptivesModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows source card to escort any number of captives.
     * @param source the source of the modifier
     */
    public MayEscortAnyNumberOfCaptivesModifier(PhysicalCard source) {
        this(source, source);
    }

    /**
     * Creates a modifier that allows accepted by the filter to escort any number of captives.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayEscortAnyNumberOfCaptivesModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May escort captive", affectFilter, null, ModifierType.MAY_ESCORT_ANY_NUMBER_OF_CAPTIVES);
    }
}

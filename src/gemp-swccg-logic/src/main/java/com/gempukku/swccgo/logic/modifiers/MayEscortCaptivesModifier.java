package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that allows affected cards to escort a captive.
 */
public class MayEscortCaptivesModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows source card to escort captive.
     * @param source the source of the modifier
     */
    public MayEscortCaptivesModifier(PhysicalCard source) {
        this(source, source);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to escort captives.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayEscortCaptivesModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May escort a captive", affectFilter, null, ModifierType.MAY_ESCORT_A_CAPTIVE);
    }
}

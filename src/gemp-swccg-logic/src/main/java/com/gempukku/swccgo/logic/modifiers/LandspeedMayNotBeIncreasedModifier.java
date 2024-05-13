package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prohibits specified card from having their landspeed increased.
 */
public class LandspeedMayNotBeIncreasedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prohibits the source card from having its landspeed increased.
     * @param source the card that is the source of the modifier and is affected by this modifier
     */
    public LandspeedMayNotBeIncreasedModifier(PhysicalCard source) {
        this(source, source);
    }

    /**
     * Creates a modifier that prohibits cards accepted by the filter from having landspeed increased.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public LandspeedMayNotBeIncreasedModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prohibits cards accepted by the filter from having landspeed increased.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public LandspeedMayNotBeIncreasedModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "Landspeed may not be increased", affectFilter, condition, ModifierType.MAY_NOT_HAVE_LANDSPEED_INCREASED);
    }
}

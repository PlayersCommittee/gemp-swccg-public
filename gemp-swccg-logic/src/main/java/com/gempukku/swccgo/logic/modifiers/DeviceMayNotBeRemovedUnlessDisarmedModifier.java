package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;

/**
 * A modifier that prevents affected devices from being removed (unless attached to card is Disarmed).
 */
public class DeviceMayNotBeRemovedUnlessDisarmedModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents devices accepted by the filter from being removed (unless attached to card is Disarmed).
     * @param source the card that is the source of the modifier and that may not be removed
     */
    public DeviceMayNotBeRemovedUnlessDisarmedModifier(PhysicalCard source) {
        this(source, source);
    }

    /**
     * Creates a modifier that prevents devices accepted by the filter from being removed (unless attached to card is Disarmed).
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public DeviceMayNotBeRemovedUnlessDisarmedModifier(PhysicalCard source, Filterable affectFilter) {
        super(source, "May not be removed unless Disarmed", Filters.and(Filters.device, affectFilter), ModifierType.DEVICE_MAY_NOT_BE_REMOVED_UNLESS_DISARMED);
    }
}

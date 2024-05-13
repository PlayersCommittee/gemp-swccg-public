package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allowed affected cards to use any number of devices.
 */
public class MayUseAnyNumberOfDevicesModifier extends AbstractModifier {

    /**
     * Creates a modifier that allows source card to use any number of devices.
     * @param source the card that is the source card and that is affected by this modifier.
     */
    public MayUseAnyNumberOfDevicesModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that allows source card to use any number of devices.
     * @param source the card that is the source card and that is affected by this modifier.
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayUseAnyNumberOfDevicesModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to use any number of devices.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayUseAnyNumberOfDevicesModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to use any number of devices.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayUseAnyNumberOfDevicesModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May use any number of devices", affectFilter, condition, ModifierType.MAY_USE_ANY_NUMBER_OF_DEVICES, true);
    }
}

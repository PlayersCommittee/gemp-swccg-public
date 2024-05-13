package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents affected cards from using any devices.
 */
public class MayNotUseDevicesModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents source card from using any devices.
     * @param source the card that is the source card and that is affected by this modifier.
     */
    public MayNotUseDevicesModifier(PhysicalCard source) {
        this(source, source, null);
    }

    /**
     * Creates a modifier that prevents source card from using any devices.
     * @param source the card that is the source card and that is affected by this modifier.
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotUseDevicesModifier(PhysicalCard source, Condition condition) {
        this(source, source, condition);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from using any devices.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayNotUseDevicesModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that prevents cards accepted by the filter from using any devices.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotUseDevicesModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not use devices", affectFilter, condition, ModifierType.MAY_NOT_USE_DEVICES, true);
    }
}

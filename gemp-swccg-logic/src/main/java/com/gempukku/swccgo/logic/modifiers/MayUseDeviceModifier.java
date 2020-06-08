package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows a card to use specified devices.
 */
public class MayUseDeviceModifier extends AbstractModifier {
    private Filter _deviceFilter;

    /**
     * Creates modifier that allows the source card to use devices accepted by the device filter.
     * @param source the source of the modifier
     * @param deviceFilter the device filter
     */
    public MayUseDeviceModifier(PhysicalCard source, Filterable deviceFilter) {
        this(source, source, null, deviceFilter);
    }

    /**
     * Creates modifier that allows the cards accepted by the filter to use devices accepted by the device filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for affected cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param deviceFilter the device filter
     */
    private MayUseDeviceModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable deviceFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_USE_DEVICE, true);
        _deviceFilter = Filters.and(Filters.device, deviceFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May use specified devices";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_deviceFilter).accepts(gameState, modifiersQuerying, target);
    }
}

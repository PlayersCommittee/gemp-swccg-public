package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for cards (e.g. Elis Helrot and Nabrun Leids) that may not be used at to transport character to or from
 * specified locations.
 */
public class MayNotUseCardToTransportToOrFromLocationModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier for locations Elis Helrot and Nabrun Leids may not be used at to transport character to or from.
     * @param source the card that is the source of the modifier
     * @param affectFilter the filter
     * @param locationFilter the location filter
     */
    public MayNotUseCardToTransportToOrFromLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier for locations Elis Helrot and Nabrun Leids may not be used at to transport character to or from.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    private MayNotUseCardToTransportToOrFromLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, affectFilter, condition, ModifierType.CANT_USE_TO_TRANSPORT_TO_OR_FROM_LOCATION, true);
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May not be used to transport to or from some locations";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, card);
    }
}

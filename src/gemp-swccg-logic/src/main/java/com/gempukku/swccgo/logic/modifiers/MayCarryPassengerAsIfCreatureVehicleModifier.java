package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier that allows affected cards to carry a passenger as if a creature vehicle.
 */
public class MayCarryPassengerAsIfCreatureVehicleModifier extends AbstractModifier {
    private Filter _passengerFilter;

    /**
     * Creates a modifier that allows source card to carry a passenger as if a creature vehicle.
     * @param source the source of the modifier
     * @param passengerFilter the passenger filter
     */
    public MayCarryPassengerAsIfCreatureVehicleModifier(PhysicalCard source, Filterable passengerFilter) {
        super(source, "May carry passenger as if creature vehicle", source, null, ModifierType.MAY_CARRY_PASSENGER_AS_IF_CREATURE_VEHICLE);
        _passengerFilter = Filters.and(passengerFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_passengerFilter).accepts(gameState, modifiersQuerying, target);
    }
}

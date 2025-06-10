package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that limits which starships/vehicles affected cards may pilot.
 */
public class MayOnlyPilotModifier extends AbstractModifier {
    private Filter _starshipVehicleFilter;

    /**
     * Creates a modifier that limits which starships/vehicles source card may pilot.
     * @param source the card that is the source of the modifier and that is affected by the modifier
     * @param starshipVehicleFilter filter for starships/vehicles
     */
    public MayOnlyPilotModifier(PhysicalCard source, Filterable starshipVehicleFilter) {
        this(source, source, null, starshipVehicleFilter);
    }

    /**
     * Creates a modifier that limits which starships/vehicles source card may pilot.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are only allowed to pilot certain starships/vehicles
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param starshipVehicleFilter filter for starships/vehicles
     */
    private MayOnlyPilotModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable starshipVehicleFilter) {
        super(source, "May only pilot certain cards", affectFilter, condition, ModifierType.MAY_NOT_PILOT_TARGET);
        _starshipVehicleFilter = Filters.not(Filters.and(starshipVehicleFilter));
    }

    @Override
    public boolean prohibitedFromPiloting(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard starshipOrVehicle) {
        return Filters.and(_starshipVehicleFilter).accepts(gameState, modifiersQuerying, starshipOrVehicle);
    }
}

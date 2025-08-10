package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes affected cards to deploy free aboard specified starships or vehicles.
 */
public class DeploysFreeAboardModifier extends AbstractModifier {
    private Filter _targetFilter;

    /**
     * Creates a modifier that causes affected cards to deploy free aboard the specified starship or vehicle.
     * @param source the card that is the source of the modifier and deploys free
     * @param starshipOrVehicle the starship/vehicle
     */
    public DeploysFreeAboardModifier(PhysicalCard source, PhysicalCard starshipOrVehicle) {
        this(source, source, null, starshipOrVehicle);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free aboard specified starships or vehicles accepted by
     * the starship/vehicle filter.
     * @param source the card that is the source of the modifier and deploys free
     * @param starshipOrVehicleFilter the starship/vehicle filter
     */
    public DeploysFreeAboardModifier(PhysicalCard source, Filter starshipOrVehicleFilter) {
        this(source, source, null, starshipOrVehicleFilter);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free aboard a starship or vehicle with the specified persona.
     * @param source the card that is the source of the modifier and deploys free
     * @param starshipOrVehiclePersona the starship/vehicle persona
     */
    public DeploysFreeAboardModifier(PhysicalCard source, Persona starshipOrVehiclePersona) {
        this(source, source, null, starshipOrVehiclePersona);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free aboard the specified starship or vehicle.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free
     * @param starshipOrVehicle the starship/vehicle
     */
    public DeploysFreeAboardModifier(PhysicalCard source, Filterable affectFilter, PhysicalCard starshipOrVehicle) {
        this(source, affectFilter, null, starshipOrVehicle);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free aboard specified starships or vehicles accepted by
     * the starship/vehicle filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free
     * @param starshipOrVehicleFilter the starship/vehicle filter
     */
    public DeploysFreeAboardModifier(PhysicalCard source, Filterable affectFilter, Filter starshipOrVehicleFilter) {
        this(source, affectFilter, null, starshipOrVehicleFilter);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free aboard a starship or vehicle with the specified persona.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free
     * @param starshipOrVehiclePersona the starship/vehicle persona
     */
    public DeploysFreeAboardModifier(PhysicalCard source, Filterable affectFilter, Persona starshipOrVehiclePersona) {
        this(source, affectFilter, null, starshipOrVehiclePersona);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free aboard the specified starship or vehicle.
     * @param source the card that is the source of the modifier and deploys free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param starshipOrVehicle the starship/vehicle
     */
    public DeploysFreeAboardModifier(PhysicalCard source, Condition condition, PhysicalCard starshipOrVehicle) {
        this(source, source, condition, starshipOrVehicle);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free aboard specified starships or vehicles accepted by
     * the starship/vehicle filter.
     * @param source the card that is the source of the modifier and deploys free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param starshipOrVehicleFilter the starship/vehicle filter
     */
    public DeploysFreeAboardModifier(PhysicalCard source, Condition condition, Filter starshipOrVehicleFilter) {
        this(source, source, condition, starshipOrVehicleFilter);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free aboard the starship or vehicle with the specified persona.
     * @param source the card that is the source of the modifier and deploys free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param starshipOrVehiclePersona the starship/vehicle persona
     */
    public DeploysFreeAboardModifier(PhysicalCard source, Condition condition, Persona starshipOrVehiclePersona) {
        this(source, source, condition, starshipOrVehiclePersona);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free aboard the specified starship or vehicle.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param starshipOrVehicle the starship/vehicle
     */
    public DeploysFreeAboardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, PhysicalCard starshipOrVehicle) {
        super(source, null, Filters.and(Filters.not(Filters.in_play), affectFilter), condition, ModifierType.DEPLOYS_FREE_TO_TARGET, true);
        _targetFilter = Filters.or(starshipOrVehicle, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehicle)));
    }

    /**
     * Creates a modifier that causes affected cards to deploy free aboard specified starships or vehicles accepted by
     * the starship/vehicle filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param starshipOrVehicleFilter the starship/vehicle filter
     */
    public DeploysFreeAboardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filter starshipOrVehicleFilter) {
        super(source, null, Filters.and(Filters.not(Filters.in_play), affectFilter), condition, ModifierType.DEPLOYS_FREE_TO_TARGET, true);
        _targetFilter = Filters.or(starshipOrVehicleFilter, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehicleFilter)));
    }

    /**
     * Creates a modifier that causes affected cards to deploy free aboard the starship or vehicle with the specified persona.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param starshipOrVehiclePersona the starship/vehicle persona
     */
    public DeploysFreeAboardModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Persona starshipOrVehiclePersona) {
        super(source, null, affectFilter, condition, ModifierType.DEPLOYS_FREE_TO_TARGET, true);
        _targetFilter = Filters.or(starshipOrVehiclePersona, Filters.locationAndCardsAtLocation(Filters.siteOfStarshipOrVehicle(starshipOrVehiclePersona, false)));
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Deploys for free aboard specific starships/vehicles";
    }

    @Override
    public boolean isDeployFreeToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }
}

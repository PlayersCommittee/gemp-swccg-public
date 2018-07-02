package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows affected cards to deploy to specified Dagobah locations.
 */
public class MayDeployToDagobahLocationModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that allows source card to deploy to Dagobah.
     * @param source the card that is the source of the modifier and whose allowed to deploy to Dagobah
     */
    public MayDeployToDagobahLocationModifier(PhysicalCard source) {
        this(source, source, Filters.any);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy to Dagobah.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayDeployToDagobahLocationModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, Filters.any);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy to Dagobah locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param locationFilter the location filter
     */
    public MayDeployToDagobahLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy to Dagobah locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MayDeployToDagobahLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_DEPLOY_TO_DAGOBAH_TARGET, true);
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May deploy to Dagobah";
    }

    @Override
    public boolean grantedToDeployToDagobahTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.locationAndCardsAtLocation(Filters.and(_locationFilter, Filters.Dagobah_location)).accepts(gameState, modifiersQuerying, target);
    }
}

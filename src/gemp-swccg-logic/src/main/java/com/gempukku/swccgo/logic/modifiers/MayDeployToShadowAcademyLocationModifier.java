package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that allows affected cards to deploy to specified Shadow Academy locations.
 */
public class MayDeployToShadowAcademyLocationModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that allows source card to deploy to Shadow Academy.
     * @param source the card that is the source of the modifier and whose allowed to deploy to Shadow Academy
     */
    public MayDeployToShadowAcademyLocationModifier(PhysicalCard source) {
        this(source, source, Filters.any);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy to Shadow Academy.
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public MayDeployToShadowAcademyLocationModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, Filters.any);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy to Shadow Academy locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param locationFilter the location filter
     */
    public MayDeployToShadowAcademyLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy to Shadow Academy locations accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MayDeployToShadowAcademyLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_DEPLOY_TO_SHADOW_ACADEMY_TARGET, true);
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "May deploy to Shadow Academy";
    }

    @Override
    public boolean grantedToDeployToShadowAcademyTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.locationAndCardsAtLocation(Filters.and(_locationFilter, Filters.Shadow_Academy_location)).accepts(gameState, modifiersQuerying, target);
    }
}

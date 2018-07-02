package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier that allows affected cards to deploy with specified starships instead of a matching starship using Combat Response.
 */
public class MayDeployWithInsteadOfMatchingStarfighterUsingCombatResponseModifier extends AbstractModifier {
    private Filter _starshipFilter;

    /**
     * Creates a modifier that allows source card to deploy with starships accepted by the starship filter instead of a
     * matching starship using Combat Response.
     * @param source the source of the modifier
     * @param starshipFilter the starship filter
     */
    public MayDeployWithInsteadOfMatchingStarfighterUsingCombatResponseModifier(PhysicalCard source, Filterable starshipFilter) {
        this(source, source, starshipFilter);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy with starships accepted by the starship filter
     * instead of a matching starship using Combat Response.
     * @param source the source of the modifier
     * @param pilotFilter the pilot filter
     * @param starshipFilter the starship filter
     */
    private MayDeployWithInsteadOfMatchingStarfighterUsingCombatResponseModifier(PhysicalCard source, Filterable pilotFilter, Filterable starshipFilter) {
        super(source, null, pilotFilter, null, ModifierType.MAY_DEPLOY_WITH_INSTEAD_OF_MATCHING_STARFIGHTER_USING_COMBAT_RESPONSE, true);
        _starshipFilter = Filters.and(starshipFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_starshipFilter).accepts(gameState, modifiersQuerying, targetCard);
    }
}

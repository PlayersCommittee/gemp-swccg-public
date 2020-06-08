package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to be immune to deploy cost modifiers from specified card when deploying to specified
 * locations.
 */
public class ImmuneToDeployCostModifiersToLocationModifier extends AbstractModifier {
    private Filter _immuneToFilters;
    private Filter _targetFilters;

    /**
     * Creates a modifier that causes affected cards accepted by the affectFilter to be immune to deploy cost modifiers from cards
     * accepted by immuneToFilter when deploying to locations accepted by locationFilter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param immuneToFilter the filter for cards that affected cards are immune to their deploy cost modifiers
     * @param locationFilters the location filter
     */
    public ImmuneToDeployCostModifiersToLocationModifier(PhysicalCard source, Filterable affectFilter, Filterable immuneToFilter, Filterable locationFilters) {
        this(source, affectFilter, null, immuneToFilter, locationFilters);
    }

    /**
     * Creates a modifier that causes affected cards accepted by the affectFilter to be immune to deploy cost modifiers from cards
     * accepted by immuneToFilter when deploying to locations accepted by locationFilter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param immuneToFilter the filter for cards that affected cards are immune to their deploy cost modifiers
     * @param locationFilters the location filter
     */
    public ImmuneToDeployCostModifiersToLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable immuneToFilter, Filterable locationFilters) {
        super(source, null, Filters.and(Filters.not(Filters.in_play), affectFilter), condition, ModifierType.IMMUNE_TO_DEPLOY_COST_MODIFIERS_TO_TARGET, true);
        _immuneToFilters = Filters.and(immuneToFilter);
        _targetFilters = Filters.locationAndCardsAtLocation(Filters.and(locationFilters));
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Immune to certain deploy cost modifiers";
    }

    @Override
    public boolean isImmuneToDeployCostToTargetModifierFromCard(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard deployToTarget, PhysicalCard sourceOfModifier) {
        return Filters.and(_immuneToFilters).accepts(gameState, modifiersQuerying, sourceOfModifier) && Filters.and(_targetFilters).accepts(gameState, modifiersQuerying, deployToTarget);
    }
}

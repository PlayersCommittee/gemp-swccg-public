package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents a player's Force generation at specified locations from being canceled by specified cards.
 */
public class ForceGenerationImmuneToCancelModifier extends AbstractModifier {
    private Filter _cancelSourceFilter;

    /**
     * Creates a modifier that prevents Force generation at locations accepted by the location filter from being canceled
     * by cards accepted by the cancel source filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param cancelSourceFilter the cancel source filter
     */
    public ForceGenerationImmuneToCancelModifier(PhysicalCard source, Filterable locationFilter, Filterable cancelSourceFilter) {
        this(source, locationFilter, null, cancelSourceFilter, null);
    }

    /**
     * Creates a modifier that prevents a player's Force generation at locations accepted by the location filter from
     * being canceled by cards accepted by the cancel source filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param cancelSourceFilter the cancel source filter
     * @param playerId the player with the Force generation
     */
    public ForceGenerationImmuneToCancelModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Filterable cancelSourceFilter, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.FORCE_GENERATION_AT_LOCATION_IMMUNE_TO_CANCEL, true);
        _cancelSourceFilter = Filters.and(cancelSourceFilter);
        _playerId = playerId;
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cancelSourceCard) {
        return Filters.and(_cancelSourceFilter).accepts(gameState, modifiersQuerying, cancelSourceCard);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return (_playerId != null ? (_playerId + "'s ") : "") + "Force generation immune to being canceled";
    }
}

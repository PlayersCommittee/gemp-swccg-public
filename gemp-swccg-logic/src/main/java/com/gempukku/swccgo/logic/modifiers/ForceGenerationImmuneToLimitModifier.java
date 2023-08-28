package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that prevents a player's Force generation at specified locations from being limited by specified cards.
 */
public class ForceGenerationImmuneToLimitModifier extends AbstractModifier {
    private Filter _limitSourceFilter;

    /**
     * Creates a modifier that prevents Force generation at locations accepted by the location filter from being limited
     * by cards accepted by the limit source filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param limitSourceFilter the limit source filter
     */
    public ForceGenerationImmuneToLimitModifier(PhysicalCard source, Filterable locationFilter, Filterable limitSourceFilter) {
        this(source, locationFilter, null, limitSourceFilter, null);
    }

    /**
     * Creates a modifier that prevents a player's Force generation at locations accepted by the location filter from
     * being limited by cards accepted by the limit source filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param limitSourceFilter the limit source filter
     * @param playerId the player with the Force generation
     */
    public ForceGenerationImmuneToLimitModifier(PhysicalCard source, Filterable locationFilter, Filterable limitSourceFilter, String playerId) {
        this(source, locationFilter, null, limitSourceFilter, playerId);
    }

    /**
     * Creates a modifier that prevents a player's Force generation at locations accepted by the location filter from
     * being limited by cards accepted by the limit source filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param limitSourceFilter the limit source filter
     * @param playerId the player with the Force generation
     */
    public ForceGenerationImmuneToLimitModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Filterable limitSourceFilter, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.FORCE_GENERATION_AT_LOCATION_IMMUNE_TO, true);
        _limitSourceFilter = Filters.and(limitSourceFilter);
        _playerId = playerId;
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard limitSourceCard) {
        return Filters.and(_limitSourceFilter).accepts(gameState, modifiersQuerying, limitSourceCard);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return (_playerId != null ? (_playerId + "'s ") : "") + "Force generation immune to certain limits";
    }
}

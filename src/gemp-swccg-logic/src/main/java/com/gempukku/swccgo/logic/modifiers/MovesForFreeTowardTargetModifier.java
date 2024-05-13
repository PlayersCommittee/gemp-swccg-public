package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.CardCategory;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to move free to when moving toward a specified target.
 */
public class MovesForFreeTowardTargetModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that causes specified cards to move free to when moving toward a location accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by the modifier
     * @param locationFilter the location filter
     */
    public MovesForFreeTowardTargetModifier(PhysicalCard source, Filterable affectFilter, Filterable locationFilter) {
        this(source, affectFilter, null, locationFilter);
    }

    /**
     * Creates a modifier that causes specified cards to move free to when moving toward a location accepted by the target filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param locationFilter the location filter
     */
    public MovesForFreeTowardTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable locationFilter) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MOVES_FREE_TOWARD_TARGET, true);
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Moves for free to specific locations";
    }

    @Override
    public boolean isMovingTowardTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, PhysicalCard destination) {
        if (destination.getBlueprint().getCardCategory() != CardCategory.LOCATION)
            return false;

        PhysicalCard currentSite = modifiersQuerying.getLocationThatCardIsAt(gameState, physicalCard);
        if (currentSite==null)
            return false;

        PhysicalCard targetSite = Filters.findFirstFromTopLocationsOnTable(gameState.getGame(), _locationFilter);
        if (targetSite==null)
            return false;

        return Filters.toward(currentSite, targetSite).accepts(gameState, modifiersQuerying, destination);
    }
}

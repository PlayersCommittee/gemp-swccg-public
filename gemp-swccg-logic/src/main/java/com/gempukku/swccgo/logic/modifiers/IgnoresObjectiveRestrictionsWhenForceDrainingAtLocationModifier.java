package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes specified cards to ignore objective restrictions when force draining at specified locations.
 */
public class IgnoresObjectiveRestrictionsWhenForceDrainingAtLocationModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that causes cards to ignore objective restrictions when force draining at locations accepted by the filter
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param playerId the player
     */
    public IgnoresObjectiveRestrictionsWhenForceDrainingAtLocationModifier(PhysicalCard source, Filterable affectFilter, String playerId) {
        this(source, affectFilter, null, playerId);
    }

    /**
     * Creates a modifier that causes cards to ignore objective restrictions when force draining at locations accepted by the filter
     * accepted by the location filter.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param playerId the player
     */
    public IgnoresObjectiveRestrictionsWhenForceDrainingAtLocationModifier(PhysicalCard source, Filterable affectFilter, Condition condition, String playerId) {
        super(source, null, affectFilter, condition, ModifierType.IGNORES_OBJECTIVE_RESTRICTIONS_WHEN_FORCE_DRAINING_AT_LOCATION);
        _playerId = playerId;
        _locationFilter = Filters.and(affectFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        if (gameState.getDarkPlayer().equals(_playerId))
            return "Dark side ignores objective restrictions when Force draining";
        else
            return "Light side ignores objective restrictions when Force draining";
    }
}
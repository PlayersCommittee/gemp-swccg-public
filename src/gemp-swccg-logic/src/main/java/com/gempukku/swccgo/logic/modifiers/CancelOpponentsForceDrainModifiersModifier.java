package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that cancels all of the opponent's Force drain modifiers.
 */
public class CancelOpponentsForceDrainModifiersModifier extends AbstractModifier {
    private Filter _locationFilter;

    /**
     * Creates a modifier that cancels all of the opponent's Force drain modifiers.
     * @param source the source of the modifier
     * @param condition the condition
     */
    public CancelOpponentsForceDrainModifiersModifier(PhysicalCard source, Condition condition) {
        this(source, condition, Filters.any);
    }

    /**
     * Creates a modifier that cancels all of the opponent's Force drain modifiers.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public CancelOpponentsForceDrainModifiersModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, null, locationFilter);
    }

    /**
     * Creates a modifier that cancels all of the opponent's Force drain modifiers at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param condition the condition
     * @param locationFilter the location filter
     */
    public CancelOpponentsForceDrainModifiersModifier(PhysicalCard source, Condition condition, Filterable locationFilter) {
        super(source, null, null, condition, ModifierType.CANCEL_OPPONENTS_FORCE_DRAIN_MODIFIERS, true);
        _playerId = source.getOwner();
        _locationFilter = Filters.and(locationFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_locationFilter).accepts(gameState, modifiersQuerying, targetCard);
    }
}

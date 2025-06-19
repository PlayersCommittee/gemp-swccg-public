package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes affected cards to be treated as if they are the matching pilot for the ship they are piloting.
 */
public class ConsideredMatchingPilotModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes affected cards to be treated as if they are the matching pilot for the ship
     * or vehicle they are currently piloting.  See Rebel Flight Suit (non-V).
     * @param source the card source of the modifier
     * @param affectFilter describes characters which should always match the ship they are piloting
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public ConsideredMatchingPilotModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.CONSIDERED_MATCHING_PILOT, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Is considered the matching pilot for whatever they pilot";
    }
}

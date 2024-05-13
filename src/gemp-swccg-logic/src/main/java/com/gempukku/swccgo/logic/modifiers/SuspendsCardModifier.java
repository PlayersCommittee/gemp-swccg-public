package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes the affected cards to be suspended.
 * Note: The affected cards are actually suspended by an action triggered by the SuspendCardRule class.
 */
public class SuspendsCardModifier extends AbstractModifier {

    /**
     * Creates a modifier that causes the affected cards to be suspended.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that should be suspended
     */
    public SuspendsCardModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    /**
     * Creates a modifier that causes the affected cards to be suspended.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that should be suspended
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public SuspendsCardModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, null, Filters.and(Filters.onTable, affectFilter), condition, ModifierType.SUSPEND_CARD, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return self.isSuspended() ? "Suspended" : null;
    }
}

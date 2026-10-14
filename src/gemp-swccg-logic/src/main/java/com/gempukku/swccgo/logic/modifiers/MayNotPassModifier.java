package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Blocks the affected cards from passing the source between-sites card.
 */
public class MayNotPassModifier extends AbstractModifier {

    public MayNotPassModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null);
    }

    public MayNotPassModifier(PhysicalCard source, Filterable affectFilter, Condition condition) {
        super(source, "May not pass", Filters.and(Filters.in_play, affectFilter), condition, ModifierType.MAY_NOT_PASS, true);
    }

    @Override
    public boolean prohibitedFromPassing(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return true;
    }
}

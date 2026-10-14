package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Extra Force to pass the source between-sites card.
 */
public class PassCostModifier extends AbstractModifier {
    private int _amount;

    public PassCostModifier(PhysicalCard source, Filterable affectFilter, int amount) {
        this(source, affectFilter, null, amount);
    }

    public PassCostModifier(PhysicalCard source, Filterable affectFilter, Condition condition, int amount) {
        // Marked cumulative so every copy stays visible to getModifiersAffectingCard.
        // MovementCosts then applies the same title once along a given path.
        super(source, "Pass cost", Filters.and(Filters.in_play, affectFilter), condition, ModifierType.PASS_COST, true);
        _amount = amount;
    }

    @Override
    public float getPassCost(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _amount;
    }
}

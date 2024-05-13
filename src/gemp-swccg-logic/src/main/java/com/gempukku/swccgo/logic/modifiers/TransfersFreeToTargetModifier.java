package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to transfer free to specified targets.
 */
public class TransfersFreeToTargetModifier extends AbstractModifier {
    private Filter _targetFilter;

    /**
     * Creates a modifier that causes affected cards to transfer free to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that transfer free to specified targets
     * @param targetFilter the target filter
     */
    public TransfersFreeToTargetModifier(PhysicalCard source, Filterable affectFilter, Filterable targetFilter) {
        this(source, affectFilter, null, targetFilter);
    }

    /**
     * Creates a modifier that causes affected cards to transfer free to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that transfer free to specified targets
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param targetFilter the target filter
     */
    public TransfersFreeToTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable targetFilter) {
        super(source, null, Filters.and(Filters.in_play, affectFilter), condition, ModifierType.TRANSFERS_FREE_TO_TARGET, true);
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Transfers for free to specific targets";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }
}

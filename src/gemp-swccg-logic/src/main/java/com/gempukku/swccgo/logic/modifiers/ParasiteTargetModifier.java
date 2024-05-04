package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that allows affected cards to attack specified cards as a parasite.
 */
public class ParasiteTargetModifier extends AbstractModifier {
    private Filter _targetFilter;

    /**
     * Creates a modifier that allows source card to attack cards accepted by the target filter as parasite.
     * @param source the source of the modifier
     * @param targetFilter the target filter
     */
    public ParasiteTargetModifier(PhysicalCard source, Filterable targetFilter) {
        this(source, source, null, targetFilter);
    }

    /**
     * Creates a modifier that allows cards accepted by the filter to deploy to attack cards accepted by the target filter as parasite.
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param targetFilter the target filter
     */
    private ParasiteTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable targetFilter) {
        super(source, "May attack specific targets as parasite", affectFilter, condition, ModifierType.PARASITE_TARGET);
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return _targetFilter.accepts(gameState, modifiersQuerying, targetCard);
    }
}

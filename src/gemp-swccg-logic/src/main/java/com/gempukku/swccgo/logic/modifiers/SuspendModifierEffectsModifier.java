package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that suspends all modifiers from specified cards from affecting specified target cards.
 */
public class SuspendModifierEffectsModifier extends AbstractModifier {
    private Filter _targetFilter;

    /**
     * Creates a modifier that suspends all modifiers from cards accepted by suspendedEffectsFromFilter from affecting
     * cards accepted by the filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param suspendedEffectsFromFilter the filter for cards whose modifiers are suspended from affecting the affected cards
     */
    public SuspendModifierEffectsModifier(PhysicalCard source, Filterable affectFilter, Filterable suspendedEffectsFromFilter) {
        this(source, affectFilter, null, suspendedEffectsFromFilter);
    }

    /**
     * Creates a modifier that cancels the effects of Revolution.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param suspendedEffectsFromFilter the filter for cards whose modifiers are suspended from affecting the affected cards
     */
    private SuspendModifierEffectsModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable suspendedEffectsFromFilter) {
        super(source, null, affectFilter, condition, ModifierType.SUSPEND_EFFECTS_FROM_CARD, true);
        _targetFilter = Filters.and(suspendedEffectsFromFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Modifiers from certain cards are suspended";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }
}

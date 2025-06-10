package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that causes affected cards to not be allowed to be the "highest-ability character".
 */
public class ExcludedFromBeingTheHighestAbilityCharacterModifier extends AbstractModifier {
    private Filter _cardCheckingFilter;

    /**
     * Creates a modifier that causes cards accepted by the filter to not be allowed to be the "highest-ability character".
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public ExcludedFromBeingTheHighestAbilityCharacterModifier(PhysicalCard source, Filterable affectFilter) {
        this(source, affectFilter, null, Filters.any);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to not be allowed to be the "highest-ability character".
     * @param source the source of the modifier
     * @param affectFilter the filter
     */
    public ExcludedFromBeingTheHighestAbilityCharacterModifier(PhysicalCard source, Filterable affectFilter, Filterable cardCheckingFilter) {
        this(source, affectFilter, null, cardCheckingFilter);
    }

    /**
     * Creates a modifier that causes cards accepted by the filter to not be allowed to be the "highest-ability character".
     * @param source the source of the modifier
     * @param affectFilter the filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    private ExcludedFromBeingTheHighestAbilityCharacterModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable cardCheckingFilter) {
        super(source, "Excluded from being the 'highest ability character'", affectFilter, condition, ModifierType.MAY_NOT_BE_HIGHEST_ABILITY_CHARACTER, true);
        _cardCheckingFilter = Filters.and(cardCheckingFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_cardCheckingFilter).accepts(gameState, modifiersQuerying, targetCard);
    }
}

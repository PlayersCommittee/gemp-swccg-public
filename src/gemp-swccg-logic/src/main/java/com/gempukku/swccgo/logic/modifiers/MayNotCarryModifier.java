package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prohibits affected cards from carrying specified cards.
 */
public class MayNotCarryModifier extends AbstractModifier {
    private Filter _cannotBeCarriedFilter;

    /**
     * Creates a modifier that prohibits source card from carrying specified cards.
     * @param source the card that is the source of the modifier and that is not allowed to carry specified cards
     * @param cannotBeCarriedFilter filter for cards not allowed to be carried
     */
    public MayNotCarryModifier(PhysicalCard source, Filterable cannotBeCarriedFilter) {
        this(source, source, null, cannotBeCarriedFilter);
    }

    /**
     * Creates a modifier that prohibits affected cards from carrying specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to carry specified cards
     * @param cannotBeCarriedFilter filter for cards not allowed to be carried
     */
    public MayNotCarryModifier(PhysicalCard source, Filterable affectFilter, Filterable cannotBeCarriedFilter) {
        this(source, affectFilter, null, cannotBeCarriedFilter);
    }

    /**
     * Creates a modifier that prohibits affected cards from carrying specified cards.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that are not allowed to carry specified cards
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param cannotBeCarriedFilter filter for cards not allowed to be carried
     */
    public MayNotCarryModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable cannotBeCarriedFilter) {
        super(source, "Affected by \"may not carry\" limitation", affectFilter, condition, ModifierType.MAY_NOT_CARRY);
        _cannotBeCarriedFilter = Filters.and(cannotBeCarriedFilter);
    }

    @Override
    public boolean prohibitedFromCarrying(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard character, PhysicalCard cardToBeCarried) {
        return Filters.and(_cannotBeCarriedFilter).accepts(gameState, modifiersQuerying, cardToBeCarried);
    }
}

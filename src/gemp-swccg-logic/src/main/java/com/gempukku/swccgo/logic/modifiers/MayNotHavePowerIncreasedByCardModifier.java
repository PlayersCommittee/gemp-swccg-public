package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier that cancels the power modifiers of cards matching a filter on target card
 */
public class MayNotHavePowerIncreasedByCardModifier extends AbstractModifier {
    private Filter _affectedFilter;
    private Filter _increasedByFilter;

    /**
     *
     * @param source the card that is the source of the modifier
     * @param affectFilter filter for the cards that may not have their power modified
     * @param increasedByFilter filter for cards that may not modify affected cards' power
     */
    public MayNotHavePowerIncreasedByCardModifier(PhysicalCard source, Filterable affectFilter, Filterable increasedByFilter) {
        super(source, null, affectFilter, ModifierType.MAY_NOT_HAVE_POWER_INCREASED_BY_CARD);
        _affectedFilter = Filters.and(affectFilter);
        _increasedByFilter = Filters.and(increasedByFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Power may not be increased by certain cards";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return Filters.and(_affectedFilter).accepts(gameState, modifiersQuerying, card);
    }

    @Override
    public Filter getCardsRestrictedFromIncreasingPowerFilter() {
        return _increasedByFilter;
    }
}

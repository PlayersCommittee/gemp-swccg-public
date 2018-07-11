package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;

/**
 * A modifier that cancels the power modifiers of pilots on target card
 */
public class MayNotHavePowerIncreasedByPilotsModifier extends AbstractModifier {
    private Filter _affectedFilter;
    private Filter _pilotFilter;

    /**
     *
     * @param source the card that is the source of the modifier
     * @param affectFilter filter for the cards that may not have their power modified by pilots
     * @param pilotsFilter filter for pilots that may not modify affected cards' power
     */
    public MayNotHavePowerIncreasedByPilotsModifier(PhysicalCard source, Filterable affectFilter, Filterable pilotsFilter) {
        super(source, null, affectFilter, ModifierType.MAY_NOT_HAVE_POWER_INCREASED_BY_PILOTS);
        _affectedFilter = Filters.and(affectFilter);
        _pilotFilter = Filters.and(pilotsFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Power may not be increased by some pilots";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return Filters.and(_affectedFilter).accepts(gameState, modifiersQuerying, card);
    }

    @Override
    public Filter getPilotsRestrictedFromIncreasingPowerFilter() {
        return _pilotFilter;
    }
}

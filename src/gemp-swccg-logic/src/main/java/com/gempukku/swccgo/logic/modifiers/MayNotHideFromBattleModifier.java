package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that prevents cards from 'hiding' from battle at affected locations.
 */
public class MayNotHideFromBattleModifier extends AbstractModifier {

    /**
     * Creates a modifier that prevents cards from 'hiding' from battle at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     */
    public MayNotHideFromBattleModifier(PhysicalCard source, Filterable locationFilter) {
        this(source, locationFilter, null);
    }

    /**
     * Creates a modifier that prevents cards from 'hiding' from battle at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     */
    public MayNotHideFromBattleModifier(PhysicalCard source, Filterable locationFilter, Condition condition) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.MAY_NOT_HIDE_FROM_BATTLE, true);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Cards may not 'hide' from battle";
    }
}

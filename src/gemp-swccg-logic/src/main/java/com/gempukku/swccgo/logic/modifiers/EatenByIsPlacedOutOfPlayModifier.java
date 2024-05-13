package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

public class EatenByIsPlacedOutOfPlayModifier extends AbstractModifier {
    private Filter _eatenFilter;

    public EatenByIsPlacedOutOfPlayModifier(PhysicalCard source) {
        this(source, source, null, Filters.any);
    }

    public EatenByIsPlacedOutOfPlayModifier(PhysicalCard source, Filterable eatenByFilter, Filterable eatenFilter) {
        this(source, eatenByFilter, null, eatenFilter);
    }

    private EatenByIsPlacedOutOfPlayModifier(PhysicalCard source, Filterable eatenByFilter, Condition condition, Filterable eatenFilter) {
        super(source, "Specific cards eaten are placed out of play", eatenByFilter, condition, ModifierType.EATEN_BY_IS_PLACED_OUT_OF_PLAY);
        _eatenFilter = Filters.and(eatenFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return _eatenFilter.accepts(gameState, modifiersQuerying, targetCard);
    }
}

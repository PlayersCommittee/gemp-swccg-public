package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

public class MayNotAttackTargetModifier extends AbstractModifier {
    private Filter _targetFilter;

    public MayNotAttackTargetModifier(PhysicalCard source, Filterable targetFilter) {
        this(source, null, targetFilter);
    }

    private MayNotAttackTargetModifier(PhysicalCard source, Condition condition, Filterable targetFilter) {
        super(source, "May not attack specific targets", source, condition, ModifierType.MAY_NOT_ATTACK_TARGET);
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return _targetFilter.accepts(gameState, modifiersQuerying, targetCard);
    }
}

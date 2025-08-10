package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier which allows specified cards to deploy to specified targets.
 */
public class MayDeployToTargetModifier extends AbstractModifier {
    private Filter _targetFilter;

    /**
     * Creates a modifier which allows source card to deploy to targets accepted by the target filter.
     * @param source the source of the modifier
     * @param targetFilter the target filter
     */
    public MayDeployToTargetModifier(PhysicalCard source, Filterable targetFilter) {
        this(source, source, null, targetFilter);
    }

    /**
     * Creates a modifier which allows cards accepted by the filter to deploy to targets accepted by the target filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param targetFilter the target filter
     */
    public MayDeployToTargetModifier(PhysicalCard source, Filterable affectFilter, Filterable targetFilter) {
        this(source, affectFilter, null, targetFilter);
    }

    /**
     * Creates a modifier which allows cards accepted by the filter to deploy to targets accepted by the target filter.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards affected by this modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param targetFilter the target filter
     */
    protected MayDeployToTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable targetFilter) {
        super(source, null, affectFilter, condition, ModifierType.MAY_DEPLOY_TO_TARGET, true);
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Valid deploy to targets affected";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }
}

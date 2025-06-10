package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier for not being able to deploy to specified targets.
 */
public class MayNotDeployToTargetModifier extends AbstractModifier {
    private Filter _targetFilter;

    /**
     * Creates a modifier for not being able to deploy to specified targets.
     * @param source the card that is the source of the modifier and that may not deploy to specified targets
     * @param targetFilter the filter for targets that affected cards may not deploy to
     */
    public MayNotDeployToTargetModifier(PhysicalCard source, Filterable targetFilter) {
        this(source, source, null, targetFilter);
    }

    /**
     * Creates a modifier for not being able to deploy to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not deploy to specified targets
     * @param targetFilter the filter for targets that affected cards may not deploy to
     */
    public MayNotDeployToTargetModifier(PhysicalCard source, Filterable affectFilter, Filterable targetFilter) {
        this(source, affectFilter, null, targetFilter);
    }

    /**
     * Creates a modifier for not being able to deploy to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that may not deploy to specified targets
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param targetFilter the filter for targets that affected cards may not deploy to
     */
    public MayNotDeployToTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable targetFilter) {
        super(source, null, Filters.and(Filters.not(Filters.in_play), affectFilter), condition, ModifierType.MAY_NOT_DEPLOY_TO_TARGET, true);
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

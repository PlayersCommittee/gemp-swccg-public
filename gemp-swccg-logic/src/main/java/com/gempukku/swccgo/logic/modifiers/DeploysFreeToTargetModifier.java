package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;

/**
 * A modifier that causes affected cards to deploy free to specified targets.
 */
public class DeploysFreeToTargetModifier extends AbstractModifier {
    private Filter _targetFilter;

    /**
     * Creates a modifier that causes the source card to deploy free to specified targets.
     * @param source the card that is the source of the modifier and deploys free to specified targets
     * @param targetFilter the target filter
     */
    public DeploysFreeToTargetModifier(PhysicalCard source, Filterable targetFilter) {
        this(source, source, null, targetFilter);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free to specified targets
     * @param targetFilter the target filter
     */
    public DeploysFreeToTargetModifier(PhysicalCard source, Filterable affectFilter, Filterable targetFilter) {
        this(source, affectFilter, null, targetFilter);
    }

    /**
     * Creates a modifier that causes the source card to deploy free to specified targets.
     * @param source the card that is the source of the modifier and deploys free to specified targeted
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param targetFilter the target filter
     */
    public DeploysFreeToTargetModifier(PhysicalCard source, Condition condition, Filterable targetFilter) {
        this(source, source, condition, targetFilter);
    }

    /**
     * Creates a modifier that causes affected cards to deploy free to specified targets.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards that deploy free to specified targets
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param targetFilter the target filter
     */
    public DeploysFreeToTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Filterable targetFilter) {
        super(source, null, Filters.or(affectFilter, Filters.hasPermanentAboard(Filters.and(affectFilter))), condition, ModifierType.DEPLOYS_FREE_TO_TARGET, true);
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Deploys for free to specific targets";
    }

    @Override
    public boolean isDeployFreeToTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard target) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, target);
    }
}

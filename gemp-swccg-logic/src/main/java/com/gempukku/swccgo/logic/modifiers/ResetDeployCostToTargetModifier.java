package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that resets deploy cost to an unmodifiable value when deploying to specified targets.
 */
public class ResetDeployCostToTargetModifier extends AbstractModifier {
    private float _resetValue;
    private Filter _targetFilter;

    /**
     * Creates a modifier that resets deploy cost to an unmodifiable value deploying to specified targets.
     * @param source the card that is the source of the reset and whose deploy cost is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     * @param targetFilter the filter for deploy to targets
     */
    public ResetDeployCostToTargetModifier(PhysicalCard source, Condition condition, float resetValue, Filterable targetFilter) {
        this(source, source, condition, resetValue, targetFilter);
    }

    /**
     * Creates a modifier that resets deploy cost to an unmodifiable value deploying to specified targets.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose deploy cost is reset
     * @param resetValue the reset value
     * @param targetFilter the filter for deploy to targets
     */
    public ResetDeployCostToTargetModifier(PhysicalCard source, Filterable affectFilter, float resetValue, Filterable targetFilter) {
        this(source, affectFilter, null, resetValue, targetFilter);
    }

    /**
     * Creates a modifier that resets deploy cost to an unmodifiable value deploying to specified targets.
     * @param source the source of the reset
     * @param affectFilter the filter for cards whose deploy cost is reset
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param resetValue the reset value
     * @param targetFilter the filter for deploy to targets
     */
    public ResetDeployCostToTargetModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float resetValue, Filterable targetFilter) {
        super(source, null, affectFilter, condition, ModifierType.UNMODIFIABLE_DEPLOY_COST_TO_TARGET, true);
        _resetValue = resetValue;
        _targetFilter = Filters.and(targetFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return "Deploy cost = " + GuiUtils.formatAsString(_resetValue) + " to specific targets";
    }

    @Override
    public boolean isAffectedTarget(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard targetCard) {
        return Filters.and(_targetFilter).accepts(gameState, modifiersQuerying, targetCard);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _resetValue;
    }
}

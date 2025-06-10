package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier that cancels Force icons for a player at specified locations.
 */
public class CancelForceIconModifier extends AbstractModifier {
    private Icon _icon;
    private Evaluator _evaluator;

    /**
     * Creates a modifier that cancel a Force icon for the specified player at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param modifierAmount the amount of the modifier
     * @param icon the Force icon
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public CancelForceIconModifier(PhysicalCard source, Filterable locationFilter, int modifierAmount, Icon icon, boolean cumulative) {
        this(source, locationFilter, null, new ConstantEvaluator(modifierAmount), icon, cumulative);
    }

    /**
     * Creates a modifier that cancel a specified number Force icons for the specified player at locations accepted by the location filter.
     * @param source the source of the modifier
     * @param locationFilter the location filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param icon the Force icon
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    private CancelForceIconModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, Icon icon, boolean cumulative) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.CANCEL_FORCE_ICON, cumulative);
        _icon = icon;
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        return (value >= Integer.MAX_VALUE ? "All" : GuiUtils.formatAsString(value)) + " " + (_icon == Icon.DARK_FORCE ? ("Dark Side Force icon" + GameUtils.s(value) + " canceled") : ("Light Side Force icon" + GameUtils.s(value) + " canceled"));
    }

    @Override
    public Icon getIcon() {
        return _icon;
    }

    @Override
    public int getIconCountModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard, Icon icon) {
        if (icon == _icon)
            return (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
        else
            return 0;
    }
}

package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.List;

/**
 * A modifier that restricts how much a pilot can alter the base maneuver of the ship/vehicle they are
 * piloting.  See Rebel Flight Suit (non-V).
 */
public class ManeuverLimitFromPilotModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private Filterable _pilotFilter;

    /**
     * Creates a modifier that caps the overall increase that can be made to a ship's maneuver from its pilots.
     * @param source the source of the modifier
     * @param pilotFilter describes the pilot who is restricted
     * @param modifierAmount the cap to the pilot's maneuver bonus
     */
    public ManeuverLimitFromPilotModifier(PhysicalCard source, Filterable pilotFilter, Condition condition, float modifierAmount) {
        this(source, pilotFilter, condition, new ConstantEvaluator(modifierAmount), true);
    }

    /**
     * Creates a modifier that caps the overall increase that can be made to a ship's maneuver from its pilots.
     * @param source       the card source of the modifier
     * @param pilotFilter  describes the pilots which have their maneuver bonus restricted
     * @param condition    the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator    the cap to the pilot's maneuver bonus
     * @param cumulative   true if the modifier is cumulative, otherwise false
     */
    public ManeuverLimitFromPilotModifier(PhysicalCard source, Filterable pilotFilter, Condition condition, Evaluator evaluator, boolean cumulative) {
        super(source, null, Filters.hasPiloting(source, Filters.and(pilotFilter)), condition, ModifierType.MANEUVER_INCREASE_FROM_PILOT_LIMIT, cumulative);
        _evaluator = evaluator;
        _pilotFilter = Filters.and(pilotFilter);
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = getManeuverModifierLimit(gameState, modifiersQuerying, self);
        if (value > 0)
            return "Maneuver may not be increased by more than " + GuiUtils.formatAsString(value) + " by pilot";
        else
            return null;
    }

    @Override
    public float getManeuverModifierLimit(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }

    public Filterable getLimitedPilotFilter() {
        return _pilotFilter;
    }
}

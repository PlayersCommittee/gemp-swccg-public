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

/**
 * A modifier that affects each tractor beam destiny for tractor beams.
 */
public class EachTractorBeamDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that affects each tractor beam destiny for tractor beams accepted by the tractor beam filter.
     * @param source the source of the modifier
     * @param tractorBeamFilter the tractor beam filter
     * @param modifierAmount the amount of the modifier
     */
    public EachTractorBeamDestinyModifier(PhysicalCard source, Filterable tractorBeamFilter, float modifierAmount) {
        this(source, tractorBeamFilter, null, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that affects each tractor beam destiny for tractor beams accepted by the tractor beam filter.
     * @param source the source of the modifier
     * @param tractorBeamFilter the tractor beam filter
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    protected EachTractorBeamDestinyModifier(PhysicalCard source, Filterable tractorBeamFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.and(Filters.tractor_beam, tractorBeamFilter), condition, ModifierType.EACH_TRACTOR_BEAM_DESTINY, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (value >= 0)
            return "Each tractor beam destiny +" + GuiUtils.formatAsString(value);
        else
            return "Each tractor beam destiny " + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, physicalCard);
    }
}

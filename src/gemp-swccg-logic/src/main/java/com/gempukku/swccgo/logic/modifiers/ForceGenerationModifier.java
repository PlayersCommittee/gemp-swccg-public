package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier for Force generation at a location.
 */
public class ForceGenerationModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier for Force generation at a location.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force generation is modified
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public ForceGenerationModifier(PhysicalCard source, Filterable locationFilter, int modifierAmount, String playerId) {
        this(source, locationFilter, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier for Force generation at a location.
     * @param source the location that is the source of the modifier and where Force generation is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public ForceGenerationModifier(PhysicalCard source, Evaluator evaluator, String playerId) {
        this(source, source, null, evaluator, playerId);
    }

    /**
     * Creates a modifier for Force generation at a location.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force generation is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public ForceGenerationModifier(PhysicalCard source, Filterable locationFilter, Evaluator evaluator, String playerId) {
        this(source, locationFilter, null, evaluator, playerId);
    }

    /**
     * Creates a modifier for Force generation at a location.
     * @param source the location that is the source of the modifier and where Force generation is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public ForceGenerationModifier(PhysicalCard source, Condition condition, int modifierAmount, String playerId) {
        this(source, source, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier for Force generation at a location.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force generation is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public ForceGenerationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, int modifierAmount, String playerId) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier for Force generation at a location.
     * @param source the location that is the source of the modifier and where Force generation is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public ForceGenerationModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        this(source, source, condition, evaluator, playerId);
    }

    /**
     * Creates a modifier for Force generation at a location.
     * @param source the source of the modifier
     * @param locationFilter the filter for locations where Force generation is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose Force generation is modified
     */
    public ForceGenerationModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.and(Filters.location, locationFilter), condition, ModifierType.FORCE_GENERATION_AT_LOCATION, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        String sideText;
        if (gameState.getDarkPlayer().equals(_playerId))
            sideText = "Dark side";
        else
            sideText = "Light side";

        if (value < 0)
            return sideText + " Force generation " + GuiUtils.formatAsString(value);
        else
            return sideText + " Force generation +" + GuiUtils.formatAsString(value);
    }

    @Override
    public float getValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}

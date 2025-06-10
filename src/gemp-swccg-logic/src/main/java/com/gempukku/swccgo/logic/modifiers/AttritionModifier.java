package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * A modifier to attrition against a specified player.
 */
public class AttritionModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whom attrition against is modified
     */
    public AttritionModifier(PhysicalCard source, float modifierAmount, String playerId) {
        this(source, Filters.any, null, new ConstantEvaluator(modifierAmount), playerId, false);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whom attrition against is modified
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public AttritionModifier(PhysicalCard source, float modifierAmount, String playerId, boolean cumulative) {
        this(source, Filters.any, null, new ConstantEvaluator(modifierAmount), playerId, cumulative);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations where attrition is modified
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whom attrition against is modified
     */
    public AttritionModifier(PhysicalCard source, Filterable locationFilter, float modifierAmount, String playerId) {
        this(source, locationFilter, null, modifierAmount, playerId);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations where attrition is modified
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whom attrition against is modified
     */
    public AttritionModifier(PhysicalCard source, Filterable locationFilter, Evaluator evaluator, String playerId) {
        this(source, locationFilter, null, evaluator, playerId);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whom attrition against is modified
     */
    public AttritionModifier(PhysicalCard source, Condition condition, float modifierAmount, String playerId) {
        this(source, Filters.any, condition, new ConstantEvaluator(modifierAmount), playerId, false);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whom attrition against is modified
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public AttritionModifier(PhysicalCard source, Condition condition, float modifierAmount, String playerId, boolean cumulative) {
        this(source, Filters.any, condition, new ConstantEvaluator(modifierAmount), playerId, cumulative);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations where attrition is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whom attrition against is modified
     */
    public AttritionModifier(PhysicalCard source, Filterable locationFilter, Condition condition, float modifierAmount, String playerId) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount), playerId, false);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations where attrition is modified
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whom attrition against is modified
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public AttritionModifier(PhysicalCard source, Filterable locationFilter, float modifierAmount, String playerId, boolean cumulative) {
        this(source, locationFilter, null, new ConstantEvaluator(modifierAmount), playerId, cumulative);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations where attrition is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whom attrition against is modified
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public AttritionModifier(PhysicalCard source, Filterable locationFilter, Condition condition, float modifierAmount, String playerId, boolean cumulative) {
        this(source, locationFilter, condition, new ConstantEvaluator(modifierAmount), playerId, cumulative);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whom attrition against is modified
     */
    public AttritionModifier(PhysicalCard source, Evaluator evaluator, String playerId) {
        this(source, Filters.any, null, evaluator, playerId, false);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whom attrition against is modified
     */
    public AttritionModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        this(source, Filters.any, condition, evaluator, playerId, false);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations where attrition is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whom attrition against is modified
     */
    public AttritionModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId) {
        this(source, locationFilter, condition, evaluator, playerId, false);
    }

    /**
     * Creates a modifier to attrition against the specified player.
     * @param source the source of the modifier
     * @param locationFilter the filter for battle locations where attrition is modified
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whom attrition against is modified
     * @param cumulative true if the modifier is cumulative, otherwise false
     */
    public AttritionModifier(PhysicalCard source, Filterable locationFilter, Condition condition, Evaluator evaluator, String playerId, boolean cumulative) {
        super(source, null, Filters.and(locationFilter, Filters.battleLocation), condition, ModifierType.ATTRITION, cumulative);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final float value = _evaluator.evaluateExpression(gameState, modifiersQuerying, self);

        String sideText;
        if (gameState.getSide(_playerId)== Side.DARK)
            sideText = "Attrition against dark side ";
        else
            sideText = "Attrition against light side ";

        if (value >= 0)
            return sideText + "+" + GuiUtils.formatAsString(value);
        else
            return sideText + GuiUtils.formatAsString(value);
    }

    @Override
    public float getAttritionModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        if (_playerId.equals(playerId))
            return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
        else
            return 0;
    }
}

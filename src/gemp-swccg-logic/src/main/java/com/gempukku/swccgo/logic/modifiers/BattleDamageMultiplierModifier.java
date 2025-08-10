package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that multiplies the originally calculated battle damage.
 */
public class BattleDamageMultiplierModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that multiplies the originally calculated battle damage for either player.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public BattleDamageMultiplierModifier(PhysicalCard source, int modifierAmount) {
        this(source, null, new ConstantEvaluator(modifierAmount), null);
    }

    /**
     * Creates a modifier that multiplies the originally calculated battle damage to the specified player.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playerId the player whose battle damage is modified
     */
    public BattleDamageMultiplierModifier(PhysicalCard source, int modifierAmount, String playerId) {
        this(source, null, new ConstantEvaluator(modifierAmount), playerId);
    }

    /**
     * Creates a modifier that multiplies the originally calculated battle damage.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playerId the player whose battle damage is modified
     */
    private BattleDamageMultiplierModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, Filters.battleLocation, condition, ModifierType.BATTLE_DAMAGE, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard battleLocation) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, battleLocation);
        String text = "Battle damage";
        if (_playerId != null) {
            text = gameState.getSide(_playerId) + " battle damage";
        }

        if (value == 3)
            return text + " is tripled";
        else if (value == 2)
            return text + " is doubled";
        else
            return text + " is *" + value;
    }

    @Override
    public float getMultiplierValue(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, location);
    }
}

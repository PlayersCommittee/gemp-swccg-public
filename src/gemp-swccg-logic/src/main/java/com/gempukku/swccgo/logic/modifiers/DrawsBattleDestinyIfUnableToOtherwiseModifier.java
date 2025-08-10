package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier that specifies "Draws X battle destiny if unable to otherwise".
 */
public class DrawsBattleDestinyIfUnableToOtherwiseModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier that specifies "Draws X battle destiny if unable to otherwise".
     * @param source the card that is the source of the modifier and that can draw battle destiny
     * @param modifierAmount the number of destiny
     */
    public DrawsBattleDestinyIfUnableToOtherwiseModifier(PhysicalCard source, int modifierAmount) {
        this(source, null, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that specifies "Draws X battle destiny if unable to otherwise".
     * @param source the card that is the source of the modifier
     * @param affectedFilter the filter for cards that can draw battle destiny
     * @param modifierAmount the number of destiny
     */
    public DrawsBattleDestinyIfUnableToOtherwiseModifier(PhysicalCard source, Filterable affectedFilter, int modifierAmount) {
        this(source, affectedFilter, null, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that specifies "Draws X battle destiny if unable to otherwise".
     * @param source the card that is the source of the modifier and that can draw battle destiny
     * @param evaluator the evaluator that calculates the number of destiny
     */
    public DrawsBattleDestinyIfUnableToOtherwiseModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, null, evaluator);
    }

    /**
     * Creates a modifier that specifies "Draws X battle destiny if unable to otherwise".
     * @param source the card that is the source of the modifier and that can draw battle destiny
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the number of destiny
     */
    public DrawsBattleDestinyIfUnableToOtherwiseModifier(PhysicalCard source, Condition condition, int modifierAmount) {
        this(source, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that specifies "Draws X battle destiny if unable to otherwise".
     * @param source the card that is the source of the modifier and that can draw battle destiny
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the number of destiny
     */
    public DrawsBattleDestinyIfUnableToOtherwiseModifier(PhysicalCard source, Condition condition, Evaluator evaluator) {
        this(source, source, condition, evaluator);
    }

    /**
     * Creates a modifier that specifies "Draws X battle destiny if unable to otherwise".
     * @param source the card that is the source of the modifier and that can draw battle destiny
     * @param affectedFilter the filter for cards that can draw battle destiny
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the number of destiny
     */
    public DrawsBattleDestinyIfUnableToOtherwiseModifier(PhysicalCard source, Filterable affectedFilter, Condition condition, int modifierAmount) {
        this(source, affectedFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier that specifies "Draws X battle destiny if unable to otherwise".
     * @param source the source of the modifier
     * @param affectedFilter the filter for cards that can draw battle destiny
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the number of destiny
     */
    public DrawsBattleDestinyIfUnableToOtherwiseModifier(PhysicalCard source, Filterable affectedFilter, Condition condition, Evaluator evaluator) {
        super(source, null, Filters.and(Filters.participatingInBattle, affectedFilter), condition, ModifierType.MIN_BATTLE_DESTINY_DRAWS, true);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        return "Draws " + value + " battle destiny if unable to otherwise";
    }

    @Override
    public int getMinimumBattleDestinyDrawsModifier(GameState gameState, ModifiersQuerying modifiersQuerying) {
        return (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
    }
}

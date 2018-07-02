package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;

/**
 * A modifier to destiny value when card is drawn for destiny.
 */
public class DestinyWhenDrawnForDestinyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    /**
     * Creates a modifier to destiny value when source card is drawn for destiny.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public DestinyWhenDrawnForDestinyModifier(PhysicalCard source, float modifierAmount) {
        this(source, source, null, modifierAmount);
    }

    /**
     * Creates a modifier to destiny value when source card is drawn for destiny.
     * @param source the source of the modifier
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public DestinyWhenDrawnForDestinyModifier(PhysicalCard source, Condition condition, float modifierAmount) {
        this(source, source, condition, modifierAmount);
    }

    /**
     * Creates a modifier to destiny value when card is drawn for destiny.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified when drawn for destiny
     * @param modifierAmount the amount of the modifier
     */
    public DestinyWhenDrawnForDestinyModifier(PhysicalCard source, Filterable affectFilter, float modifierAmount) {
        this(source, affectFilter, null, modifierAmount);
    }

    /**
     * Creates a modifier to destiny value when card is drawn for destiny.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified when drawn for destiny
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DestinyWhenDrawnForDestinyModifier(PhysicalCard source, Filterable affectFilter, Evaluator evaluator) {
        this(source, affectFilter, null, evaluator);
    }

    /**
     * Creates a modifier to destiny value when card is drawn for destiny.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified when drawn for destiny
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param modifierAmount the amount of the modifier
     */
    public DestinyWhenDrawnForDestinyModifier(PhysicalCard source, Filterable affectFilter, Condition condition, float modifierAmount) {
        this(source, affectFilter, condition, new ConstantEvaluator(modifierAmount));
    }

    /**
     * Creates a modifier to destiny value when card is drawn for destiny.
     * @param source the source of the modifier
     * @param affectFilter the filter for cards whose destiny value is modified when drawn for destiny
     * @param condition the condition that must be fulfilled for the modifier to be in effect
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DestinyWhenDrawnForDestinyModifier(PhysicalCard source, Filterable affectFilter, Condition condition, Evaluator evaluator) {
        super(source, null, affectFilter, condition, ModifierType.DESTINY_WHEN_DRAWN_FOR_DESTINY, false);
        _evaluator = evaluator;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        return null;
    }

    @Override
    public float getDestinyWhenDrawnForDestinyModifier(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}

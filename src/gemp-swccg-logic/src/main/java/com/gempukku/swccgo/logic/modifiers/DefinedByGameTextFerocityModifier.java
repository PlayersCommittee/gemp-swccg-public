package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to define the ferocity of a creature.
 * This is used when the printed ferocity of a creature is defined by game text.
 */
public class DefinedByGameTextFerocityModifier extends AbstractModifier {
    private Evaluator _baseEvaluator;
    private int _numDestiny;

    /**
     * Creates a modifier to define the ferocity of a creature.
     * @param source the source of the modifier
     * @param numDestiny the number of destiny in ferocity value
     */
    public DefinedByGameTextFerocityModifier(PhysicalCard source, int numDestiny) {
        this(source, 0, numDestiny);
    }

    /**
     * Creates a modifier to define the ferocity of a creature.
     * @param source the source of the modifier
     * @param baseValue the base ferocity value
     * @param numDestiny the number of destiny in ferocity value
     */
    public DefinedByGameTextFerocityModifier(PhysicalCard source, float baseValue, int numDestiny) {
        this(source, new ConstantEvaluator(baseValue), numDestiny);
    }

    /**
     * Creates a modifier to define the ferocity of a creature.
     * @param source the source of the modifier
     * @param baseEvaluator the evaluator that calculates the base ferocity value
     * @param numDestiny the number of destiny in ferocity value
     */
    public DefinedByGameTextFerocityModifier(PhysicalCard source, Evaluator baseEvaluator, int numDestiny) {
        super(source, null, source, null, ModifierType.PRINTED_FEROCITY, true);
        _baseEvaluator = baseEvaluator;
        _numDestiny = numDestiny;
    }

    @Override
    public float getBaseFerocityDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _baseEvaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }

    @Override
    public int getNumFerocityDestinyDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _numDestiny;
    }
}

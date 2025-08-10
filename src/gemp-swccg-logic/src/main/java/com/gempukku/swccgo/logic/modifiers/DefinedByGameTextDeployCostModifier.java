package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.PlayCardOption;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * A modifier to define the initial printed deploy cost of a card.
 * This is used when the printed deploy cost of a card is defined by game text.
 */
public class DefinedByGameTextDeployCostModifier extends AbstractModifier {
    private Evaluator _evaluator;
    private PlayCardOptionId _playCardOptionId;

    /**
     * Creates a modifier to define the initial printed deploy cost of a card.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     */
    public DefinedByGameTextDeployCostModifier(PhysicalCard source, int modifierAmount) {
        this(source, new ConstantEvaluator(modifierAmount), null, null);
    }

    /**
     * Creates a modifier to define the initial printed deploy cost of a card.
     *
     * @param source         the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param condition      the condition that must be fulfilled for the modifier to be in effect
     */
    public DefinedByGameTextDeployCostModifier(PhysicalCard source, int modifierAmount, Condition condition) {
        this(source, new ConstantEvaluator(modifierAmount), null, condition);
    }

    /**
     * Creates a modifier to define the initial printed deploy cost of a card.
     * @param source the source of the modifier
     * @param modifierAmount the amount of the modifier
     * @param playCardOptionId the identifier for which play card option the modifier applies
     */
    public DefinedByGameTextDeployCostModifier(PhysicalCard source, int modifierAmount, PlayCardOptionId playCardOptionId) {
        this(source, new ConstantEvaluator(modifierAmount), playCardOptionId, null);
    }

    /**
     * Creates a modifier to define the initial printed deploy cost of a card.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     */
    public DefinedByGameTextDeployCostModifier(PhysicalCard source, Evaluator evaluator) {
        this(source, evaluator, null, null);
    }

    /**
     * Creates a modifier to define the initial printed deploy cost of a card.
     * @param source the source of the modifier
     * @param evaluator the evaluator that calculates the amount of the modifier
     * @param playCardOptionId the identifier for which play card option the modifier applies
     */
    private DefinedByGameTextDeployCostModifier(PhysicalCard source, Evaluator evaluator, PlayCardOptionId playCardOptionId, Condition condition) {
        super(source, null, source, condition, ModifierType.PRINTED_DEPLOY_COST, true);
        _evaluator = evaluator;
        _playCardOptionId = playCardOptionId;
    }

    @Override
    public boolean isPlayCardOption(PlayCardOption playCardOption) {
        return (_playCardOptionId == null || (playCardOption != null && _playCardOptionId == playCardOption.getId()));
    }

    @Override
    public float getPrintedValueDefinedByGameText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
        return _evaluator.evaluateExpression(gameState, modifiersQuerying, card);
    }
}

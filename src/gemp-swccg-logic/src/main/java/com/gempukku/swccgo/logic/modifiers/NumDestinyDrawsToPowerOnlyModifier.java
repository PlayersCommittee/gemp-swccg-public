package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

public class NumDestinyDrawsToPowerOnlyModifier extends AbstractModifier {
    private Evaluator _evaluator;

    public NumDestinyDrawsToPowerOnlyModifier(PhysicalCard source, int modifier, String playerId, Filterable affectFilter) {
        this(source, null, modifier, playerId, affectFilter);
    }

    public NumDestinyDrawsToPowerOnlyModifier(PhysicalCard source, Condition condition, int modifier, String playerId) {
        this(source, condition, new ConstantEvaluator(modifier), playerId, Filters.any);
    }

    public NumDestinyDrawsToPowerOnlyModifier(PhysicalCard source, Condition condition, int modifier, String playerId, Filterable affectFilter) {
        this(source, condition, new ConstantEvaluator(modifier), playerId, affectFilter);
    }

    public NumDestinyDrawsToPowerOnlyModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId, Filterable affectFilter) {
        super(source, null, Filters.and(Filters.battleLocation, affectFilter), condition, ModifierType.NUM_DESTINY_DRAWS_TO_POWER_ONLY, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (_playerId.equals(gameState.getDarkPlayer()))
            return "Dark Side: Add " + value + " destiny to power only";
        else
            return "Light Side: Add " + value + " destiny to power only";
    }

    @Override
    public int getNumDestinyDrawsToPowerOnlyModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (_playerId.equals(playerId))
            return (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
        else
            return 0;
    }
}

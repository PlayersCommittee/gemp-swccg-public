package com.gempukku.swccgo.logic.modifiers;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

public class NumLightsaberCombatDestinyDrawsModifier extends AbstractModifier {
    private Evaluator _evaluator;


    public NumLightsaberCombatDestinyDrawsModifier(PhysicalCard source, int modifier, String playerId) {
        this(source, null, modifier, playerId);
    }

    public NumLightsaberCombatDestinyDrawsModifier(PhysicalCard source, Condition condition, int modifier, String playerId) {
        this(source, condition, new ConstantEvaluator(modifier), playerId);
    }

    public NumLightsaberCombatDestinyDrawsModifier(PhysicalCard source, Condition condition, Evaluator evaluator, String playerId) {
        super(source, null, null, condition, ModifierType.NUM_LIGHTSABER_COMBAT_DESTINY_DRAWS, false);
        _evaluator = evaluator;
        _playerId = playerId;
    }

    @Override
    public String getText(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard self) {
        final int value = (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, self);
        if (_playerId.equals(gameState.getDarkPlayer()))
            return "Dark Side: Add " + value + " lightsaber combat destiny";
        else
            return "Light Side: Add " + value + " lightsaber combat destiny";
    }

    @Override
    public int getNumLightsaberCombatDestinyDrawsModifier(String playerId, GameState gameState, ModifiersQuerying modifiersQuerying) {
        if (_playerId.equals(playerId))
            return (int) _evaluator.evaluateExpression(gameState, modifiersQuerying, null);
        else
            return 0;
    }
}

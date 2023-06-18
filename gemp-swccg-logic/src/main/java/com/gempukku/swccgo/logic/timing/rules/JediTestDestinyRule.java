package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.EachTrainingDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;

public class JediTestDestinyRule implements Rule {
    private ModifiersEnvironment _modifiersEnvironment;

    public JediTestDestinyRule(ModifiersEnvironment modifiersEnvironment) {
        _modifiersEnvironment = modifiersEnvironment;
    }

    public void applyRule() {
        Evaluator evaluator = new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                return Filters.countTopLocationsOnTable(gameState.getGame(), Filters.and(Filters.Dagobah_site, Filters.not(Filters.generic)));
            }

            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                return Filters.countTopLocationsOnTable(gameState.getGame(), Filters.and(Filters.Dagobah_site, Filters.not(Filters.generic)));
            }
        };
        _modifiersEnvironment.addAlwaysOnModifier(
                new EachTrainingDestinyModifier(null, Filters.Jedi_Test, evaluator));
    }
}

package com.gempukku.swccgo.cards.actions;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.AbstractTopLevelRuleAction;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

public class TestInitiateDuelAction extends AbstractTopLevelRuleAction {
    private PhysicalCard _location;
    private boolean _duelInitiated;
    private Effect _duelEffect;

    public TestInitiateDuelAction(String playerId, final PhysicalCard location, PhysicalCard darkSideCharacter, PhysicalCard lightSideCharacter, final boolean isCrossOverToDarkSideAttempt, final boolean isEpicDuel) {
        super(location, playerId);
        _location = location;

        _duelEffect = new DuelEffect(this, darkSideCharacter, lightSideCharacter, new DuelDirections() {
            @Override
            public boolean isEpicDuel() {
                return isEpicDuel;
            }

            @Override
            public boolean isCrossOverToDarkSideAttempt() {
                return isCrossOverToDarkSideAttempt;
            }

            @Override
            public Evaluator getBaseDuelTotal(final String playerId, final DuelState duelState) {
                return new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                        PhysicalCard character = duelState.getCharacter(playerId);
                        float total =  modifiersQuerying.getPower(gameState, character);
                        if (Filters.character_with_a_lightsaber.accepts(gameState, modifiersQuerying, character)) {
                            total += 2;
                        }
                        return total;
                    }
                };
            }

            @Override
            public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) {
                return 0;
            }

            @Override
            public void performDuelDirections(Action action, SwccgGame game, DuelState duelState) {

            }

            @Override
            public void performDuelResults(Action action, SwccgGame game, DuelState duelState) {
                // Place losing character out of play
                action.appendEffect(
                        new PlaceCardOutOfPlayFromTableEffect(action, duelState.getLosingCharacter()));
            }
        });
    }

    @Override
    public PhysicalCard getActionSource() {
        return _location;
    }

    @Override
    public String getText() {
        return "TEST: 'duel'";
    }

    @Override
    public Effect nextEffect(SwccgGame game) {
        if (!isAnyCostFailed()) {
            Effect cost = getNextCost();
            if (cost != null)
                return cost;

            if (!_duelInitiated) {
                _duelInitiated = true;

                return _duelEffect;
            }

            Effect effect = getNextEffect();
            if (effect != null)
                return effect;
        }

        return null;
    }


    @Override
    public boolean wasActionCarriedOut() {
        return _duelInitiated;
    }
}

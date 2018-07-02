package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.SendMessageEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Enforces the rule that if an asteroid sector is drawn for asteroid destiny, the targeted starship is immediately lost.
 */
public class AsteroidDestinyRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that if an asteroid sector is drawn for asteroid destiny, the targeted starship is immediately lost.
     * @param actionsEnvironment the actions environment
     */
    public AsteroidDestinyRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        GameState gameState = game.getGameState();
                        PhysicalCard starship = game.getGameState().getStarshipDrawingAsteroidDestinyAgainst();

                        if (starship != null
                                && TriggerConditions.isAsteroidDestinyJustDrawnMatching(game, effectResult, Filters.asteroid_sector)
                                && !game.getModifiersQuerying().notImmediatelyLostIfAsteroidSectorDrawnForAsteroidDestiny(gameState, starship)) {

                            RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                            action.setText("Make " + GameUtils.getCardLink(starship) + " lost");
                            action.appendEffect(
                                    new SendMessageEffect(action, "Result: Succeeded due to asteroid sector drawn for asteroid destiny"));
                            action.appendEffect(
                                    new LoseCardFromTableEffect(action, starship));
                            return Collections.singletonList((TriggerAction) action);
                        }
                        return null;
                    }
                });
    }
}

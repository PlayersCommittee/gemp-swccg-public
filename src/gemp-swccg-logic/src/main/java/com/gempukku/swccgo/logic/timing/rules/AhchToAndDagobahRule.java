package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.List;

/**
 * Enforces the rule that once a player deploys a location for Ahch-To or Dagobah, that player may not deploy any locations for
 * the other Jedi training for the rest of the game.
 */
public class AhchToAndDagobahRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;

    /**
     * Creates a rule that once a player deploys a location for Ahch-To or Dagobah, that player may not deploy any locations for
     * the other Jedi training for the rest of the game.
     * @param actionsEnvironment the actions environment
     */
    public AhchToAndDagobahRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        GameState gameState = game.getGameState();
                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                        if (TriggerConditions.justDeployed(game, effectResult, Filters.location)) {
                            PlayCardResult playCardResult = (PlayCardResult) effectResult;
                            String playerId = playCardResult.getPerformingPlayerId();

                            // A player can only deploy either AhchTo or Dagobah locations over the course of the game, not both.
                            if (!gameState.isDeployedAhchToDagobahLocation(playerId)) {
                                PhysicalCard deployedLocation = playCardResult.getPlayedCard();

                                if (Filters.AhchTo_location.accepts(gameState, modifiersQuerying, deployedLocation)) {
                                    gameState.setDeployedAhchToDagobahLocation(playerId);
                                    game.getModifiersEnvironment().addUntilEndOfGameModifier(
                                            new MayNotDeployModifier(null, Filters.Dagobah_location, playerId));
                                }
                                else if (Filters.Dagobah_location.accepts(gameState, modifiersQuerying, deployedLocation)) {
                                    gameState.setDeployedAhchToDagobahLocation(playerId);
                                    game.getModifiersEnvironment().addUntilEndOfGameModifier(
                                            new MayNotDeployModifier(null, Filters.AhchTo_location, playerId));
                                }
                            }
                        }

                        return null;
                    }
                }
        );
    }
}
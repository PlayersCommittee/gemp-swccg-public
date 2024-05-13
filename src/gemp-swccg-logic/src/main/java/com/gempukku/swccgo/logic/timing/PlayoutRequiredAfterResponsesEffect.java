package com.gempukku.swccgo.logic.timing;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.decisions.ActionSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * An effect for playing out the required "after" responses to an effect result.
 */
public class PlayoutRequiredAfterResponsesEffect extends AbstractSuccessfulEffect {
    private Set<EffectResult> _effectResults;

    public PlayoutRequiredAfterResponsesEffect(Action action, Set<EffectResult> effectResults) {
        super(action);
        _effectResults = effectResults;
    }

    @Override
    public void doPlayEffect(final SwccgGame game) {
        final Map<TriggerAction, EffectResult> requiredAfterTriggers = game.getActionsEnvironment().getRequiredAfterTriggers(_effectResults);

        if (!requiredAfterTriggers.isEmpty()) {
            // If only one action, add it to the action stack.
            // Or if all the actions are the same, just add the first one to the action stack.
            // Or if the order does not matter for the involved effect results, just add the first one to the action stack.
            if (requiredAfterTriggers.size() == 1 || TurnProcedure.areAllTriggerActionsTheSame(requiredAfterTriggers.keySet()) || TurnProcedure.doesOrderNotMatterForEffectResult(requiredAfterTriggers.values())) {
                TriggerAction action = requiredAfterTriggers.keySet().iterator().next();
                // Add the action to the action stack, mark trigger as used, and play out required
                // "after" responses again in case there are more triggers after this trigger is processed.
                game.getActionsEnvironment().addActionToStack(action);
                requiredAfterTriggers.get(action).triggerUsed(action);
                _action.insertEffect(new PlayoutRequiredAfterResponsesEffect(_action, _effectResults));
            }
            // Otherwise, ask the player whose turn it is, which action to perform first.
            else {
                String actionDesc = TurnProcedure.getActionDecisionTextFromEffectResults(game, requiredAfterTriggers.keySet(), _effectResults, "Required responses");
                game.getUserFeedback().sendAwaitingDecision(game.getGameState().getCurrentPlayerId(),
                        new ActionSelectionDecision(actionDesc, new LinkedList<Action>(requiredAfterTriggers.keySet())) {
                            @Override
                            public void decisionMade(String result) throws DecisionResultInvalidException {
                                TriggerAction action = (TriggerAction) getSelectedAction(result);
                                // Add the action to the action stack, mark trigger as used, and play out required
                                // "after" responses again in case there are more triggers after this trigger is processed.
                                game.getActionsEnvironment().addActionToStack(action);
                                requiredAfterTriggers.get(action).triggerUsed(action);
                                _action.insertEffect(new PlayoutRequiredAfterResponsesEffect(_action, _effectResults));
                            }
                        }
                );
            }
        }
    }
}


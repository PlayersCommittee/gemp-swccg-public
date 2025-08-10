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
import com.gempukku.swccgo.logic.effects.RotateCardEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the effects of Revolution.
 */
public class EffectsOfRevolutionRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that enforces the effects of Revolution.
     * @param actionsEnvironment the actions environment
     */
    public EffectsOfRevolutionRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        // Check condition(s)
                        if (TriggerConditions.isTableChanged(game, effectResult)) {

                            List<TriggerAction> triggerActions = new LinkedList<TriggerAction>();

                            GameState gameState = game.getGameState();
                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                            // Check each location and compare its "isInverted" state with whether it should be rotated.
                            for (PhysicalCard location : Filters.filterTopLocationsOnTable(game, Filters.any)) {
                                boolean shouldBeRotated = modifiersQuerying.isRotatedLocation(gameState, location);
                                if (shouldBeRotated != location.isInverted()) {

                                    RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, location);
                                    action.skipInitialMessageAndAnimation();
                                    action.setSingletonTrigger(true);
                                    action.setText("Rotate " + GameUtils.getFullName(location));
                                    // Perform result(s)
                                    action.appendEffect(
                                            new RotateCardEffect(action, location, shouldBeRotated));
                                    triggerActions.add(action);
                                }
                            }

                            return triggerActions;
                        }
                        return null;
                    }
                }
        );
    }
}

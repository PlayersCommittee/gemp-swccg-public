package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddToBlownAwayForceLossEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.BlownAwayCalculateForceLossStepResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Enforces the rule that causes the Light side player Force loss for each Light side icon on a Hoth location when
 * Hoth is 'blown away' (or causes the Light side player Force loss for each Light side icon on a Yavin 4 location
 * when Yavin 4 is 'blown away').
 */
public class BlownAwayRebelBaseRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that causes the Light side player Force loss for each Light side icon on a Hoth location when
     * Hoth is 'blown away' (or causes the Light side player Force loss for each Light side icon on a Yavin 4 location
     * when Yavin 4 is 'blown away').
     * @param actionsEnvironment the actions environment
     */
    public BlownAwayRebelBaseRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        GameState gameState = game.getGameState();
                        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                        String lightSidePlayerId = game.getLightPlayer();

                        // Check condition(s)
                        if (TriggerConditions.isBlownAwayCalculateForceLossStep(game, effectResult, Filters.or(Filters.Hoth_system, Filters.Yavin_4_system))) {
                            PhysicalCard blownAwayCard = ((BlownAwayCalculateForceLossStepResult) effectResult).getBlownAwayCard();
                            int iconCount = 0;
                            if (Filters.Hoth_system.accepts(game, blownAwayCard)) {
                                Collection<PhysicalCard> hothLocations = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.Hoth_location, Filters.not(Filters.generic)));
                                for (PhysicalCard hothLocation : hothLocations) {
                                    iconCount += modifiersQuerying.getIconCount(gameState, hothLocation, Icon.LIGHT_FORCE);
                                }
                            }
                            else if (Filters.Yavin_4_system.accepts(game, blownAwayCard)) {
                                Collection<PhysicalCard> yavin4Locations = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.Yavin_4_location, Filters.not(Filters.generic)));
                                for (PhysicalCard yavin4Location : yavin4Locations) {
                                    iconCount += modifiersQuerying.getIconCount(gameState, yavin4Location, Icon.LIGHT_FORCE);
                                }
                            }

                            if (iconCount > 0) {
                                int forceLoss = iconCount * 2;

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.skipInitialMessageAndAnimation();
                                // Perform result(s)
                                action.appendEffect(
                                        new AddToBlownAwayForceLossEffect(action, lightSidePlayerId, forceLoss));
                                return Collections.singletonList((TriggerAction) action);
                            }
                        }

                        return null;
                    }
                }
        );
    }
}

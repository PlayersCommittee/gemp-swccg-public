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
 * Enforces the rule that causes the Dark side player Force loss for each Dark side icon on a Death Star location when
 * Death Star is 'blown away' (or causes the Dark side player Force loss for each Dark side icon on a Death Star II location
 * when Death Star II is 'blown away').
 */
public class BlownAwayDeathStarOrDeathStarIIRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that causes the Dark side player Force loss for each Dark side icon on a Death Star location when
     * Death Star is 'blown away' (or causes the Dark side player Force loss for each Dark side icon on a Death Star II
     * location when Death Star II is 'blown away').
     * @param actionsEnvironment the actions environment
     */
    public BlownAwayDeathStarOrDeathStarIIRule(ActionsEnvironment actionsEnvironment) {
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
                        String darkSidePlayerId = game.getDarkPlayer();

                        // Check condition(s)
                        if (TriggerConditions.isBlownAwayCalculateForceLossStep(game, effectResult, Filters.or(Filters.Death_Star_system, Filters.Death_Star_II_system))) {
                            PhysicalCard blownAwayCard = ((BlownAwayCalculateForceLossStepResult) effectResult).getBlownAwayCard();
                            int iconCount = 0;
                            if (Filters.Death_Star_system.accepts(game, blownAwayCard)) {
                                Collection<PhysicalCard> deathStarLocations = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.Death_Star_location, Filters.not(Filters.generic)));
                                for (PhysicalCard deathStarLocation : deathStarLocations) {
                                    iconCount += modifiersQuerying.getIconCount(gameState, deathStarLocation, Icon.DARK_FORCE);
                                }
                            }
                            else if (Filters.Death_Star_II_system.accepts(game, blownAwayCard)) {
                                Collection<PhysicalCard> deathStarIILocations = Filters.filterTopLocationsOnTable(game, Filters.and(Filters.Death_Star_II_location, Filters.not(Filters.generic)));
                                for (PhysicalCard deathStarIILocation : deathStarIILocations) {
                                    iconCount += modifiersQuerying.getIconCount(gameState, deathStarIILocation, Icon.DARK_FORCE);
                                }
                            }

                            if (iconCount > 0) {
                                int forceLoss = iconCount * 2;

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.skipInitialMessageAndAnimation();
                                // Perform result(s)
                                action.appendEffect(
                                        new AddToBlownAwayForceLossEffect(action, darkSidePlayerId, forceLoss));
                                return Collections.singletonList((TriggerAction) action);
                            }
                        }

                        return null;
                    }
                }
        );
    }
}

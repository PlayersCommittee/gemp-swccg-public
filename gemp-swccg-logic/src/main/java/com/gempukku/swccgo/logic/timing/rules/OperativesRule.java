package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployOrMoveOperativeToLocationsRuleModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the game rules for Operatives.
 */
public class OperativesRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private ModifiersEnvironment _modifiersEnvironment;
    private Rule _that;

    /**
     * Creates the game rules for Operatives.
     * @param actionsEnvironment the actions environment
     * @param modifiersEnvironment the modifiers environment
     */
    public OperativesRule(ActionsEnvironment actionsEnvironment, ModifiersEnvironment modifiersEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _modifiersEnvironment = modifiersEnvironment;
        _that = this;
    }

    public void applyRule() {
        _modifiersEnvironment.addAlwaysOnModifier(new MayNotDeployOrMoveOperativeToLocationsRuleModifier(null));
        _modifiersEnvironment.addAlwaysOnModifier(new MayNotApplyAbilityForBattleDestinyModifier(null, Filters.operativeOnMatchingPlanet));
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        // Check condition(s)
                        if (TriggerConditions.isTableChanged(game, effectResult)) {

                            List<TriggerAction> triggerActions = new LinkedList<TriggerAction>();

                            // Check all the cards in play to see if any matching Operatives owned by same player exist at the same location
                            String darkSidePlayerId = game.getDarkPlayer();
                            Filter darkFilter = Filters.and(Filters.owner(darkSidePlayerId), Filters.operativeOnMatchingPlanet);
                            Collection<PhysicalCard> darkOperatives = Filters.filterAllOnTable(game, Filters.and(darkFilter, Filters.with(null, SpotOverride.INCLUDE_ALL, darkFilter)));
                            if (!darkOperatives.isEmpty()) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.setText(darkSidePlayerId + "'s operative lost");
                                action.setMessage(darkSidePlayerId + " must choose operative to be lost");
                                action.appendEffect(
                                        new ChooseCardToLoseFromTableEffect(action, darkSidePlayerId, true, Filters.in(darkOperatives)));
                                triggerActions.add(action);
                            }

                            String lightSidePlayerId = game.getLightPlayer();
                            Filter lightFilter = Filters.and(Filters.owner(lightSidePlayerId), Filters.operativeOnMatchingPlanet);
                            Collection<PhysicalCard> lightOperatives = Filters.filterAllOnTable(game, Filters.and(lightFilter, Filters.with(null, SpotOverride.INCLUDE_ALL, lightFilter)));
                            if (!lightOperatives.isEmpty()) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.setText(lightSidePlayerId + "'s operative lost");
                                action.setMessage(lightSidePlayerId + " must choose operative to be lost");
                                action.appendEffect(
                                        new ChooseCardToLoseFromTableEffect(action, lightSidePlayerId, true, Filters.in(lightOperatives)));
                                triggerActions.add(action);
                            }

                            return triggerActions;
                        }
                        return null;
                    }
                }
        );
    }
}

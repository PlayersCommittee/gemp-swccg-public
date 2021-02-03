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
import com.gempukku.swccgo.logic.effects.choose.StealCapturedStarshipEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardsToLocationEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotApplyAbilityForBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployOrMoveOperativeToLocationsRuleModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the game rules for stealing captured starships with no characters aboard.
 */
public class StealCapturedStarshipWithNoCharactersRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates the game rules for stealing captured starships with no characters aboard.
     * @param actionsEnvironment the actions environment
     */
    public StealCapturedStarshipWithNoCharactersRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        // Check condition(s)
                        if (TriggerConditions.captured(game, effectResult, Filters.starship)
                                || TriggerConditions.isTableChanged(game, effectResult)) {

                            List<TriggerAction> triggerActions = new LinkedList<TriggerAction>();

                            String darkSidePlayerId = game.getDarkPlayer();

                            // Check all the cards in play to see if there are any captured starships with no characters aboard
                            Collection<PhysicalCard> starshipsThatShouldBeStolen = Filters.filterAllOnTable(game,
                                    Filters.and(Filters.opponents(darkSidePlayerId), Filters.captured_starship,
                                            //Filters.not(Filters.aboardOrAboardCargoOf(null, Filters.character))
                                            Filters.not(Filters.hasAboard(null, SpotOverride.INCLUDE_ALL, Filters.character))
                                    )
                            );

                            if (!starshipsThatShouldBeStolen.isEmpty()) {
                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.setText("Steal captured starship with no characters aboard");
                                action.setMessage("Steal captured starship with no characters aboard");
                                action.appendEffect(new StealCapturedStarshipEffect(action, darkSidePlayerId, starshipsThatShouldBeStolen));

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
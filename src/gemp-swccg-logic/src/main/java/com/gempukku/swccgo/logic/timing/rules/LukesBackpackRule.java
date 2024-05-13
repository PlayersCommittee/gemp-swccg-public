package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Enforces the game rule that causes the character in Luke's Backpack to disembark if Luke's Backpack is about to leave table.
 */
public class LukesBackpackRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that causes the character in Luke's Backpack to disembark if Luke's Backpack is about to leave table.
     * @param actionsEnvironment the actions environment
     */
    public LukesBackpackRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {

                        // Check condition(s)
                        if (TriggerConditions.isAboutToLeaveTable(game, effectResult, Filters.or(Filters.Lukes_Backpack, Filters.hasAttached(Filters.Lukes_Backpack)))) {
                            Collection<PhysicalCard> charactersInLukesBackpack = Filters.filterAllOnTable(game, Filters.and(Filters.character, Filters.attachedTo(Filters.Lukes_Backpack)));
                            if (effectResult.getType() == EffectResult.Type.ABOUT_TO_BE_LOST_FROM_TABLE) {
                                AboutToLoseCardFromTableResult aboutToLoseCardFromTableResult = (AboutToLoseCardFromTableResult) effectResult;
                                if (aboutToLoseCardFromTableResult.isAllCardsSituation()) {
                                    charactersInLukesBackpack = Filters.filter(charactersInLukesBackpack, game, Filters.not(Filters.in(aboutToLoseCardFromTableResult.getAllCardsAboutToBeLost())));
                                }
                            }
                            if (!charactersInLukesBackpack.isEmpty()) {

                                // Determine characters that may "jump off"
                                List<TriggerAction> actions = new ArrayList<TriggerAction>();

                                for (PhysicalCard characterInLukesBackpack : charactersInLukesBackpack) {
                                    Action disembarkAction = characterInLukesBackpack.getBlueprint().getDisembarkAction(characterInLukesBackpack.getOwner(), game, characterInLukesBackpack, true, true, Filters.any);
                                    if (disembarkAction != null) {

                                        // Create action for character to "jump off"
                                        RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, characterInLukesBackpack);
                                        action.setText("Disembark");
                                        action.skipInitialMessageAndAnimation();
                                        action.appendEffect(
                                                new StackActionEffect(action, disembarkAction));
                                        actions.add(action);
                                    }
                                }

                                return actions;
                            }
                        }

                        return null;
                    }
                }
        );
    }
}

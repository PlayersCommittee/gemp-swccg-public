package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.BreakCoverEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the game rule that if an undercover card is no longer a spy, then its cover is broken.
 */
public class BreakCoverWhenNotSpyRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that if an undercover card is no longer a spy, then its cover is broken.
     * @param actionsEnvironment the actions environment
     */
    public BreakCoverWhenNotSpyRule(ActionsEnvironment actionsEnvironment) {
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

                            List<TriggerAction> actions = new LinkedList<TriggerAction>();

                            // Check for any cards that are undercover but are not spies.
                            Collection<PhysicalCard> undercoverButNotSpies = Filters.filterAllOnTable(game, Filters.and(Filters.undercover_spy, Filters.not(Filters.spy)));
                            for (PhysicalCard undercoverButNotSpy : undercoverButNotSpies) {
                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, undercoverButNotSpy);
                                action.setSingletonTrigger(true);
                                action.setText("'Break cover'");
                                action.appendEffect(
                                        new BreakCoverEffect(action, undercoverButNotSpy));
                                actions.add(action);
                            }
                            return actions;
                        }
                        return null;
                    }
                }
        );
    }
}

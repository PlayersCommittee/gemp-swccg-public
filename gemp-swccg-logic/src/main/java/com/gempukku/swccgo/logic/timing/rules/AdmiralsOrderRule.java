package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collections;
import java.util.List;

/**
 * Enforces the game rule that when an Admiral's Order is deployed, an Admiral's Order already on the table (if any)
 * is placed in its owner's Used Pile.
 */
public class AdmiralsOrderRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that when an Admiral's Order is deployed, an Admiral's Order already on the table (if any) is
     * placed in its owner's Used Pile.
     * @param actionsEnvironment the actions environment
     */
    public AdmiralsOrderRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        // Check condition(s)
                        if (TriggerConditions.justDeployed(game, effectResult, Filters.Admirals_Order)) {
                            PhysicalCard playedCard = ((PlayCardResult) effectResult).getPlayedCard();
                            PhysicalCard otherAdmiralsOrder = Filters.findFirstFromAllOnTable(game, Filters.and(Filters.Admirals_Order, Filters.not(playedCard)));
                            if (otherAdmiralsOrder != null) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, otherAdmiralsOrder);
                                action.setText("Place in Used Pile");
                                // Perform result(s)
                                action.appendEffect(
                                        new PlaceCardInUsedPileFromTableEffect(action, otherAdmiralsOrder));
                                return Collections.singletonList((TriggerAction) action);
                            }
                        }
                        return null;
                    }
                });
    }
}

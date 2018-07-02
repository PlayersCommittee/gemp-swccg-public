package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.ReleaseCaptivesEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the game rule that if a Light Side character is escorting a captive, the captive is released.
 */
public class ReleaseCaptivesWithLightSideEscortRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that if a Light Side character is escorting a captive, the captive is released.
     * @param actionsEnvironment the actions environment
     */
    public ReleaseCaptivesWithLightSideEscortRule(ActionsEnvironment actionsEnvironment) {
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

                            // Check for any Light Side cards escorting a captive
                            Collection<PhysicalCard> lightSideEscorts = Filters.filterAllOnTable(game, Filters.and(Filters.escort, Filters.owner(game.getLightPlayer())));
                            for (PhysicalCard lightSideEscort : lightSideEscorts) {
                                List<PhysicalCard> captives = game.getGameState().getCaptivesOfEscort(lightSideEscort);
                                if (!captives.isEmpty()) {

                                    RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, lightSideEscort);
                                    action.setSingletonTrigger(true);
                                    action.setText("Release escorted captives");
                                    action.setMessage("Release escorted captives of " + GameUtils.getCardLink(lightSideEscort));
                                    action.appendEffect(
                                            new ReleaseCaptivesEffect(action, captives, true));
                                    actions.add(action);
                                }
                            }
                            return actions;
                        }
                        return null;
                    }
                }
        );
    }
}

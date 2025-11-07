package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.LoseCardsFromTableSimultaneouslyEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Enforces the rule that causes cards to be lost if they are attached to an invalid card.
 */
public class AttachedToInvalidCardRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that causes cards to be lost if they are attached to an invalid card.
     * @param actionsEnvironment the actions environment
     */
    public AttachedToInvalidCardRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {

                        if (TriggerConditions.isTableChanged(game, effectResult)) {
                            // Check for any cards (devices, Effects, and weapons) that are attached to a card that it
                            // can no longer target or that is not valid for it to be attached to.
                            Collection<PhysicalCard> cardsAttachedToOtherCards = Filters.filterAllOnTable(game, Filters.and(Zone.ATTACHED, Filters.or(Filters.device, Filters.Effect_of_any_Kind, Filters.weapon)));
                            List<PhysicalCard> cardsToLose = new ArrayList<PhysicalCard>();

                            for (PhysicalCard attachedCard : cardsAttachedToOtherCards) {
                                List<String> attachedCardTitles = attachedCard.getTitles();
                                PhysicalCard attachedTo = attachedCard.getAttachedTo();
                                if (attachedTo != null) {
                                    if(!attachedCard.getBlueprint().getValidTargetFilterToRemainAttachedTo(game, attachedCard).accepts(game, attachedTo)) {
                                        cardsToLose.add(attachedCard);
                                    }
                                    else {
                                        for (String attachedCardTitle : attachedCardTitles) {
                                            if (game.getModifiersQuerying().isImmuneToCardTitle(game.getGameState(), attachedTo, attachedCardTitle)) {
                                                cardsToLose.add(attachedCard);
                                                break; //avoid adding same card more than once
                                            }
                                        }
                                    }
                                }
                            }

                            if (!cardsToLose.isEmpty()) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that);
                                action.setText("Make " + GameUtils.getAppendedTextNames(cardsToLose) + " lost");
                                action.setSingletonTrigger(true);
                                action.appendEffect(
                                        new LoseCardsFromTableSimultaneouslyEffect(action, cardsToLose, true, true));
                                return Collections.singletonList((TriggerAction) action);
                            }
                        }
                        return null;
                    }
                }
        );
    }
}

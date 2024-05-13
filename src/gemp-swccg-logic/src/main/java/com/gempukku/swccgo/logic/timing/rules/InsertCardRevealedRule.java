package com.gempukku.swccgo.logic.timing.rules;

import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.ActionsEnvironment;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.RequiredRuleTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.RevealInsertCardEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Enforces the game rule that causes an 'insert' card to be considered revealed when it reaches the top of the card pile.
 */
public class InsertCardRevealedRule implements Rule {
    private ActionsEnvironment _actionsEnvironment;
    private Rule _that;

    /**
     * Creates a rule that enforces the game rule that causes an 'insert' card to be considered revealed when it reaches
     * the top of the card pile.
     * @param actionsEnvironment the actions environment
     */
    public InsertCardRevealedRule(ActionsEnvironment actionsEnvironment) {
        _actionsEnvironment = actionsEnvironment;
        _that = this;
    }

    public void applyRule() {
        _actionsEnvironment.addUntilEndOfGameActionProxy(
                new AbstractActionProxy() {
                    @Override
                    public List<TriggerAction> getRequiredAfterTriggers(SwccgGame game, EffectResult effectResult) {
                        List<TriggerAction> actions = new LinkedList<TriggerAction>();

                        List<PhysicalCard> topCards = game.getGameState().getTopCardsOfPiles();
                        for (PhysicalCard topCard : topCards) {
                            if (topCard.isInserted() && !topCard.isInsertCardRevealed()) {

                                RequiredRuleTriggerAction action = new RequiredRuleTriggerAction(_that, topCard);
                                action.setText("Reveal " + GameUtils.getCardLink(topCard));
                                // Perform result(s)
                                action.appendEffect(
                                        new RevealInsertCardEffect(action, topCard));
                                actions.add(action);
                            }
                        }
                        return actions;
                    }
                }
        );
    }
}

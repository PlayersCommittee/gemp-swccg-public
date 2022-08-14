package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.ReduceForceLossEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseForceResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: It Could Be Worse
 */
public class Card1_090 extends AbstractUsedInterrupt {
    public Card1_090() {
        super(Side.LIGHT, 4, Title.It_Could_Be_Worse);
        setLore("After escaping Detention Block AA-23. Rebels found themselves in the trash compactor. Leia pointed out, 'It could be worse.' It soon was.");
        setGameText("If you must lose Force for any reason, reduce the loss by X amount by using X Force.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isAboutToLoseForce(game, effectResult, playerId)) {
            AboutToLoseForceResult result = (AboutToLoseForceResult) effectResult;
            if (!result.isCannotBeReduced(game)) {
                final int maxForceToUse = GameConditions.forceAvailableToUseToPlayInterrupt(game, playerId, self);
                if (maxForceToUse > 0) {
                    final int defaultForceToUse = Math.min(maxForceToUse, (int) Math.ceil(result.getForceLossAmount(game)));

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Reduce Force loss");
                    // Pay cost(s)
                    action.appendCost(
                            new PlayoutDecisionEffect(action, playerId,
                                    new IntegerAwaitingDecision("Choose amount of Force to use", 1, maxForceToUse, defaultForceToUse) {
                                        @Override
                                        public void decisionMade(final int result) throws DecisionResultInvalidException {
                                            action.appendCost(
                                                    new UseForceEffect(action, playerId, result));
                                            // Allow response(s)
                                            action.allowResponses("Reduce Force loss by " + result,
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new ReduceForceLossEffect(action, playerId, result));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            ));
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
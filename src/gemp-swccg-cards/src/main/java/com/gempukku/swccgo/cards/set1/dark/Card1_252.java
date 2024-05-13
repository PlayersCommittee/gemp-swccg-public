package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.CancelCardBeingPlayedEffect;
import com.gempukku.swccgo.logic.effects.IncreaseForceLossEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayingCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardBeingPlayedForCancelingEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: It's Worse
 */
public class Card1_252 extends AbstractLostInterrupt {
    public Card1_252() {
        super(Side.DARK, 6, Title.Its_Worse, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.C2);
        setLore("Things went from bad to worse when Luke was attacked by a dianoga, a parasitic predator native to the planet Vodran.");
        setGameText("If It Could Be Worse was just played, it is canceled and you may use X Force to raise damage against opponent by X amount. OR If opponent just lost Force from a battle, play to increase loss by 1 Force.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, final SwccgGame game, Effect effect, PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, opponent, Filters.It_Could_Be_Worse)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel " + GameUtils.getFullName(respondableEffect.getCard()));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardBeingPlayedForCancelingEffect(action, respondableEffect) {
                        @Override
                        protected void cardTargetedToBeCanceled(final PhysicalCard targetedCard) {
                            // Pay cost(s)
                            final int maxForceToUse = GameConditions.forceAvailableToUse(game, playerId);
                            if (maxForceToUse > 0) {
                                action.appendCost(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new IntegerAwaitingDecision("Choose amount of Force to use ", 0, maxForceToUse, maxForceToUse) {
                                                    @Override
                                                    public void decisionMade(final int result) throws DecisionResultInvalidException {
                                                        String actionMsgText = "Cancel " + GameUtils.getCardLink(targetedCard);
                                                        if (result > 0) {
                                                            action.appendCost(
                                                                    new UseForceEffect(action, playerId, result));
                                                            actionMsgText += (" and increase Force loss by " + result);
                                                        }
                                                        // Allow response(s)
                                                        action.allowResponses(actionMsgText,
                                                                new RespondablePlayCardEffect(action) {
                                                                    @Override
                                                                    protected void performActionResults(Action targetingAction) {
                                                                        // Perform result(s)
                                                                        action.appendEffect(
                                                                                new CancelCardBeingPlayedEffect(action, respondableEffect));
                                                                        if (result > 0) {
                                                                            action.appendEffect(
                                                                                    new IncreaseForceLossEffect(action, opponent, result));
                                                                        }
                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                        )
                                );
                            }
                            else {
                                // Allow response(s)
                                action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new CancelCardBeingPlayedEffect(action, respondableEffect));
                                            }
                                        }
                                );
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justLostForceFromBattleDamage(game, effectResult, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Increase Force loss by 1");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new IncreaseForceLossEffect(action, opponent, 1));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
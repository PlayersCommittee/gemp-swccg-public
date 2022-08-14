package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.SubtractFromOpponentsTotalPowerAndAttritionEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Main Course
 */
public class Card8_149 extends AbstractUsedOrLostInterrupt {
    public Card8_149() {
        super(Side.DARK, 5, "Main Course", Uniqueness.UNIQUE);
        setLore("Threepio always tried to be polite to Captain Solo and to keep him from getting hot under the collar.");
        setGameText("USED: Cancel Frozen Assets. (Immune to Sense.) LOST: If opponent's alien and Rebel are in battle together without a protocol droid, draw one destiny (or two if Rebel is Han) and subtract that amount from opponent's attrition and total power.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Frozen_Assets)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Frozen_Assets)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Frozen_Assets, Title.Frozen_Assets);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isInitialAttritionJustCalculated(game, effectResult)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(self), Filters.alien))
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(self), Filters.Rebel))
                && !GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(self), Filters.protocol_droid))) {
            final BattleState battleState = game.getGameState().getBattleState();
            if (battleState.hasAttritionTotal(game.getOpponent(playerId))) {

                final PlayInterruptAction action1 = new PlayInterruptAction(game, self, CardSubtype.LOST);
                action1.setText("Draw one destiny to reduce attrition and power");
                // Allow response(s)
                action1.allowResponses("Draw one destiny to reduce opponent's attrition and total power",
                        new RespondablePlayCardEffect(action1) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action1.appendEffect(
                                        new DrawDestinyEffect(action1, playerId, 1) {
                                            @Override
                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                if (totalDestiny != null && totalDestiny > 0) {
                                                    action1.appendEffect(
                                                            new SubtractFromOpponentsTotalPowerAndAttritionEffect(action1, totalDestiny));
                                                }
                                            }
                                        });
                            }
                        }
                );
                actions.add(action1);

                if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.opponents(self), Filters.Rebel, Filters.Han))) {

                    final PlayInterruptAction action2 = new PlayInterruptAction(game, self, CardSubtype.LOST);
                    action2.setText("Draw two destiny to reduce attrition and power");
                    // Allow response(s)
                    action2.allowResponses("Draw two destiny to reduce opponent's attrition and total power",
                            new RespondablePlayCardEffect(action2) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    action2.appendEffect(
                                            new DrawDestinyEffect(action2, playerId, 2) {
                                                @Override
                                                protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                    if (totalDestiny != null && totalDestiny > 0) {
                                                        action2.appendEffect(
                                                                new SubtractFromOpponentsTotalPowerAndAttritionEffect(action2, totalDestiny));
                                                    }
                                                }
                                            });
                                }
                            }
                    );
                    actions.add(action2);
                }
            }
        }
        return actions;
    }
}
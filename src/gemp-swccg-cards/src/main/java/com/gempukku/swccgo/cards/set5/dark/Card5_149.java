package com.gempukku.swccgo.cards.set5.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Projective Telepathy
 */
public class Card5_149 extends AbstractUsedInterrupt {
    public Card5_149() {
        super(Side.DARK, 3, "Projective Telepathy", Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.U);
        setLore("'Luke.' 'Father.' 'Son, come with me.'");
        setGameText("Cancel Anger, Fear, Aggression when it is inserted or revealed. OR If your opponent just initiated a battle or Force drain, opponent must choose to use 2 Force, or cancel that battle or Force drain.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        Filter filter = Filters.Anger_Fear_Aggression;

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, filter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.justRevealedInsertCard(game, effectResult, Filters.Anger_Fear_Aggression)
                && GameConditions.canCancelRevealedInsertCard(game, self, effectResult)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelRevealedInsertCardAction(action, effectResult);
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent use 2 Force or cancel battle");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            if (GameConditions.canUseForce(game, opponent, 2)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Use 2 Force", "Cancel battle"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {
                                                            game.getGameState().sendMessage(opponent + " chooses to use 2 Force");
                                                            action.appendEffect(
                                                                    new UseForceEffect(action, opponent, 2));
                                                        } else {
                                                            game.getGameState().sendMessage(opponent + " chooses to cancel battle");
                                                            action.appendEffect(
                                                                    new CancelBattleEffect(action));
                                                        }
                                                    }
                                                }
                                        )
                                );
                            }
                            else {
                                action.appendEffect(
                                        new CancelBattleEffect(action));
                            }
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent)
                && GameConditions.canCancelForceDrain(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make opponent use 2 Force or cancel drain");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            if (GameConditions.canUseForce(game, opponent, 2)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, opponent,
                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Use 2 Force", "Cancel Force drain"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {
                                                            game.getGameState().sendMessage(opponent + " chooses to use 2 Force");
                                                            action.appendEffect(
                                                                    new UseForceEffect(action, opponent, 2));
                                                        } else {
                                                            game.getGameState().sendMessage(opponent + " chooses to cancel Force drain");
                                                            action.appendEffect(
                                                                    new CancelForceDrainEffect(action));
                                                        }
                                                    }
                                                }
                                        )
                                );
                            }
                            else {
                                action.appendEffect(
                                        new CancelForceDrainEffect(action));
                            }
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }
}
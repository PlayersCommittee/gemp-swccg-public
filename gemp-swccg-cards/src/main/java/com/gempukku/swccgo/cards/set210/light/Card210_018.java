package com.gempukku.swccgo.cards.set210.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelBattleEffect;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virutal Set 10
 * Type: Interrupt
 * Subtype: Used
 * Title: Jedi Mind Trick
 */

public class Card210_018 extends AbstractUsedOrLostInterrupt {
    public Card210_018() {
        super(Side.LIGHT, 3, "Jedi Mind Trick", Uniqueness.UNIQUE);
        setLore("'You will bring Captain Solo and the Wookiee to me.'");
        setGameText("USED: If opponent just initiated a battle or Force drain, they must use 2 Force, or it is canceled. LOST: If your Jedi in battle (unless with a Dark Jedi, Hutt, or Toydarian), 'wave hand at' (add or subtract 2 from) an opponent's just drawn destiny.");
        addIcons(Icon.JABBAS_PALACE, Icon.VIRTUAL_SET_10);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        if (TriggerConditions.forceDrainInitiatedBy(game, effectResult, opponent)
                && GameConditions.canCancelForceDrain(game, self)) {
            final PlayInterruptAction action1 = new PlayInterruptAction(game, self, CardSubtype.USED);

            action1.setText("Make opponent use 2 Force or cancel drain");
            // Allow response(s)
            action1.allowResponses(
                    new RespondablePlayCardEffect(action1) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            if (GameConditions.canUseForce(game, opponent, 2)) {
                                action1.appendEffect(
                                        new PlayoutDecisionEffect(action1, opponent,
                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Use 2 Force", "Cancel Force drain"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {
                                                            game.getGameState().sendMessage(opponent + " chooses to use 2 Force");
                                                            action1.appendEffect(
                                                                    new UseForceEffect(action1, opponent, 2));
                                                        } else {
                                                            game.getGameState().sendMessage(opponent + " chooses to cancel Force drain");
                                                            action1.appendEffect(
                                                                    new CancelForceDrainEffect(action1));
                                                        }
                                                    }
                                                }
                                        )
                                );
                            }
                            else {
                                action1.appendEffect(
                                        new CancelForceDrainEffect(action1));
                            }
                        }
                    }
            );
            actions.add(action1);
        }

        // Calling this one action4, makes it out of order, but means I don't have to change *any* of the add or
        //  subtract code that I had done before this
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)) {
            final PlayInterruptAction action4 = new PlayInterruptAction(game, self, CardSubtype.USED);
            action4.setText("Make opponent use 2 Force or cancel battle");
            // Allow response(s)
            action4.allowResponses(
                    new RespondablePlayCardEffect(action4) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            if (GameConditions.canUseForce(game, opponent, 2)) {
                                action4.appendEffect(
                                        new PlayoutDecisionEffect(action4, opponent,
                                                new MultipleChoiceAwaitingDecision("Choose effect", new String[]{"Use 2 Force", "Cancel battle"}) {
                                                    @Override
                                                    protected void validDecisionMade(int index, String result) {
                                                        if (index == 0) {
                                                            game.getGameState().sendMessage(opponent + " chooses to use 2 Force");
                                                            action4.appendEffect(
                                                                    new UseForceEffect(action4, opponent, 2));
                                                        } else {
                                                            game.getGameState().sendMessage(opponent + " chooses to cancel battle");
                                                            action4.appendEffect(
                                                                    new CancelBattleEffect(action4));
                                                        }
                                                    }
                                                }
                                        )
                                );
                            }
                            else {
                                action4.appendEffect(
                                        new CancelBattleEffect(action4));
                            }
                        }
                    }
            );
            actions.add(action4);
        }

        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Jedi))
                && !(GameConditions.isDuringBattleWithParticipant(game, Filters.or(Filters.Dark_Jedi, Filters.Hutt, Filters.Toydarian))))
        {
            final PlayInterruptAction action2 = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action2.setText("Wave hand: Add 2 to destiny");
            // Allow response(s)
            action2.allowResponses(
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action2.appendEffect(
                                    new ModifyDestinyEffect(action2, 2));
                        }
                    }
            );
            actions.add(action2);

            final PlayInterruptAction action3 = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action3.setText("Wave hand: Subtract 2 from destiny");
            // Allow response(s)
            action3.allowResponses(
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action3.appendEffect(
                                    new ModifyDestinyEffect(action3, -2));
                        }
                    }
            );
            actions.add(action3);

        }
        return actions;
    }

}
package com.gempukku.swccgo.cards.set207.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.SubAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Special Edition
 * Type: Interrupt
 * Subtype: Used
 * Title: They're Tracking Us (V)
 */
public class Card207_014 extends AbstractUsedInterrupt {
    public Card207_014() {
        super(Side.LIGHT, 4, "They're Tracking Us", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        setLore("'Not this ship, sister.'");
        setGameText("Cancel Program Trap; droid it was deployed on may be taken into owner’s hand. [Immune to Sense.] OR Add or subtract 1 from opponent’s just drawn destiny. OR Activate 1 Force.");
        addIcons(Icon.SPECIAL_EDITION, Icon.VIRTUAL_SET_7);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Program_Trap)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.setImmuneTo(Title.Sense);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        Filter targetFilter = Filters.Program_Trap;

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel Program Trap");
            action.setImmuneTo(Title.Sense);
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose card to cancel", TargetingReason.TO_BE_CANCELED, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);

                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(cardTargeted),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard cardToCancel = targetingAction.getPrimaryTargetCard(targetGroupId);
                                            final PhysicalCard droid = cardToCancel.getAttachedTo() != null && Filters.droid.accepts(game, cardToCancel.getAttachedTo()) ? cardToCancel.getAttachedTo() : null;

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelCardOnTableEffect(action, cardToCancel));
                                            if (droid != null) {
                                                final String droidOwner = droid.getOwner();
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, droidOwner,
                                                                new YesNoDecision("Do you want to take " + GameUtils.getCardLink(droid) + " into hand?") {
                                                                    @Override
                                                                    protected void yes() {
                                                                        game.getGameState().sendMessage(droidOwner + " chooses to take " + GameUtils.getCardLink(droid) + " into hand");
                                                                        SubAction subAction = new SubAction(action, droidOwner);
                                                                        subAction.appendEffect(
                                                                                new ReturnCardToHandFromTableEffect(subAction, droid));
                                                                        action.appendEffect(
                                                                                new StackActionEffect(action, subAction));
                                                                    }
                                                                    @Override
                                                                    protected void no() {
                                                                        action.setActionMsg(null);
                                                                        game.getGameState().sendMessage(droidOwner + " chooses to not take " + GameUtils.getCardLink(droid) + " into hand");
                                                                    }
                                                                }
                                                        ));
                                            }
                                        }
                                    });
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canActivateForce(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Activate 1 Force");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ActivateForceEffect(action, playerId, 1));
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }


    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.isDestinyJustDrawnBy(game, effectResult, opponent)) {

            final PlayInterruptAction action1 = new PlayInterruptAction(game, self);
            action1.setText("Add 1 to destiny");
            // Allow response(s)
            action1.allowResponses(
                    new RespondablePlayCardEffect(action1) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action1.appendEffect(
                                    new ModifyDestinyEffect(action1, 1));
                        }
                    }
            );
            actions.add(action1);

            final PlayInterruptAction action2 = new PlayInterruptAction(game, self);
            action2.setText("Subtract 1 from destiny");
            // Allow response(s)
            action2.allowResponses(
                    new RespondablePlayCardEffect(action2) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action2.appendEffect(
                                    new ModifyDestinyEffect(action2, -1));
                        }
                    }
            );
            actions.add(action2);

        }
        return actions;
    }
}
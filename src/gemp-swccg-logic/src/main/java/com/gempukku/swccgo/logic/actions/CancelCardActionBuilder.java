package com.gempukku.swccgo.logic.actions;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.InactiveReason;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.InsertCardRevealedResult;

import java.util.Map;

/**
 * A utility class for building common "cancel" actions.
 */
public class CancelCardActionBuilder {

    /**
     * Builds a game text action that targets and cancels a card in play.
     * @param action the game text action to build
     * @param cardToCancelFilter the card to cancel filter
     * @param cardToCancelText the text to show as card to cancel
     */
     public static void buildCancelCardAction(final AbstractGameTextAction action, Filterable cardToCancelFilter, String cardToCancelText) {
         buildCancelCardAction(action, null, cardToCancelFilter, cardToCancelText, 0);
     }

    /**
     * Builds a game text action that targets and cancels a card in play.
     * @param action the game text action to build
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param cardToCancelFilter the card to cancel filter
     * @param cardToCancelText the text to show as card to cancel
     */
    public static void buildCancelCardAction(final AbstractGameTextAction action, Map<InactiveReason, Boolean> spotOverrides, Filterable cardToCancelFilter, String cardToCancelText) {
        buildCancelCardAction(action, spotOverrides, cardToCancelFilter, cardToCancelText, 0);
    }

    /**
     * Builds a game text action that targets and cancels a card in play, when action cost includes using Force.
     * @param action the game text action to build
     * @param cardToCancelFilter the card to cancel filter
     * @param cardToCancelText the text to show as card to cancel
     * @param forceToUse the amount of Force to use
     */
    public static void buildCancelCardAction(final AbstractGameTextAction action, Filterable cardToCancelFilter, String cardToCancelText, final int forceToUse) {
        buildCancelCardAction(action, null, cardToCancelFilter, cardToCancelText, forceToUse);
    }

    /**
     * Builds a game text action that targets and cancels a card in play, when action cost includes using Force.
     * @param action the game text action to build
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param cardToCancelFilter the card to cancel filter
     * @param cardToCancelText the text to show as card to cancel
     * @param forceToUse the amount of Force to use
     */
    public static void buildCancelCardAction(final AbstractGameTextAction action, Map<InactiveReason, Boolean> spotOverrides, Filterable cardToCancelFilter, String cardToCancelText, final int forceToUse) {
         action.setSingletonTrigger(true);
         action.setText("Cancel " + cardToCancelText);

         // Choose target(s)
         action.appendTargeting(
                 new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Target card to cancel", spotOverrides, TargetingReason.TO_BE_CANCELED, cardToCancelFilter) {
                     @Override
                     protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                         action.addAnimationGroup(cardTargeted);
                         // Pay cost(s)
                         if (forceToUse > 0) {
                             action.appendCost(
                                     new UseForceEffect(action, action.getPerformingPlayer(), forceToUse));
                         }
                         // Allow response(s)
                         action.allowResponses("Cancel " + GameUtils.getCardLink(cardTargeted),
                                 new RespondableEffect(action) {
                                     @Override
                                     protected void performActionResults(Action targetingAction) {
                                         // Get the targeted card(s) from the action using the targetGroupId.
                                         // This needs to be done in case the target(s) were changed during the responses.
                                         PhysicalCard cardToCancel = targetingAction.getPrimaryTargetCard(targetGroupId);

                                         // Perform result(s)
                                         action.appendEffect(
                                                 new CancelCardOnTableEffect(action, cardToCancel));
                                     }
                                 });
                     }
                 }
         );
    }

    /**
     * Builds a play interrupt action that targets and cancels a card in play.
     * @param action the play interrupt action to build
     * @param cardToCancelFilter the card to cancel filter
     * @param cardToCancelText the text to show as card to cancel
     */
    public static void buildCancelCardAction(final PlayInterruptAction action, Filterable cardToCancelFilter, String cardToCancelText) {
        buildCancelCardAction(action, cardToCancelFilter, cardToCancelText, 0);
    }

    /**
     * Builds a play interrupt action that targets and cancels a card in play.
     * @param action the play interrupt action to build
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param cardToCancelFilter the card to cancel filter
     * @param cardToCancelText the text to show as card to cancel
     */
    public static void buildCancelCardAction(final PlayInterruptAction action, Map<InactiveReason, Boolean> spotOverrides, Filterable cardToCancelFilter, String cardToCancelText) {
        buildCancelCardAction(action, spotOverrides, cardToCancelFilter, cardToCancelText, 0);
    }

    /**
     * Builds a play interrupt that targets and cancels a card in play, when action cost includes using Force.
     * @param action the play interrupt action to build
     * @param cardToCancelFilter the card to cancel filter
     * @param cardToCancelText the text to show as card to cancel
     * @param forceToUse the amount of Force to use
     */
    public static void buildCancelCardAction(final PlayInterruptAction action, Filterable cardToCancelFilter, String cardToCancelText, final int forceToUse) {
        buildCancelCardAction(action, null, cardToCancelFilter, cardToCancelText, forceToUse);
    }

    /**
     * Builds a play interrupt that targets and cancels a card in play, when action cost includes using Force.
     * @param action the play interrupt action to build
     * @param spotOverrides overrides which cards can be seen as "active" for the purposes of this query, or null
     * @param cardToCancelFilter the card to cancel filter
     * @param cardToCancelText the text to show as card to cancel
     * @param forceToUse the amount of Force to use
     */
    public static void buildCancelCardAction(final PlayInterruptAction action, Map<InactiveReason, Boolean> spotOverrides, Filterable cardToCancelFilter, String cardToCancelText, final int forceToUse) {
        action.setText("Cancel " + cardToCancelText);
        // Choose target(s)
        action.appendTargeting(
                new TargetCardOnTableEffect(action, action.getPerformingPlayer(), "Choose card to cancel", spotOverrides, TargetingReason.TO_BE_CANCELED, cardToCancelFilter) {
                    @Override
                    protected void cardTargeted(final int targetGroupId1, PhysicalCard cardTargeted) {
                        action.addAnimationGroup(cardTargeted);
                        // Pay cost(s)
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, action.getPerformingPlayer(), forceToUse));
                        }

                        // Allow response(s)
                        action.allowResponses("Cancel " + GameUtils.getCardLink(cardTargeted),
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Get the final targeted card(s)
                                        PhysicalCard cardToCancel = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                        // Perform result(s)
                                        action.appendEffect(
                                                new CancelCardOnTableEffect(action, cardToCancel));
                                    }
                                });
                    }
                }
        );
    }

    /*
    *  Builds an action that targets and cancels a card being played.
    */

    /**
     * Builds a game text action that targets and cancels a card being played.
     * @param action the game text action to build
     * @param effect the playing card effect
     */
    public static void buildCancelCardBeingPlayedAction(final AbstractGameTextAction action, final Effect effect) {
        buildCancelCardBeingPlayedAction(action, effect, 0);
    }

    /**
     * Builds a game text action that targets and cancels a card being played, when action cost includes using Force.
     * @param action the game text action to build
     * @param effect the playing card effect
     * @param forceToUse the amount of Force to use
     */
    public static void buildCancelCardBeingPlayedAction(final AbstractGameTextAction action, final Effect effect, final int forceToUse) {
        final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

        String ownerText = (((RespondablePlayingCardEffect) effect).getCard().getOwner().equals(action.getPerformingPlayer()) ? "your " : "");
        action.setText("Cancel " + ownerText + GameUtils.getFullName(respondableEffect.getCard()));
        // Choose target(s)
        action.appendTargeting(
                new TargetCardBeingPlayedForCancelingEffect(action, respondableEffect) {
                    @Override
                    protected void cardTargetedToBeCanceled(final PhysicalCard targetedCard) {
                        // Pay cost(s)
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, action.getPerformingPlayer(), forceToUse));
                        }
                        // Allow response(s)
                        action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard),
                                new RespondableEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        action.appendEffect(
                                                new CancelCardBeingPlayedEffect(action, respondableEffect));
                                    }
                                });
                    }
                }
        );
    }

    /*
    *  Builds an action that targets and cancels a card being played.
    */

    /**
     * Builds a play interrupt action that targets and cancels a card being played.
     * @param action the play interrupt action to build
     * @param effect the playing card effect
     */
    public static void buildCancelCardBeingPlayedAction(final PlayInterruptAction action, final Effect effect) {
        buildCancelCardBeingPlayedAction(action, effect, 0);
    }

    /**
     * Builds a play interrupt action that targets and cancels a card being played, when action cost includes using Force.
     * @param action the play interrupt action to build
     * @param effect the playing card effect
     * @param forceToUse the amount of Force to use
     */
    public static void buildCancelCardBeingPlayedAction(final PlayInterruptAction action, final Effect effect, final int forceToUse) {
        final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;

        String ownerText = (((RespondablePlayingCardEffect) effect).getCard().getOwner().equals(action.getPerformingPlayer()) ? "your " : "");
        action.setText("Cancel " + ownerText + GameUtils.getFullName(respondableEffect.getCard()));
        // Choose target(s)
        action.appendTargeting(
                new TargetCardBeingPlayedForCancelingEffect(action, respondableEffect) {
                    @Override
                    protected void cardTargetedToBeCanceled(final PhysicalCard targetedCard) {
                        // Pay cost(s)
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, action.getPerformingPlayer(), forceToUse));
                        }
                        // Allow response(s)
                        action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard),
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        action.appendEffect(
                                                new CancelCardBeingPlayedEffect(action, respondableEffect));
                                    }
                                });
                    }
                }
        );
    }

    /*
    *  Builds an action that targets and cancels a revealed 'insert' card.
    */

    /**
     * Builds a game text action that targets and cancels a revealed 'insert' card.
     * @param action the game text action to build
     * @param effectResult the 'insert' card revealed effect result
     */
    public static void buildCancelRevealedInsertCardAction(final AbstractGameTextAction action, final EffectResult effectResult) {
        buildCancelRevealedInsertCardAction(action, effectResult, 0);
    }

    /**
     * Builds a game text action that targets and cancels a revealed 'insert' card.
     * @param action the game text action to build
     * @param effectResult the 'insert' card revealed effect result
     * @param forceToUse the amount of Force to use
     */
    public static void buildCancelRevealedInsertCardAction(final AbstractGameTextAction action, final EffectResult effectResult, final int forceToUse) {
        final InsertCardRevealedResult insertCardRevealedResult = (InsertCardRevealedResult) effectResult;

        action.setText("Cancel " + GameUtils.getFullName(insertCardRevealedResult.getCard()));
        // Choose target(s)
        action.appendTargeting(
                new TargetRevealedInsertCardForCancelingEffect(action, insertCardRevealedResult) {
                    @Override
                    protected void cardTargetedToBeCanceled(final PhysicalCard targetedCard) {
                        // Pay cost(s)
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, action.getPerformingPlayer(), forceToUse));
                        }
                        // Allow response(s)
                        action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard),
                                new RespondableEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        action.appendEffect(
                                                new CancelRevealedInsertCardEffect(action, insertCardRevealedResult));
                                    }
                                });
                    }
                }
        );
    }

    /**
     * Builds a game text action that targets and cancels a revealed 'insert' card.
     * @param action the game text action to build
     * @param effectResult the 'insert' card revealed effect result
     */
    public static void buildCancelRevealedInsertCardAction(final PlayInterruptAction action, final EffectResult effectResult) {
        buildCancelRevealedInsertCardAction(action, effectResult, 0);
    }

    /**
     * Builds a game text action that targets and cancels a revealed 'insert' card.
     * @param action the game text action to build
     * @param effectResult the 'insert' card revealed effect result
     * @param forceToUse the amount of Force to use
     */
    public static void buildCancelRevealedInsertCardAction(final PlayInterruptAction action, final EffectResult effectResult, final int forceToUse) {
        final InsertCardRevealedResult insertCardRevealedResult = (InsertCardRevealedResult) effectResult;

        action.setText("Cancel " + GameUtils.getFullName(insertCardRevealedResult.getCard()));
        // Choose target(s)
        action.appendTargeting(
                new TargetRevealedInsertCardForCancelingEffect(action, insertCardRevealedResult) {
                    @Override
                    protected void cardTargetedToBeCanceled(final PhysicalCard targetedCard) {
                        // Pay cost(s)
                        if (forceToUse > 0) {
                            action.appendCost(
                                    new UseForceEffect(action, action.getPerformingPlayer(), forceToUse));
                        }
                        // Allow response(s)
                        action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard),
                                new RespondablePlayCardEffect(action) {
                                    @Override
                                    protected void performActionResults(Action targetingAction) {
                                        // Perform result(s)
                                        action.appendEffect(
                                                new CancelRevealedInsertCardEffect(action, insertCardRevealedResult));
                                    }
                                });
                    }
                }
        );
    }
}

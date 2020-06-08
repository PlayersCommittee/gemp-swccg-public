package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.complete.ChooseExistingCardPileEffect;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Scomp Link Access
 */
public class Card1_108 extends AbstractUsedInterrupt {
    public Card1_108() {
        super(Side.LIGHT, 3, Title.Scomp_Link_Access);
        setLore("A computer connection access port used mainly by droids to plug into database networks and locate information, evaluate threats, execute diagnostics or perform maintenance.");
        setGameText("If one of your 'R' unit droids is at any Scomp link, you may: glance at the cards in any Reserve Deck for 20 seconds. Shuffle, cut and replace. OR If also at a Death Star site, cancel We're All Gonna Be A Lot Thinner or Boring Conversation Anyway.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);
        Filter droidFilter = Filters.and(Filters.your(self), Filters.R_unit, Filters.at_Scomp_Link);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, droidFilter)
                && (GameConditions.hasReserveDeck(game, playerId) || GameConditions.hasReserveDeck(game, opponent))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Look at Reserve Deck");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose 'R' unit droid", droidFilter) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard droidTargeted) {
                            action.addAnimationGroup(droidTargeted);
                            action.appendTargeting(
                                    new ChooseExistingCardPileEffect(action, playerId, Zone.RESERVE_DECK) {
                                        @Override
                                        protected void pileChosen(SwccgGame game, final String cardPileOwner, Zone cardPile) {
                                            // Allow response(s)
                                            action.allowResponses("Look at " + cardPileOwner + "'s " + cardPile.getHumanReadable() + " by targeting " + GameUtils.getCardLink(droidTargeted),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new LookAtReserveDeckEffect(action, playerId, cardPileOwner));
                                                            action.appendEffect(
                                                                    new ShuffleReserveDeckEffect(action, cardPileOwner));
                                                        }
                                                    }
                                            );
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }

        Filter droidAtDeathStarSiteFilter = Filters.and(Filters.your(self), Filters.R_unit, Filters.at_Scomp_Link, Filters.at(Filters.Death_Star_site));
        final Filter cardToCancelFilter = Filters.or(Filters.Were_All_Gonna_Be_A_Lot_Thinner, Filters.Boring_Conversation_Anyway);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, droidAtDeathStarSiteFilter)
                && GameConditions.canTargetToCancel(game, self, cardToCancelFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel We're All Gonna Be A Lot Thinner or Boring Conversation Anyway");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose 'R' unit droid", droidAtDeathStarSiteFilter) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard droidTargeted) {
                            action.addAnimationGroup(droidTargeted);
                            // Choose target(s)
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose card to cancel", TargetingReason.TO_BE_CANCELED, cardToCancelFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard cardToCancelTargeted) {
                                            action.addAnimationGroup(cardToCancelTargeted);
                                            // Allow response(s)
                                            action.allowResponses("Cancel " + GameUtils.getCardLink(cardToCancelTargeted) + " by targeting " + GameUtils.getCardLink(droidTargeted),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the final targeted card(s)
                                                            PhysicalCard finalCardToCancel = targetingAction.getPrimaryTargetCard(targetGroupId2);
                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new CancelCardOnTableEffect(action, finalCardToCancel));
                                                        }
                                                    });
                                        }
                                    }
                            );
                        }
                    }
            );
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(final String playerId, SwccgGame game, final Effect effect, final PhysicalCard self) {
        Filter droidAtDeathStarSiteFilter = Filters.and(Filters.your(self), Filters.R_unit, Filters.at_Scomp_Link, Filters.at(Filters.Death_Star_site));
        Filter cardToCancelFilter = Filters.or(Filters.Were_All_Gonna_Be_A_Lot_Thinner, Filters.Boring_Conversation_Anyway);

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, cardToCancelFilter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)
                && GameConditions.canTarget(game, self, droidAtDeathStarSiteFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            final RespondablePlayingCardEffect respondableEffect = (RespondablePlayingCardEffect) effect;
            String ownerText = (((RespondablePlayingCardEffect) effect).getCard().getOwner().equals(action.getPerformingPlayer()) ? "your " : "");
            action.setText("Cancel " + ownerText + GameUtils.getFullName(respondableEffect.getCard()));
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose 'R' unit droid", droidAtDeathStarSiteFilter) {
                        @Override
                        protected boolean getUseShortcut() {
                            return true;
                        }
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard droidTargeted) {
                            action.addAnimationGroup(droidTargeted);
                            // Choose target(s)
                            action.appendTargeting(
                                    new TargetCardBeingPlayedForCancelingEffect(action, respondableEffect) {
                                        @Override
                                        protected void cardTargetedToBeCanceled(final PhysicalCard cardToCancelTargeted) {
                                            // Allow response(s)
                                            action.allowResponses("Cancel " + GameUtils.getCardLink(cardToCancelTargeted) + " by targeting " + GameUtils.getCardLink(droidTargeted),
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
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
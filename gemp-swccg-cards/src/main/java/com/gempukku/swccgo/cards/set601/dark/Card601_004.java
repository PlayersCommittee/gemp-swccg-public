package com.gempukku.swccgo.cards.set601.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SuspendCardUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.PlayStackedDefensiveShieldEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Block 4
 * Type: Interrupt
 * Subtype: Used
 * Title: Cold Feet (V)
 */
public class Card601_004 extends AbstractUsedInterrupt {
    public Card601_004() {
        super(Side.DARK, 5, "Cold Feet");
        setVirtualSuffix(true);
        setLore("Wampas pack snow around the appendages of captured prey, making use of Hoth's cold environment to immobilize them.");
        setGameText("Take a 'grabber' into hand from Reserve Deck; reshuffle. (Immune to Sense.)  OR  Play a Defensive Shield from under your Starting Effect.  OR  Cancel Don't Forget The Droids, It Can Wait, or Surprise Assault.  OR  Suspend Bacta Tank ('patient' remains on Effect), Bo Shuda, Goo Nee Tay, or Mantellian Savrip for remainder of turn.");
        addIcons(Icon.HOTH, Icon.LEGACY_BLOCK_4);
        setAsLegacy(true);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__COLD_FEET__UPLOAD_GRABBER;


        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setImmuneTo(Title.Sense);
            action.setText("Take a grabber into hand from Reserve Deck");

            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.grabber, true));
                        }
                    });

            actions.add(action);
        }

        // Check condition(s)
        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (startingEffect != null) {
            Filter filter = Filters.and(Filters.Defensive_Shield, Filters.playable(self));
            if (GameConditions.hasStackedCards(game, startingEffect, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Play a Defensive Shield");
                // Choose target(s)
                action.appendTargeting(
                        new ChooseStackedCardEffect(action, playerId, startingEffect, filter) {
                            @Override
                            protected void cardSelected(final PhysicalCard selectedCard) {
                                // Allow response(s)
                                action.allowResponses(
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new PlayStackedDefensiveShieldEffect(action, self, selectedCard));
                                            }
                                        });
                            }
                        });
                actions.add(action);
            }
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.title("Don't Forget The Droids"))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title("Don't Forget The Droids"), "Don't Forget The Droids");
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.title("It Can Wait"))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.title("It Can Wait"), "It Can Wait");
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Surprise_Assault)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Surprise_Assault, Title.Surprise_Assault);
            actions.add(action);
        }

        if (GameConditions.canSpot(game, self, Filters.or(Filters.Bacta_Tank, Filters.Bo_Shuda, Filters.Goo_Nee_Tay, Filters.Mantellian_Savrip))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Suspend card");
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target card to suspend", Filters.or(Filters.Bacta_Tank, Filters.Bo_Shuda, Filters.Goo_Nee_Tay, Filters.Mantellian_Savrip)) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(new RespondablePlayCardEffect(action) {
                                              @Override
                                              protected void performActionResults(Action targetingAction) {
                                                  PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);
                                                  action.appendEffect(new SuspendCardUntilEndOfTurnEffect(action, finalTarget));
                                              }
                                          }
                    );
                }
            });
            actions.add(action);
        }


        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.title("Don't Forget The Droids"), Filters.title("It Can Wait"), Filters.Surprise_Assault))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
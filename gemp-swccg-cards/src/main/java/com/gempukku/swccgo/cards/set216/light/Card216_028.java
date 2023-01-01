package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.SpotOverride;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.CancelReactEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.SuspendCardUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 16
 * Type: Interrupt
 * Subtype: Used
 * Title: Free Ride & Endor Celebration (V)
 */
public class Card216_028 extends AbstractUsedInterrupt {
    public Card216_028() {
        super(Side.LIGHT, 5, "Free Ride & Endor Celebration", Uniqueness.UNIQUE, ExpansionSet.SET_16, Rarity.V);
        setVirtualSuffix(true);
        addComboCardTitles(Title.Free_Ride, Title.Endor_Celebration);
        setGameText("Cancel Cloud City Occupation, Rebel Base Occupation, or Tatooine Occupation. [Immune to Sense.] OR Cancel Force Lightning (unless targeting an Undercover spy). OR Cancel an attempt to deploy or move a combat vehicle as a 'react.' OR During your turn, target opponent's spy (or unpiloted combat vehicle) at a site you control; target is lost. (Immune to Oh, Switch Off.) OR For remainder of turn, suspend the following cards on table (if any): A Million Voices Crying Out and An Entire Legion Of My Best Troops.");
        addIcons(Icon.CORUSCANT, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Tatooine_Occupation)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Tatooine_Occupation, Title.Tatooine_Occupation);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Cloud_City_Occupation)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Cloud_City_Occupation, Title.Cloud_City_Occupation);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Rebel_Base_Occupation)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Rebel_Base_Occupation, Title.Rebel_Base_Occupation);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.Force_Lightning)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Force_Lightning, Title.Force_Lightning);
            actions.add(action);
        }


        final PlayInterruptAction suspendCardsAction = new PlayInterruptAction(game, self);
        suspendCardsAction.setText("Suspend cards for remainder of turn");

        // Allow response(s)
        suspendCardsAction.allowResponses("Suspend A Million Voices Crying Out and An Entire Legion Of My Best Troops for remainder of turn",
                new RespondablePlayCardEffect(suspendCardsAction) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        PhysicalCard AMVCO = Filters.findFirstActive(game, self, Filters.title(Title.A_Million_Voices_Crying_Out));
                        PhysicalCard Legion = Filters.findFirstActive(game, self, Filters.title(Title.An_Entire_Legion_Of_My_Best_Troops));

                        // if these are on table need to do more than the modifier otherwise there is a delay in suspending the card
                        if (AMVCO != null) {
                            suspendCardsAction.appendEffect(new SuspendCardUntilEndOfTurnEffect(suspendCardsAction, AMVCO));
                        }
                        if (Legion != null) {
                            suspendCardsAction.appendEffect(new SuspendCardUntilEndOfTurnEffect(suspendCardsAction, Legion));
                        }

                        suspendCardsAction.appendEffect(
                                new AddUntilEndOfTurnModifierEffect(suspendCardsAction,
                                        new SuspendsCardModifier(self, Filters.or(Filters.title(Title.A_Million_Voices_Crying_Out), Filters.title(Title.An_Entire_Legion_Of_My_Best_Troops))),
                                        "Suspends A Million Voices Crying Out and An Entire Legion Of My Best Troops")
                        );
                    }
                }
        );
        actions.add(suspendCardsAction);

        // Check condition(s)
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter filter = Filters.and(Filters.at(Filters.and(Filters.controls(playerId), Filters.site)), Filters.opponents(playerId),
                Filters.or(Filters.spy, Filters.and(Filters.unpiloted, Filters.combat_vehicle)),
                Filters.canBeTargetedBy(self, targetingReason));

        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make spy or combat vehicle lost");
            action.setImmuneTo(Title.Oh_Switch_Off);

            // Allow response(s)
            action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Target card to be lost", SpotOverride.INCLUDE_UNDERCOVER, targetingReason, filter) {
                @Override
                protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                    action.allowResponses(null,
                            new RespondablePlayCardEffect(action) {
                                @Override
                                protected void performActionResults(Action targetingAction) {
                                    // Perform result(s)
                                    PhysicalCard finalCard = action.getPrimaryTargetCard(targetGroupId);
                                    action.appendEffect(new LoseCardFromTableEffect(action, finalCard));
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
        List<PlayInterruptAction> actions = new LinkedList<>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Tatooine_Occupation, Filters.Cloud_City_Occupation, Filters.Rebel_Base_Occupation))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }

        if (TriggerConditions.isPlayingCard(game, effect, Filters.Force_Lightning)
                && !TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Force_Lightning, Filters.undercover_spy)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }


        if (TriggerConditions.isReact(game, effect)
                && (TriggerConditions.isMovingAsReact(game, effect, Filters.combat_vehicle)
                || TriggerConditions.isDeployingAsReact(game, effect, Filters.combat_vehicle))) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel 'react'");
            // Allow responses
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform results
                            action.appendEffect(new CancelReactEffect(action));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
package com.gempukku.swccgo.cards.set216.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelReactEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
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
        super(Side.LIGHT, 5, "Free Ride & Endor Celebration", Uniqueness.UNIQUE);
        setVirtualSuffix(true);
        addComboCardTitles(Title.Free_Ride, Title.Endor_Celebration);
        setGameText("Cancel Cloud City Occupation, Rebel Base Occupation, or Tatooine Occupation. [Immune to Sense.] OR During your turn, target opponent's spy (or unpiloted combat vehicle) at a site you control; target is lost. OR Cancel an attempt to deploy or move a combat vehicle as a 'react.' OR Cancel a 'react' to or from same site as Leia or Luke.");
        addIcons(Icon.CORUSCANT, Icon.VIRTUAL_SET_16);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self) {
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
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter filter = Filters.and(Filters.at(Filters.controls(playerId)), Filters.opponents(playerId),
                Filters.or(Filters.spy, Filters.and(Filters.unpiloted, Filters.combat_vehicle)), Filters.canBeTargetedBy(self, targetingReason));

        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.canSpot(game, self, SpotOverride.INCLUDE_UNDERCOVER, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Make spy or unpiloted combat vehicle lost");
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

        if (TriggerConditions.isReact(game, effect)
                && (TriggerConditions.isMovingAsReact(game, effect, Filters.combat_vehicle)
                || (GameConditions.isDuringBattleAt(game, Filters.site) && GameConditions.isDuringBattleWithParticipant(game, Filters.or(Filters.Luke, Filters.Leia)))
                || TriggerConditions.isMovingAsReact(game, effect, Filters.at(Filters.sameSiteAs(self, Filters.or(Filters.Luke, Filters.Leia)))))) {
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
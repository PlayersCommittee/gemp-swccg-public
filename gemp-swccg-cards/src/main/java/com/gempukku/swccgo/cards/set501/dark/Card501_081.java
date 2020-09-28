package com.gempukku.swccgo.cards.set501.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfTurnEffect;
import com.gempukku.swccgo.logic.effects.PutStackedCardsInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Set 13
 * Type: Interrupt
 * Subtype: Used
 * Title: I've Been Searching For You For Some Time
 */
public class Card501_081 extends AbstractUsedInterrupt {
    public Card501_081() {
        super(Side.DARK, 4, "I've Been Searching For You For Some Time");
        setLore("");
        setGameText("If opponent is about to lose a character, place any hatred cards on that character in owner's Used Pile." +
                "OR Cancel Arcona's or a spy's game text for remainder of turn. OR Cancel Rebel Barrier or Blast The Door, Kid! at same site as an Inquisitor.");
        addIcons(Icon.VIRTUAL_SET_13);
        setTestingText("I've Been Searching For You For Some Time");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        Filter characterAtSameSiteAsInquisitor = Filters.and(Filters.character, Filters.at(Filters.sameSiteAs(self, Filters.inquisitor)));
        Filter inquisitorOrCharacterWithInquisitor = Filters.or(Filters.inquisitor, characterAtSameSiteAsInquisitor);

        // Check condition(s)
        if (TriggerConditions.isPlayingCardTargeting(game, effect, Filters.Rebel_Barrier, inquisitorOrCharacterWithInquisitor)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Blast_The_Door_Kid)
                && GameConditions.isDuringBattleWithParticipant(game, Filters.inquisitor)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        //Check Condition
        if (TriggerConditions.isAboutToBeLost(game, effectResult, Filters.and(Filters.opponents(playerId), Filters.character))
                || TriggerConditions.isAboutToBeForfeitedToLostPile(game, effectResult, Filters.and(Filters.opponents(playerId), Filters.character))) {
            final AboutToLeaveTableResult aboutToLeaveTableResult = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard cardToBeLost = aboutToLeaveTableResult.getCardAboutToLeaveTable();
            final Collection<PhysicalCard> hatredCards = Filters.filterStacked(game, Filters.and(Filters.hatredCard, Filters.stackedOn(cardToBeLost)));
            if (!hatredCards.isEmpty()) {
                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Place any hatred cards in Used Pile");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new PutStackedCardsInUsedPileEffect(action, playerId, 1, hatredCards.size(), false, cardToBeLost)
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }

        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();

        Filter filter = Filters.or(Filters.title("Arcona"), Filters.spy);

        // Check condition(s)
        if (GameConditions.canTarget(game, self, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Cancel character's game text");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose character", filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfTurnEffect(action, finalTarget));
                                        }
                                    }
                            );
                        }
                    });
            actions.add(action);
        }
        return actions;
    }
}

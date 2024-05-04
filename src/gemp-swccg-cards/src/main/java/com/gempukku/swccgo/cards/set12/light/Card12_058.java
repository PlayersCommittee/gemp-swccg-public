package com.gempukku.swccgo.cards.set12.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealRandomCardInOpponentsHandEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromOpponentsHand;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used
 * Title: Free Ride & Endor Celebration
 */
public class Card12_058 extends AbstractUsedInterrupt {
    public Card12_058() {
        super(Side.LIGHT, 5, "Free Ride & Endor Celebration", Uniqueness.UNIQUE, ExpansionSet.CORUSCANT, Rarity.R);
        addComboCardTitles(Title.Free_Ride, Title.Endor_Celebration);
        setGameText("Cancel Tatooine Occupation, Cloud City Occupation, Rebel Base Occupation. (Immune to Sense.) OR Peak at one card randomly selected from opponent's hand. If that card is a speeder bike, AT-ST, or skiff, steal it into hand.");
        addIcons(Icon.CORUSCANT);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

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
        if (GameConditions.hasHand(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Reveal random card in opponent's hand");
            // Allow response(s)
            action.allowResponses("Reveal a random card in opponent's hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RevealRandomCardInOpponentsHandEffect(action, playerId) {
                                        @Override
                                        protected void cardRevealed(PhysicalCard revealedCard) {
                                            if (Filters.or(Filters.speeder_bike, Filters.AT_ST, Filters.skiff).accepts(game, revealedCard)) {
                                                action.appendEffect(
                                                        new StealCardIntoHandFromOpponentsHand(action, revealedCard));
                                            }
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
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.or(Filters.Tatooine_Occupation, Filters.Cloud_City_Occupation, Filters.Rebel_Base_Occupation))
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            action.setImmuneTo(Title.Sense);
            actions.add(action);
        }
        return actions;
    }
}
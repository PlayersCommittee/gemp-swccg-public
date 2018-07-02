package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealRandomCardInOpponentsHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StealCardIntoHandFromOpponentsHand;
import com.gempukku.swccgo.logic.effects.choose.StealCardToLocationEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Free Ride
 */
public class Card8_052 extends AbstractUsedInterrupt {
    public Card8_052() {
        super(Side.LIGHT, 5, Title.Free_Ride, Uniqueness.UNIQUE);
        setLore("To prevent biker scouts from reaching their base, Luke and Leia 'borrowed' some nearby transportation.");
        setGameText("If you control a location where opponent has a speeder bike, AT-ST or skiff, steal that vehicle (any characters aboard are lost). OR Peek at one card randomly selected from opponent's hand. If that card is a speeder bike, AT-ST or skiff, steal it into hand.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        String opponent = game.getOpponent(playerId);

        Filter targetFilter = Filters.and(Filters.or(Filters.speeder_bike, Filters.AT_ST, Filters.skiff), Filters.at(Filters.controls(playerId)));
        TargetingReason targetingReason = TargetingReason.TO_BE_STOLEN_EVEN_IF_CARDS_ABOARD;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Steal vehicle");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose vehicle to steal", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                            // Allow response(s)
                            action.allowResponses("Steal " + GameUtils.getCardLink(cardTargeted),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            PhysicalCard cardToSteal = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new StealCardToLocationEffect(action, cardToSteal));
                                        }
                                    }
                            );
                        }
                    }
            );
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
}
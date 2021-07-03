package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.MoveAwayAsReactEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Used
 * Title: Lift Tube Escape
 */
public class Card5_057 extends AbstractUsedInterrupt {
    public Card5_057() {
        super(Side.LIGHT, 5, "Lift Tube Escape", Uniqueness.UNIQUE);
        setLore("First floor: parasols and powdered blue milk. Second floor: bantha skin rugs, bog-wing drapes and juri juice tables. Third floor: a squadron of Imperial stormtroopers.");
        setGameText("If opponent just initiated battle, one of your Lift Tubes present may move as a 'react' from that battle. OR Place one of your unoccupied Lift Tubes on table in your Used Pile. OR Search your Reserve Deck, take one Lift Tube into hand and reshuffle.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<>();
        GameTextActionId gameTextActionId = GameTextActionId.LIFT_TUBE_ESCAPE__UPLOAD_LIFT_TUBE;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take a Lift Tube into hand from Reserve Deck");
            // Allow response(s)
            action.allowResponses("Take a Lift Tube into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.title(Title.Lift_Tube), true));
                        }
                    }
            );
            actions.add(action);
        }

        Filter unoccupiedLiftTubeFilter = Filters.and(Filters.your(self), Filters.title(Title.Lift_Tube), Filters.not(Filters.hasPassenger(self, Filters.any)));
        if (GameConditions.canSpot(game, self, unoccupiedLiftTubeFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target an unoccupied Lift Tube");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose an unoccupied Lift Tube", unoccupiedLiftTubeFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            // Allow response(s)
                            action.allowResponses("Put " + GameUtils.getCardLink(targetedCard) + " in Used Pile",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = action.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new PlaceCardInUsedPileFromTableEffect(action, finalTarget));
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
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)) {
            Filter liftTubeFilter = Filters.and(Filters.your(self), Filters.title(Title.Lift_Tube), Filters.presentInBattle, Filters.notPreventedFromParticipatingInReact, Filters.canMoveAsReactAsActionFromOtherCard(self, false, 0, true));
            if (GameConditions.canTarget(game, self, liftTubeFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Move Lift Tube away as 'react'");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Lift Tube", liftTubeFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                                action.addAnimationGroup(targetedCard);
                                // Allow response(s)
                                action.allowResponses("Move " + GameUtils.getCardLink(targetedCard) + " away as a 'react'",
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(Action targetingAction) {
                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                // This needs to be done in case the target(s) were changed during the responses.
                                                PhysicalCard finalLiftTube = action.getPrimaryTargetCard(targetGroupId);

                                                // Perform result(s)
                                                action.appendEffect(
                                                        new MoveAwayAsReactEffect(action, finalLiftTube, false));
                                            }
                                        }
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return actions;
    }
}
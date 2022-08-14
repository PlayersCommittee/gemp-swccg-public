package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Get Alongside That One
 */
public class Card8_053 extends AbstractUsedInterrupt {
    public Card8_053() {
        super(Side.LIGHT, 4, Title.Get_Alongside_That_One, Uniqueness.UNIQUE);
        setLore("Versatility is key to the Rebellion. The Rebels' ability to adapt to any situation is well-known.");
        setGameText("Target two non-creature vehicles with maneuver (one yours, one opponent's) present at same site. Both players draw destiny; add your vehicle's maneuver to your total. Lowest total loses vehicle. OR Your speeder bike piloted by Luke or a scout adds one battle destiny.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        final String opponent = game.getOpponent(playerId);
        Filter opponentsVehicleFilter = Filters.and(Filters.opponents(self), Filters.non_creature_vehicle, Filters.hasManeuverDefined, Filters.presentAt(Filters.site), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST));
        final Filter yourVehicleFilter = Filters.and(Filters.your(self), Filters.or(Filters.piloted, Filters.driven), Filters.non_creature_vehicle, Filters.hasManeuverDefined, Filters.presentWith(self, opponentsVehicleFilter));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, opponentsVehicleFilter)
            && GameConditions.canTarget(game, self, yourVehicleFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target two non-creature vehicles");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose your vehicle", yourVehicleFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard yourVehicle) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose opponent's vehicle", TargetingReason.TO_BE_LOST,
                                            Filters.and(Filters.opponents(self), Filters.non_creature_vehicle, Filters.hasManeuverDefined, Filters.presentWith(yourVehicle))) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, final PhysicalCard opponentsVehicle) {
                                            action.addAnimationGroup(yourVehicle, opponentsVehicle);
                                            // Allow response(s)
                                            action.allowResponses("Target " + GameUtils.getCardLink(yourVehicle) + " and " + GameUtils.getCardLink(opponentsVehicle),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard yourFinalTarget = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard opponentsFinalTarget = targetingAction.getPrimaryTargetCard(targetGroupId2);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                         @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            return Collections.singletonList(yourFinalTarget);
                                                                        }
                                                                        @Override
                                                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> playersDestinyCardDraws, List<Float> playersDestinyDrawValues, final Float playersTotalDestiny) {
                                                                            action.appendEffect(
                                                                                    new DrawDestinyEffect(action, opponent) {
                                                                                        @Override
                                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> opponentsDestinyCardDraws, List<Float> opponentsDestinyDrawValues, Float opponentsTotalDestiny) {
                                                                                            GameState gameState = game.getGameState();

                                                                                            gameState.sendMessage(playerId + "'s destiny: " + (playersTotalDestiny != null ? GuiUtils.formatAsString(playersTotalDestiny) : "Failed destiny draw"));
                                                                                            gameState.sendMessage(opponent + "'s destiny: " + (opponentsTotalDestiny != null ? GuiUtils.formatAsString(opponentsTotalDestiny) : "Failed destiny draw"));
                                                                                            float yourManeuver = game.getModifiersQuerying().getManeuver(gameState, yourFinalTarget);
                                                                                            gameState.sendMessage(GameUtils.getCardLink(yourFinalTarget) + "'s maneuver: " + GuiUtils.formatAsString(yourManeuver));

                                                                                            float playersTotal = (playersTotalDestiny != null ? playersTotalDestiny : 0) + yourManeuver;
                                                                                            gameState.sendMessage(playerId + "'s total: " + GuiUtils.formatAsString(playersTotal));
                                                                                            float opponentsTotal = (opponentsTotalDestiny != null ? opponentsTotalDestiny : 0);
                                                                                            gameState.sendMessage(opponent + "'s total: " + GuiUtils.formatAsString(opponentsTotal));


                                                                                            if (playersTotalDestiny == null && opponentsTotalDestiny == null) {
                                                                                                gameState.sendMessage("Both players failed due to failed destiny draws");
                                                                                                gameState.sendMessage("Result: No result");
                                                                                            } else if (playersTotalDestiny == null) {
                                                                                                gameState.sendMessage(playerId + "'s total failed due to failed destiny draw");
                                                                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(yourFinalTarget) + " to be lost");
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, yourFinalTarget));
                                                                                            } else if (opponentsTotalDestiny == null) {
                                                                                                gameState.sendMessage(opponent + "'s total failed due to failed destiny draw");
                                                                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(opponentsFinalTarget) + " to be lost");
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, opponentsFinalTarget));
                                                                                            } else if (playersTotal > opponentsTotal) {
                                                                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(opponentsFinalTarget) + " to be lost");
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, opponentsFinalTarget));
                                                                                            }
                                                                                            else if (opponentsTotal > playersTotal) {
                                                                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(yourFinalTarget) + " to be lost");
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, yourFinalTarget));
                                                                                            }
                                                                                            else {
                                                                                                gameState.sendMessage("Result: No result");
                                                                                            }
                                                                                        }
                                                                                    }
                                                                            );
                                                                        }
                                                                    }
                                                            );
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

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.speeder_bike, Filters.hasPiloting(self, Filters.or(Filters.Luke, Filters.scout))))
                && GameConditions.canAddBattleDestinyDraws(game, self)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add one battle destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new AddBattleDestinyEffect(action, 1));
                        }
                    }
            );
            actions.add(action);
        }

        return actions;
    }
}
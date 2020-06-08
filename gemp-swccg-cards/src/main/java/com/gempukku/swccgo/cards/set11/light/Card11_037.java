package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Losing Track
 */
public class Card11_037 extends AbstractUsedOrLostInterrupt {
    public Card11_037() {
        super(Side.LIGHT, 6, "Losing Track", Uniqueness.UNIQUE);
        setLore("'Wha-? Chuba da noya!'");
        setGameText("USED: During battle, place top card of opponent's Lost Pile on top of their Reserve Deck. LOST: Use 1 Force to 'repair' your Podracer. OR Target a Podracer. Draw destiny. If destiny > Podracer's destiny number, target is 'damaged.'");
        addIcons(Icon.TATOOINE, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringBattle(game)
                && GameConditions.hasLostPile(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Place top card of opponent's Lost Pile on Reserve Deck");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlaceTopCardOfLostPileOnTopOfReserveDeckEffect(action, opponent));
                        }
                    }
            );
            actions.add(action);
        }

        Filter yourDamagedPodracerFilter = Filters.and(Filters.your(self), Filters.damaged, Filters.Podracer);

        // Check condition(s)
        if (GameConditions.isDuringPodrace(game)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canTarget(game, self, yourDamagedPodracerFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("'Repair' a Podracer");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Podracer", yourDamagedPodracerFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedPodracer) {
                            action.addAnimationGroup(targetedPodracer);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 1));
                            // Allow response(s)
                            action.allowResponses("'Repair' " + GameUtils.getCardLink(targetedPodracer),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            PhysicalCard finalPodracer = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new RepairPodracerEffect(action, finalPodracer));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        // Check condition(s)
        if (GameConditions.isDuringPodrace(game)
                && GameConditions.canTarget(game, self, Filters.Podracer)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Make a Podracer 'damaged'");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Podracer", Filters.Podracer) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedPodracer) {
                            action.addAnimationGroup(targetedPodracer);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedPodracer) + " 'damaged'",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the final targeted card(s)
                                            final PhysicalCard finalPodracer = action.getPrimaryTargetCard(targetGroupId);
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            float podracerDestiny = game.getModifiersQuerying().getDestiny(game.getGameState(), finalPodracer);
                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            gameState.sendMessage("Podracer's destiny: " + GuiUtils.formatAsString(podracerDestiny));
                                                            if (totalDestiny > podracerDestiny) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new DamagePodracerEffect(action, finalPodracer));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        return actions;
    }
}
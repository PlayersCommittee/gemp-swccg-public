package com.gempukku.swccgo.cards.set10.light;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Reflections II
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Out Of Commission & Transmission Terminated
 */
public class Card10_016 extends AbstractUsedOrLostInterrupt {
    public Card10_016() {
        super(Side.LIGHT, 5, "Out Of Commission & Transmission Terminated");
        addComboCardTitles(Title.Out_Of_Commission, Title.Transmission_Terminated);
        setGameText("USED: Use 1 Force to randomly place one card in opponents Lost Pile out of play. LOST: Cancel one hologram. OR Use 4 Force. Draw destiny. Add 1 to destiny for each asteroid sector on table. If total destiny > 3, Imperial Holotable site is 'blown away' (opponent loses 4 Force).");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasLostPile(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, GameTextActionId.OUT_OF_COMMISSION__PLACE_CARD_OUT_OF_PLAY, CardSubtype.USED);
            action.setText("Place card from Lost Pile out of play");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Place a random card from opponent's Lost Pile out of play",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new PlaceRandomCardOutOfPlayFromLostPileEffect(action, opponent));
                        }
                    }
            );
            actions.add(action);
        }
        // Check condition(s)
        if (GameConditions.canTargetToCancel(game, self, Filters.hologram)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.hologram, "hologram");
            actions.add(action);
        }

        Filter holotableFilter = Filters.Imperial_Holotable;
        TargetingReason targetingReason = TargetingReason.TO_BE_BLOWN_AWAY;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, holotableFilter)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 4)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("'Blow away' Imperial Holotable");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose Imperial Holotable", targetingReason, holotableFilter) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard holotable) {
                            action.addAnimationGroup(holotable);
                            // Pay cost(s)
                            action.appendCost(
                                    new UseForceEffect(action, playerId, 4));
                            // Allow response(s)
                            action.allowResponses("'Blow away' " + GameUtils.getCardLink(holotable),
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                            Modifier modifier = new TotalDestinyModifier(self, drawDestinyState.getId(), new OnTableEvaluator(self, Filters.asteroid_sector));
                                                            return Collections.singletonList(modifier);
                                                        }
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));

                                                            if (totalDestiny > 3) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new BlowAwayEffect(action, holotable));
                                                            }
                                                            else {
                                                                gameState.sendMessage("Result: Failed");
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
            actions.add(action);
        }

        return actions;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalBeforeActions(String playerId, SwccgGame game, Effect effect, PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCard(game, effect, Filters.hologram)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
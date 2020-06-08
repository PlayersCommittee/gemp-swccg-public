package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.TotalDestinyModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Ewok Log Jam
 */
public class Card8_048 extends AbstractUsedInterrupt {
    public Card8_048() {
        super(Side.LIGHT, 4, "Ewok Log Jam", Uniqueness.UNIQUE);
        setLore("Any problem can be solved by the proper application of the principles of mass and velocity.");
        setGameText("If opponent's transport vehicle or AT-ST just deployed or moved to an Endor site, draw destiny. Add 1 for each of your Ewoks present. Vehicle lost if total destiny -1 > defense value. OR Lose 1 Force to take into hand a card you just drew for battle or weapon destiny.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if ((TriggerConditions.isBattleDestinyJustDrawnBy(game, effectResult, playerId)
                || TriggerConditions.isWeaponDestinyJustDrawnBy(game, effectResult, playerId))
                && GameConditions.canTakeDestinyCardIntoHand(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Take destiny card into hand");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Allow response(s)
            action.allowResponses("Take just drawn destiny card, " + GameUtils.getCardLink(((DestinyDrawnResult) effectResult).getCard()) + ", into hand",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeDestinyCardIntoHandEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }

        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter vehicleFilter = Filters.and(Filters.opponents(self), Filters.and(Filters.or(Filters.transport_vehicle, Filters.AT_ST), Filters.canBeTargetedBy(self, targetingReason)));
        Filter locationFilter = Filters.Endor_site;

        // Check condition(s)
        Collection<PhysicalCard> vehicles = null;
        if (TriggerConditions.justDeployedTo(game, effectResult, vehicleFilter, locationFilter)) {
            vehicles = Collections.singletonList(((PlayCardResult) effectResult).getPlayedCard());
        }
        else if (TriggerConditions.movedToLocation(game, effectResult, vehicleFilter, locationFilter)) {
            vehicles = Filters.filter(((MovedResult) effectResult).getMovedCards(), game, vehicleFilter);
        }
        if (vehicles != null && !vehicles.isEmpty()) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target vehicle");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose vehicle", targetingReason, Filters.in(vehicles)) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            // Allow response(s)
                            action.allowResponses("Make " + GameUtils.getCardLink(cardTargeted) + " lost",
                                    new RespondablePlayCardEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            // Get the targeted card(s) from the action using the targetGroupId.
                                            // This needs to be done in case the target(s) were changed during the responses.
                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId);

                                            // Perform result(s)
                                            action.appendEffect(
                                                    new DrawDestinyEffect(action, playerId) {
                                                        @Override
                                                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                            Modifier modifier = new TotalDestinyModifier(self, drawDestinyState.getId(), new PresentEvaluator(self, finalTarget, Filters.and(Filters.your(self), Filters.Ewok)));
                                                            return Collections.singletonList(modifier);
                                                        }
                                                        @Override
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(finalTarget);
                                                        }
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            float defenseValue = modifiersQuerying.getDefenseValue(gameState, finalTarget);
                                                            gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));

                                                            if ((totalDestiny - 1) > defenseValue) {
                                                                gameState.sendMessage("Result: Succeeded");
                                                                action.appendEffect(
                                                                        new LoseCardFromTableEffect(action, finalTarget));
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
            return Collections.singletonList(action);
        }

        return null;
    }
}
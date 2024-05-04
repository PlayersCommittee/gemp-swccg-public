package com.gempukku.swccgo.cards.set8.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CrashVehicleEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.ModifyTotalPowerUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.MovedResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Set: Endor
 * Type: Interrupt
 * Subtype: Used
 * Title: Ewok And Roll
 */
public class Card8_047 extends AbstractUsedInterrupt {
    public Card8_047() {
        super(Side.LIGHT, 5, "Ewok And Roll", Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.C);
        setLore("Even an All Terrain Scout Transport cannot stand on all terrain.");
        setGameText("If opponent's creature vehicle or AT-ST just deployed or moved to where your Ewok is present, draw destiny. Creature vehicle lost if destiny > defense value. AT-ST crashed if destiny +2 > armor. OR If your Ewok is defending a battle, add 2 to your total power.");
        addIcons(Icon.ENDOR);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        Filter vehicleFilter = Filters.and(Filters.opponents(self), Filters.or(Filters.and(Filters.creature_vehicle,
                Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST)), Filters.and(Filters.AT_ST, Filters.canBeTargetedBy(self, TargetingReason.TO_BE_CRASHED))));
        Filter locationFilter = Filters.wherePresent(self, Filters.and(Filters.your(self), Filters.Ewok));

        // Check condition(s)
        Collection<PhysicalCard> vehicles = null;
        if (TriggerConditions.justDeployedTo(game, effectResult, vehicleFilter, locationFilter)) {
            vehicles = Collections.singletonList(((PlayCardResult) effectResult).getPlayedCard());
        }
        else if (TriggerConditions.movedToLocation(game, effectResult, vehicleFilter, locationFilter)) {
            vehicles = Filters.filter(((MovedResult) effectResult).getMovedCards(), game, vehicleFilter);
        }
        if (vehicles != null && !vehicles.isEmpty()) {
            Map<TargetingReason, Filterable> targetFiltersMap = new HashMap<TargetingReason, Filterable>();
            targetFiltersMap.put(TargetingReason.TO_BE_LOST, Filters.and(Filters.in(vehicles), Filters.creature_vehicle));
            targetFiltersMap.put(TargetingReason.TO_BE_CRASHED, Filters.and(Filters.in(vehicles), Filters.AT_ST));

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target vehicle");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose vehicle", targetFiltersMap) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard cardTargeted) {
                            action.addAnimationGroup(cardTargeted);
                            String msgText = Filters.creature_vehicle.accepts(game, cardTargeted) ? ("Make " + GameUtils.getCardLink(cardTargeted) + " lost") : ("Crash " + GameUtils.getCardLink(cardTargeted));
                            // Allow response(s)
                            action.allowResponses(msgText,
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
                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                            return Collections.singletonList(finalTarget);
                                                        }
                                                        @Override
                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                            GameState gameState = game.getGameState();
                                                            if (totalDestiny == null) {
                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                return;
                                                            }

                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                            if (Filters.creature_vehicle.accepts(game, finalTarget)) {

                                                                float defenseValue = game.getModifiersQuerying().getDefenseValue(game.getGameState(), finalTarget);
                                                                gameState.sendMessage("Defense value: " + GuiUtils.formatAsString(defenseValue));
                                                                if (totalDestiny > defenseValue) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new LoseCardFromTableEffect(action, finalTarget));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
                                                            }
                                                            else {

                                                                float armor = game.getModifiersQuerying().getArmor(game.getGameState(), finalTarget);
                                                                gameState.sendMessage("Armor: " + GuiUtils.formatAsString(armor));
                                                                if ((totalDestiny + 2) > armor) {
                                                                    gameState.sendMessage("Result: Succeeded");
                                                                    action.appendEffect(
                                                                            new CrashVehicleEffect(action, finalTarget, self));
                                                                }
                                                                else {
                                                                    gameState.sendMessage("Result: Failed");
                                                                }
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

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.your(self), Filters.Ewok, Filters.defendingBattle))) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Add 2 to total power");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new ModifyTotalPowerUntilEndOfBattleEffect(action, 2, playerId, "Adds 2 to total power"));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
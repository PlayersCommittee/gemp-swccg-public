package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardToLoseFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.EachDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: Under Attack
 */
public class Card3_050 extends AbstractUsedInterrupt {
    public Card3_050() {
        super(Side.LIGHT, 6, Title.Under_Attack);
        setLore("With the Rebels' limited resources and small numbers, desperate strategies are often required.");
        setGameText("During your control phase, target a vehicle with armor present with your warrior. Draw destiny. If warrior has a Concussion Grenade or a lightsaber, add 3 to destiny draw (7 if both). Vehicle (and grenade) lost if total destiny > armor.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        Filter filter = Filters.and(Filters.vehicle, Filters.hasArmor, Filters.presentWith(self, Filters.and(Filters.your(self), Filters.warrior)));

        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.CONTROL)
                && GameConditions.canTarget(game, self, targetingReason, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose vehicle", targetingReason, filter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard vehicle) {
                            Filter filter2 = Filters.and(Filters.your(self), Filters.warrior, Filters.presentWith(vehicle));
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose warrior", filter2) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard warrior) {
                                            action.addAnimationGroup(warrior, vehicle);
                                            // Allow response(s)
                                            action.allowResponses("Target " + GameUtils.getCardLink(vehicle) + " present with " + GameUtils.getCardLink(warrior),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalVehicle = action.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard finalWarrior = action.getPrimaryTargetCard(targetGroupId2);
                                                            final Filter grenadeFilter = Filters.and(Filters.Concussion_Grenade, Filters.attachedTo(finalWarrior));

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                                            boolean hasGrenade = GameConditions.canSpot(game, self, grenadeFilter);
                                                                            boolean hasLightsaber = Filters.armedWith(Filters.lightsaber).accepts(game, finalWarrior);
                                                                            if (hasGrenade || hasLightsaber) {
                                                                                Modifier modifier = new EachDestinyModifier(self, drawDestinyState.getId(), (hasGrenade && hasLightsaber) ? 7 : 3);
                                                                                return Collections.singletonList(modifier);
                                                                            }
                                                                            return null;
                                                                        }
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                            GameState gameState = game.getGameState();
                                                                            if (totalDestiny == null) {
                                                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                                return;
                                                                            }

                                                                            float armor = game.getModifiersQuerying().getArmor(game.getGameState(), finalVehicle);
                                                                            gameState.sendMessage("Total destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            gameState.sendMessage("Armor: " + GuiUtils.formatAsString(armor));
                                                                            if (totalDestiny > armor) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                if (GameConditions.canSpot(game, self, grenadeFilter)) {
                                                                                    action.appendEffect(
                                                                                            new ChooseCardToLoseFromTableEffect(action, playerId, true, grenadeFilter));
                                                                                }
                                                                                action.appendEffect(
                                                                                        new LoseCardFromTableEffect(action, finalVehicle));
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
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
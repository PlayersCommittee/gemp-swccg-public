package com.gempukku.swccgo.cards.set3.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawFerocityDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.modifiers.FerocityModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.EatenResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Hoth
 * Type: Interrupt
 * Subtype: Used
 * Title: Yaggle Gakkle
 */
public class Card3_142 extends AbstractUsedInterrupt {
    public Card3_142() {
        super(Side.DARK, 6, Title.Yaggle_Gakkle, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.R2);
        setLore("Steady. Hey! Steady girl. Hey, what's the matter? You smell something?");
        setGameText("Target a creature vehicle at same site as a creature. If ferocity > target's maneuver + landspeed, creature vehicle is eaten, cumulatively adding 2 to creature's ferocity.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final TargetingReason targetingReason = TargetingReason.TO_BE_LOST;
        Filter vehicleFilter = Filters.and(Filters.creature_vehicle, Filters.hasManeuverDefined,
                Filters.at(Filters.sameSiteAs(self, Filters.creature)));

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, vehicleFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target creature vehicle");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose creature vehicle", targetingReason, vehicleFilter) {
                        @Override
                        protected void cardTargeted(final int vehicleTargetGroupId, final PhysicalCard targetedVehicle) {
                            Filter creatureFilter = Filters.and(Filters.creature, Filters.atSameSite(targetedVehicle));
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose creature", creatureFilter) {
                                        @Override
                                        protected void cardTargeted(final int creatureTargetGroupId, PhysicalCard targetedCreature) {
                                            action.addAnimationGroup(targetedVehicle);
                                            action.addAnimationGroup(targetedCreature);
                                            // Allow response(s)
                                            action.allowResponses("Target " + GameUtils.getCardLink(targetedVehicle)
                                                            + " with " + GameUtils.getCardLink(targetedCreature),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            final PhysicalCard finalVehicle = action.getPrimaryTargetCard(vehicleTargetGroupId);
                                                            final PhysicalCard finalCreature = action.getPrimaryTargetCard(creatureTargetGroupId);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawFerocityDestinyEffect(action, finalCreature) {
                                                                        @Override
                                                                        protected void totalFerocityDestinyCalculated(Float totalFerocityDestiny) {
                                                                            GameState gameState = game.getGameState();
                                                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                                                                            if (totalFerocityDestiny == null
                                                                                    && modifiersQuerying.getNumFerocityDestiny(gameState, finalCreature) > 0) {
                                                                                gameState.sendMessage("Result: Failed due to failed ferocity destiny draw");
                                                                                return;
                                                                            }

                                                                            float ferocity = modifiersQuerying.getFerocity(gameState, finalCreature, totalFerocityDestiny);
                                                                            float maneuver = modifiersQuerying.getManeuver(gameState, finalVehicle);
                                                                            float landspeed = modifiersQuerying.getLandspeed(gameState, finalVehicle);
                                                                            float threshold = maneuver + landspeed;

                                                                            gameState.sendMessage("Ferocity: " + GuiUtils.formatAsString(ferocity));
                                                                            gameState.sendMessage("Maneuver: " + GuiUtils.formatAsString(maneuver));
                                                                            gameState.sendMessage("Landspeed: " + GuiUtils.formatAsString(landspeed));
                                                                            gameState.sendMessage("Maneuver + landspeed: " + GuiUtils.formatAsString(threshold));

                                                                            if (ferocity > threshold) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                float forfeit = modifiersQuerying.getForfeit(gameState, finalVehicle);
                                                                                PhysicalCard location = modifiersQuerying.getLocationThatCardIsAt(gameState, finalVehicle);
                                                                                action.appendEffect(
                                                                                        new TriggeringResultEffect(action,
                                                                                                new EatenResult(finalVehicle, null, ferocity, forfeit, false, finalCreature, location)));
                                                                                action.appendEffect(
                                                                                        new LoseCardFromTableEffect(action, finalVehicle));
                                                                                action.appendEffect(
                                                                                        new AddUntilEndOfGameModifierEffect(action,
                                                                                                new FerocityModifier(self, finalCreature, 2, true),
                                                                                                "Adds 2 to " + GameUtils.getCardLink(finalCreature) + "'s ferocity"));
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

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
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.CrashVehicleEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
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
 * Title: Crash Landing
 */
public class Card3_120 extends AbstractUsedInterrupt {
    public Card3_120() {
        super(Side.DARK, 4, Title.Crash_Landing, Uniqueness.UNRESTRICTED, ExpansionSet.HOTH, Rarity.U1);
        setLore("AT-AT weapons are rarely quick enough to score a direct hit on a fastmoving snowspeeder. They are more likely to wing a craft, causing a forced landing.");
        setGameText("If you have a piloted AT-AT present at a site, target opponent's non-creature vehicle present at same or adjacent exterior site. Draw destiny. If AT-AT has a vehicle weapon, add 1 to destiny draw. Target 'crashes' if total destiny > 3.");
        addIcons(Icon.HOTH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        // Check condition(s)
        Filter atatFilter = Filters.and(Filters.your(self), Filters.piloted, Filters.AT_AT, Filters.presentAt(Filters.site));
        Filter filter = Filters.and(Filters.opponents(self), Filters.non_creature_vehicle, Filters.not(Filters.crashed),
                Filters.presentAt(Filters.and(Filters.exterior_site, Filters.sameOrAdjacentSiteAs(self, atatFilter))));
        final TargetingReason targetingReason = TargetingReason.TO_BE_CRASHED;

        // Check condition(s)
        if (GameConditions.canTarget(game, self, targetingReason, filter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            // Choose target(s)
            action.appendTargeting(
                    new ChooseCardOnTableEffect(action, playerId, "Choose AT-AT", atatFilter) {
                        @Override
                        protected void cardSelected(final PhysicalCard atat) {
                            action.addAnimationGroup(atat);
                            Filter vehicleFilter = Filters.and(Filters.opponents(self), Filters.non_creature_vehicle, Filters.not(Filters.crashed),
                                    Filters.presentAt(Filters.and(Filters.exterior_site, Filters.sameOrAdjacentSite(atat))));
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose vehicle", targetingReason, vehicleFilter) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId1, PhysicalCard vehicle) {
                                            action.addAnimationGroup(vehicle);
                                            // Allow response(s)
                                            action.allowResponses("'Crash' " + GameUtils.getCardLink(vehicle),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalVehicle = action.getPrimaryTargetCard(targetGroupId1);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected List<Modifier> getDrawDestinyModifiers(SwccgGame game, DrawDestinyState drawDestinyState) {
                                                                            if (Filters.hasAttached(Filters.vehicle_weapon).accepts(game, atat)) {
                                                                                Modifier modifier = new EachDestinyModifier(self, drawDestinyState.getId(), 1);
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

                                                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                            if (totalDestiny > 3) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new CrashVehicleEffect(action, finalVehicle, self));
                                                                            } else {
                                                                                gameState.sendMessage("Result: Failed");
                                                                            }
                                                                        }
                                                                    });
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
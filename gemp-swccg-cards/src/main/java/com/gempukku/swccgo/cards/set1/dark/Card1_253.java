package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.results.BattleEndedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: I've Got A Problem Here
 */
public class Card1_253 extends AbstractLostInterrupt {
    public Card1_253() {
        super(Side.DARK, 4, Title.Ive_Got_A_Problem_Here);
        setLore("Debris fragments damaged Jek Porkins' X-wing, causing a cascade of computer and flight control failures.");
        setGameText("Use 1 Force to target opponent's starfighter with maneuver at a system or sector where a battle just finished. Draw destiny. Starfighter lost if destiny > maneuver.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.battleEndedAt(game, effectResult, Filters.system_or_sector)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            PhysicalCard location = ((BattleEndedResult) effectResult).getLocation();
            Filter filter = Filters.and(Filters.opponents(self), Filters.starfighter, Filters.hasManeuverDefined, Filters.at(location));
            TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

            if (GameConditions.canTarget(game, self, targetingReason, filter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Target opponent's starfighter");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose starfighter", targetingReason, filter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, PhysicalCard starfighter) {
                                action.addAnimationGroup(starfighter);
                                // Pay cost(s)
                                action.appendCost(
                                        new UseForceEffect(action, playerId, 1));
                                // Allow response(s)
                                action.allowResponses("Make " + GameUtils.getCardLink(starfighter) + " lost",
                                        new RespondablePlayCardEffect(action) {
                                                    @Override
                                                    protected void performActionResults(Action targetingAction) {
                                                        // Get the targeted card(s) from the action using the targetGroupId.
                                                        // This needs to be done in case the target(s) were changed during the responses.
                                                        final PhysicalCard finalStarfighter = action.getPrimaryTargetCard(targetGroupId);

                                                        // Perform result(s)
                                                        action.appendEffect(
                                                                new DrawDestinyEffect(action, playerId) {
                                                                    @Override
                                                                    protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                        return Collections.singletonList(finalStarfighter);
                                                                    }
                                                                    @Override
                                                                    protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                                                        GameState gameState = game.getGameState();
                                                                        if (totalDestiny == null) {
                                                                            gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                                            return;
                                                                        }

                                                                        float maneuver = game.getModifiersQuerying().getManeuver(game.getGameState(), finalStarfighter);
                                                                        gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                                                        gameState.sendMessage("Maneuver: " + GuiUtils.formatAsString(maneuver));
                                                                        if (totalDestiny > maneuver) {
                                                                            gameState.sendMessage("Result: Succeeded");
                                                                            action.appendEffect(
                                                                                    new LoseCardFromTableEffect(action, finalStarfighter));
                                                                        }
                                                                        else {
                                                                            gameState.sendMessage("Result: Failed");
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                }
                                        );
                            }
                        });
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
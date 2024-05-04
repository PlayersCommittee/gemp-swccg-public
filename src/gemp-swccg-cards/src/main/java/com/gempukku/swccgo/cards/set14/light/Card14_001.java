package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Theed Palace
 * Type: Admiral's Order
 * Title: I'll Try Spinning
 */
public class Card14_001 extends AbstractAdmiralsOrder {
    public Card14_001() {
        super(Side.LIGHT, "I'll Try Spinning", ExpansionSet.THEED_PALACE, Rarity.R);
        setGameText("Landed starfighters and vehicles aboard starships are forfeit = 0. Once during your control phase may target your N-1 starfighter and opponent's starfighter present at same system. Both players draw destiny. You add your starfighter's power and maneuver. Opponent adds starfighter's power. Starship with lowest total is lost.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ResetForfeitModifier(self, Filters.or(Filters.and(Filters.landed, Filters.starfighter),
                Filters.and(Filters.vehicle, Filters.aboardAnyStarship)), 0));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)) {
            Filter opponentsStarfighter = Filters.and(Filters.opponents(self), Filters.starfighter, Filters.presentAt(Filters.system), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_LOST));
            final Filter yourN1Starfighter = Filters.and(Filters.your(self), Filters.piloted, Filters.N1_starfighter, Filters.presentWith(self, opponentsStarfighter));
            if (GameConditions.canTarget(game, self, yourN1Starfighter)) {

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
                action.setText("Target N-1 starfighter and opponent's starfighter");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerPhaseEffect(action));
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose N-1 starfighter", yourN1Starfighter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard yourStarfighter) {
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose opponent's starfighter", TargetingReason.TO_BE_LOST,
                                                Filters.and(Filters.opponents(self), Filters.starfighter, Filters.presentWith(yourStarfighter))) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, final PhysicalCard opponentsStarfighter) {
                                                action.addAnimationGroup(yourStarfighter, opponentsStarfighter);
                                                // Allow response(s)
                                                action.allowResponses("Target " + GameUtils.getCardLink(yourStarfighter) + " and " + GameUtils.getCardLink(opponentsStarfighter),
                                                        new RespondableEffect(action) {
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
                                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> playersDestinyCardDraws, List<Float> playersDestinyDrawValues, final Float playersTotalDestiny) {
                                                                                final String opponent = game.getOpponent(playerId);

                                                                                action.appendEffect(
                                                                                        new DrawDestinyEffect(action, opponent) {
                                                                                            @Override
                                                                                            protected void destinyDraws(SwccgGame game, List<PhysicalCard> opponentsDestinyCardDraws, List<Float> opponentsDestinyDrawValues, Float opponentsTotalDestiny) {
                                                                                                GameState gameState = game.getGameState();

                                                                                                gameState.sendMessage(playerId + "'s destiny: " + (playersTotalDestiny != null ? GuiUtils.formatAsString(playersTotalDestiny) : "Failed destiny draw"));
                                                                                                gameState.sendMessage(opponent + "'s destiny: " + (opponentsTotalDestiny != null ? GuiUtils.formatAsString(opponentsTotalDestiny) : "Failed destiny draw"));
                                                                                                float yourPower = game.getModifiersQuerying().getPower(gameState, yourFinalTarget);
                                                                                                gameState.sendMessage(GameUtils.getCardLink(yourFinalTarget) + "'s power: " + GuiUtils.formatAsString(yourPower));
                                                                                                float yourManeuver = game.getModifiersQuerying().getManeuver(gameState, yourFinalTarget);
                                                                                                gameState.sendMessage(GameUtils.getCardLink(yourFinalTarget) + "'s maneuver: " + GuiUtils.formatAsString(yourManeuver));
                                                                                                float opponentsPower = game.getModifiersQuerying().getPower(gameState, opponentsFinalTarget);
                                                                                                gameState.sendMessage(GameUtils.getCardLink(opponentsFinalTarget) + "'s power: " + GuiUtils.formatAsString(opponentsPower));

                                                                                                float playersTotal = (playersTotalDestiny != null ? playersTotalDestiny : 0) + yourPower + yourManeuver;
                                                                                                gameState.sendMessage(playerId + "'s total: " + GuiUtils.formatAsString(playersTotal));
                                                                                                float opponentsTotal = (opponentsTotalDestiny != null ? opponentsTotalDestiny : 0) + opponentsPower;
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
                                                                                        });
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                );
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

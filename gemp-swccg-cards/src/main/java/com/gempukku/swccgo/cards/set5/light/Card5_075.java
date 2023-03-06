package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
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
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Cloud City
 * Type: Interrupt
 * Subtype: Lost
 * Title: Wookiee Strangle
 */
public class Card5_075 extends AbstractLostInterrupt {
    public Card5_075() {
        super(Side.LIGHT, 3, Title.Wookiee_Strangle, Uniqueness.UNIQUE, ExpansionSet.CLOUD_CITY, Rarity.R);
        setLore("Wookiees have been known to dislocate more than just arms.");
        setGameText("Use 2 Force to target an opponent's non-droid character present with one of your Wookiees. Both players draw destiny. Add ability and power. Target is lost if Wookiee's total destiny > target's total destiny.");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        Filter targetFilter = Filters.and(Filters.opponents(self), Filters.non_droid_character, Filters.presentWith(self, Filters.and(Filters.your(self), Filters.Wookiee, Filters.canBeTargetedBy(self))));
        TargetingReason targetingReason = TargetingReason.TO_BE_LOST;

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 2)
                && GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Target a non-droid character");
            // Choose target(s)
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose non-droid character", targetingReason, targetFilter) {
                        @Override
                        protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetCard) {
                            action.appendTargeting(
                                    new TargetCardOnTableEffect(action, playerId, "Choose Wookiee", Filters.and(Filters.your(self), Filters.Wookiee, Filters.presentWith(targetCard))) {
                                        @Override
                                        protected void cardTargeted(final int targetGroupId2, PhysicalCard wookiee) {
                                            action.addAnimationGroup(targetCard, wookiee);
                                            // Pay cost(s)
                                            action.appendCost(
                                                    new UseForceEffect(action, playerId, 2));
                                            // Allow response(s)
                                            action.allowResponses("Target " + GameUtils.getCardLink(wookiee) + " and " + GameUtils.getCardLink(targetCard),
                                                    new RespondablePlayCardEffect(action) {
                                                        @Override
                                                        protected void performActionResults(Action targetingAction) {
                                                            // Get the targeted card(s) from the action using the targetGroupId.
                                                            // This needs to be done in case the target(s) were changed during the responses.
                                                            final PhysicalCard finalTarget = targetingAction.getPrimaryTargetCard(targetGroupId1);
                                                            final PhysicalCard finalWookiee = targetingAction.getPrimaryTargetCard(targetGroupId2);

                                                            // Perform result(s)
                                                            action.appendEffect(
                                                                    new DrawDestinyEffect(action, playerId) {
                                                                        @Override
                                                                        protected Collection<PhysicalCard> getGameTextAbilityManeuverOrDefenseValueTargeted() {
                                                                            return Arrays.asList(finalTarget, finalWookiee);
                                                                        }
                                                                        @Override
                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float playersTotalDestiny) {
                                                                            action.appendEffect(
                                                                                    new DrawDestinyEffect(action, opponent) {
                                                                                        @Override
                                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float opponentsTotalDestiny) {
                                                                                            GameState gameState = game.getGameState();

                                                                                            gameState.sendMessage(playerId + "'s destiny: " + (playersTotalDestiny != null ? GuiUtils.formatAsString(playersTotalDestiny) : "Failed destiny draw"));
                                                                                            gameState.sendMessage(opponent + "'s destiny: " + (opponentsTotalDestiny != null ? GuiUtils.formatAsString(opponentsTotalDestiny) : "Failed destiny draw"));
                                                                                            float yourAbility = game.getModifiersQuerying().getAbility(gameState, finalWookiee);
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalWookiee) + "'s ability: " + GuiUtils.formatAsString(yourAbility));
                                                                                            float yourPower = game.getModifiersQuerying().getPower(gameState, finalWookiee);
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalWookiee) + "'s power: " + GuiUtils.formatAsString(yourPower));

                                                                                            float opponentsAbility = game.getModifiersQuerying().getAbility(gameState, finalTarget);
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalTarget) + "'s ability: " + GuiUtils.formatAsString(opponentsAbility));
                                                                                            float opponentsPower = game.getModifiersQuerying().getPower(gameState, finalTarget);
                                                                                            gameState.sendMessage(GameUtils.getCardLink(finalTarget) + "'s power: " + GuiUtils.formatAsString(opponentsAbility));


                                                                                            if (playersTotalDestiny == null && opponentsTotalDestiny == null) {
                                                                                                gameState.sendMessage("Both players failed due to failed destiny draws");
                                                                                                gameState.sendMessage("Result: No result");
                                                                                            } else if (playersTotalDestiny == null) {
                                                                                                gameState.sendMessage(playerId + "'s total failed due to failed destiny draw");
                                                                                                gameState.sendMessage("Result: No result");
                                                                                            } else if (opponentsTotalDestiny == null) {
                                                                                                gameState.sendMessage(opponent + "'s total failed due to failed destiny draw");
                                                                                                gameState.sendMessage("Result: " + GameUtils.getCardLink(finalTarget) + " to be lost");
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, finalTarget));
                                                                                            } else {
                                                                                                float playersTotal = (playersTotalDestiny != null ? playersTotalDestiny : 0) + yourAbility + yourPower;
                                                                                                gameState.sendMessage(playerId + "'s total: " + GuiUtils.formatAsString(playersTotal));
                                                                                                float opponentsTotal = (opponentsTotalDestiny != null ? opponentsTotalDestiny : 0) + opponentsAbility + opponentsPower;
                                                                                                gameState.sendMessage(opponent + "'s total: " + GuiUtils.formatAsString(opponentsTotal));
                                                                                                if (playersTotal > opponentsTotal) {
                                                                                                    gameState.sendMessage("Result: " + GameUtils.getCardLink(finalTarget) + " to be lost");
                                                                                                    action.appendEffect(
                                                                                                            new LoseCardFromTableEffect(action, finalTarget));
                                                                                                } else {
                                                                                                    gameState.sendMessage("Result: No result");
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
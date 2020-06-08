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
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: The Circle Is Now Complete
 */
public class Card1_271 extends AbstractLostInterrupt {
    public Card1_271() {
        super(Side.DARK, 6, Title.The_Circle_Is_Now_Complete);
        setLore("Vader and Obi-Wan Kenobi fought a final lightsaber duel near Hangar Bay 327 on the Death Star. 'When I left you I was but the learner; now I am the master.'");
        setGameText("If Vader and Obi-Wan are present at same site, use 1 Force to initiate a duel between them.  Compare their power, and add 2 if that character is armed with a lightsaber.  Loser (lowest total) of duel is placed out of play.");
    }

    @Override
    public Filter getValidDuelParticipant(Side side, SwccgGame game, final PhysicalCard self) {
        if (side == Side.DARK)
            return Filters.Vader;
        else
            return Filters.ObiWan;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final Filter darkSideFilter = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.DARK);
            Filter lightSideFilter = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.LIGHT);
            Filter lightSideToTargetFilter = Filters.and(lightSideFilter, Filters.presentAt(Filters.site), Filters.presentWith(self, darkSideFilter));
            if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_DUELED, lightSideToTargetFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Initiate duel");
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Light Side character", TargetingReason.TO_BE_DUELED, lightSideToTargetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedCard1) {
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose Dark Side character", Filters.and(darkSideFilter, Filters.presentWith(targetedCard1))) {
                                            @Override
                                            protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedCard2) {
                                                action.addAnimationGroup(targetedCard1, targetedCard2);
                                                // Pay cost(s)
                                                action.appendCost(
                                                        new UseForceEffect(action, playerId, 1));
                                                // Allow response(s)
                                                action.allowResponses("Initiate duel between " + GameUtils.getCardLink(targetedCard2) + " and " + GameUtils.getCardLink(targetedCard1),
                                                        new RespondablePlayCardEffect(action) {
                                                            @Override
                                                            protected void performActionResults(Action action) {
                                                                // Get the targeted card(s) from the action using the targetGroupId.
                                                                // This needs to be done in case the target(s) were changed during the responses.
                                                                PhysicalCard lightCardToDuel = action.getPrimaryTargetCard(targetGroupId1);
                                                                PhysicalCard darkCardToDuel = action.getPrimaryTargetCard(targetGroupId2);

                                                                // Perform result(s)
                                                                action.appendEffect(
                                                                        new DuelEffect(action, darkCardToDuel, lightCardToDuel,
                                                                                new DuelDirections() {
                                                                                    @Override
                                                                                    public boolean isEpicDuel() {
                                                                                        return false;
                                                                                    }

                                                                                    @Override
                                                                                    public boolean isCrossOverToDarkSideAttempt() {
                                                                                        return false;
                                                                                    }

                                                                                    @Override
                                                                                    public Evaluator getBaseDuelTotal(final String playerId, final DuelState duelState) {
                                                                                        return new BaseEvaluator() {
                                                                                            @Override
                                                                                            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                                                                                                PhysicalCard character = duelState.getCharacter(playerId);
                                                                                                float total =  modifiersQuerying.getPower(gameState, character);
                                                                                                if (Filters.armedWith(Filters.lightsaber).accepts(gameState, modifiersQuerying, character)) {
                                                                                                    total += 2;
                                                                                                }
                                                                                                return total;
                                                                                            }
                                                                                        };
                                                                                    }

                                                                                    @Override
                                                                                    public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) {
                                                                                        return 0;
                                                                                    }

                                                                                    @Override
                                                                                    public void performDuelDirections(Action action, SwccgGame game, DuelState duelState) {
                                                                                    }

                                                                                    @Override
                                                                                    public void performDuelResults(Action action, SwccgGame game, DuelState duelState) {
                                                                                        // Place losing character out of play
                                                                                        if (duelState.getLosingCharacter() != null) {
                                                                                            action.appendEffect(
                                                                                                    new PlaceCardOutOfPlayFromTableEffect(action, duelState.getLosingCharacter()));
                                                                                        }
                                                                                    }
                                                                                }));
                                                            }
                                                        });
                                            }
                                        }
                                );
                            }
                        }
                );
                actions.add(action);
            }
        }
        return actions;
    }
}
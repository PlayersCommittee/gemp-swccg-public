package com.gempukku.swccgo.cards.set101.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.PowerEvaluator;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.MovedResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premium (Premiere Introductory Two Player Game)
 * Type: Interrupt
 * Subtype: Lost
 * Title: Vader's Obsession
 */
public class Card101_006 extends AbstractLostInterrupt {
    public Card101_006() {
        super(Side.DARK, 6, Title.Vaders_Obsession, Uniqueness.UNIQUE);
        setLore("Vader sought to hunt down and destroy all Jedi. After completing the circle with Obi-Wan, he turned his attention to the young Skywalker.");
        setGameText("During your move phase, if Vader moves to Luke's site from an adjacent site, begin a duel between them. Each adds two destiny to power. Winner retrieves lost Force equal to the difference. Loser loses same amount of Force, plus the character.");
    }

    @Override
    public Filter getValidDuelParticipant(Side side, SwccgGame game, final PhysicalCard self) {
        if (side == Side.DARK)
            return Filters.or(Filters.Vader, Filters.grantedMayBeTargetedBy(self));
        else
            return Filters.Luke;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.MOVE)) {
            final Filter darkSideFilter = Filters.or(game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.DARK));
            Filter lightSideFilter = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.LIGHT);
            if (TriggerConditions.movedFromLocationToLocation(game, effectResult, darkSideFilter, Filters.adjacentSiteTo(self, lightSideFilter), Filters.sameSiteAs(self, lightSideFilter))) {
                MovedResult movedResult = (MovedResult) effectResult;
                final Collection<PhysicalCard> movedCards = Filters.filter(movedResult.getMovedCards(), game, darkSideFilter);
                Filter lightSideToTargetFilter = Filters.and(lightSideFilter, Filters.presentAt(Filters.site), Filters.presentWith(self, Filters.in(movedCards)));
                if (movedResult.isMoveComplete()
                        && GameConditions.canTarget(game, self, TargetingReason.TO_BE_DUELED, lightSideToTargetFilter)) {

                    final PlayInterruptAction action = new PlayInterruptAction(game, self);
                    action.setText("Initiate duel");
                    action.appendTargeting(
                            new TargetCardOnTableEffect(action, playerId, "Choose Light Side character", TargetingReason.TO_BE_DUELED, lightSideToTargetFilter) {
                                @Override
                                protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedCard1) {
                                    action.appendTargeting(
                                            new TargetCardOnTableEffect(action, playerId, "Choose Dark Side character", Filters.in(movedCards)) {
                                                @Override
                                                protected void cardTargeted(final int targetGroupId2, PhysicalCard targetedCard2) {
                                                    action.addAnimationGroup(targetedCard1, targetedCard2);
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
                                                                                            return new PowerEvaluator(duelState.getCharacter(playerId));
                                                                                        }

                                                                                        @Override
                                                                                        public int getBaseNumDuelDestinyDraws(String playerId, DuelState duelState) {
                                                                                            return 2;
                                                                                        }

                                                                                        @Override
                                                                                        public void performDuelDirections(final Action duelAction, SwccgGame game, final DuelState duelState) {
                                                                                            duelAction.appendEffect(
                                                                                                    new DrawDestinyEffect(duelAction, game.getDarkPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getDarkPlayer()), DestinyType.DUEL_DESTINY) {
                                                                                                        @Override
                                                                                                        protected void destinyDraws(SwccgGame game, final List<PhysicalCard> darkDestinyCardDraws, List<Float> darkDestinyDrawValues, final Float darkTotalDestiny) {
                                                                                                            if (darkTotalDestiny != null) {
                                                                                                                duelState.increaseTotalDuelDestinyFromDraws(game.getDarkPlayer(), darkTotalDestiny, darkDestinyCardDraws.size());
                                                                                                            }
                                                                                                            duelAction.appendEffect(
                                                                                                                    new DrawDestinyEffect(duelAction, game.getLightPlayer(), game.getModifiersQuerying().getNumDuelDestinyDraws(game.getGameState(), game.getLightPlayer()), DestinyType.DUEL_DESTINY) {
                                                                                                                        @Override
                                                                                                                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> lightDestinyCardDraws, List<Float> lightDestinyDrawValues, Float lightTotalDestiny) {
                                                                                                                            if (lightTotalDestiny != null) {
                                                                                                                                duelState.increaseTotalDuelDestinyFromDraws(game.getLightPlayer(), lightTotalDestiny, lightDestinyCardDraws.size());
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                            );
                                                                                                        }
                                                                                                    }
                                                                                            );
                                                                                        }

                                                                                        @Override
                                                                                        public void performDuelResults(Action action, SwccgGame game, final DuelState duelState) {
                                                                                            if (duelState.getWinner() != null) {
                                                                                                float difference = duelState.getFinalDuelTotal(duelState.getWinner()) - duelState.getFinalDuelTotal(duelState.getLoser());

                                                                                                // Winner retrieves Force equal to difference
                                                                                                action.appendEffect(
                                                                                                        new RetrieveForceEffect(action, duelState.getWinner(), difference) {
                                                                                                            @Override
                                                                                                            public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                                                                                                                return Collections.singletonList(duelState.getWinningCharacter());
                                                                                                            }
                                                                                                        });
                                                                                                // Loser loses Force equal to difference
                                                                                                action.appendEffect(
                                                                                                        new LoseForceEffect(action, duelState.getLoser(), difference));
                                                                                                action.appendEffect(
                                                                                                        new LoseCardFromTableEffect(action, duelState.getLosingCharacter()));
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
                    return Collections.singletonList(action);
                }
            }
        }
        return null;
    }
}
package com.gempukku.swccgo.cards.set11.light;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.evaluators.PowerEvaluator;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfDuelModifierEffect;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Tatooine
 * Type: Interrupt
 * Subtype: Lost
 * Title: You'll Find I'm Full Of Surprises
 */
public class Card11_041 extends AbstractLostInterrupt {
    public Card11_041() {
        super(Side.LIGHT, 6, "You'll Find I'm Full Of Surprises", Uniqueness.UNIQUE, ExpansionSet.TATOOINE, Rarity.U);
        setLore("Even though he wasn't fully prepared to face the Dark Lord of the Sith, Luke rushed to save his friends.");
        setGameText("If Luke and Vader present at same site, use 1 Force to initiate a duel between them. Either is power +2 if armed with a lightsaber. Draw destiny. Opponent draws two destiny. Both players add power. Loser is placed out of play.");
        addIcons(Icon.TATOOINE);
    }

    @Override
    public Filter getValidDuelParticipant(Side side, SwccgGame game, final PhysicalCard self) {
        if (side == Side.DARK)
            return Filters.Vader;
        else
            return Filters.Luke;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        if (GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {
            final Filter lightSideFilter = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.LIGHT);
            Filter darkSideFilter = game.getModifiersQuerying().getValidDuelParticipant(game.getGameState(), self, Side.DARK);
            Filter darkSideToTargetFilter = Filters.and(darkSideFilter, Filters.presentAt(Filters.site), Filters.presentWith(self, lightSideFilter));
            if (GameConditions.canTarget(game, self, TargetingReason.TO_BE_DUELED, darkSideToTargetFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Initiate duel");
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Dark Side character", TargetingReason.TO_BE_DUELED, darkSideToTargetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId1, final PhysicalCard targetedCard1) {
                                action.appendTargeting(
                                        new TargetCardOnTableEffect(action, playerId, "Choose Light Side character", Filters.and(lightSideFilter, Filters.presentWith(targetedCard1))) {
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
                                                                final PhysicalCard darkCardToDuel = action.getPrimaryTargetCard(targetGroupId1);
                                                                final PhysicalCard lightCardToDuel = action.getPrimaryTargetCard(targetGroupId2);

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
                                                                                    public int getBaseNumDuelDestinyDraws(String playerIdForDestiny, DuelState duelState) {
                                                                                        return playerIdForDestiny.equals(playerId) ? 1 : 2;
                                                                                    }

                                                                                    @Override
                                                                                    public void performDuelDirections(final Action duelAction, SwccgGame game, final DuelState duelState) {
                                                                                        duelAction.appendEffect(
                                                                                                new AddUntilEndOfDuelModifierEffect(duelAction, new PowerModifier(self, Filters.and(Filters.or(darkCardToDuel, lightCardToDuel), Filters.armedWith(Filters.lightsaber)), 2), null));
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
                                                                                    public void performDuelResults(Action action, SwccgGame game, DuelState duelState) {
                                                                                        // Place losing character out of play
                                                                                        if (duelState.getLosingCharacter() != null) {
                                                                                            action.appendEffect(
                                                                                                    new PlaceCardOutOfPlayFromTableEffect(action, duelState.getLosingCharacter()));
                                                                                        }
                                                                                    }
                                                                                }
                                                                        )
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
                actions.add(action);
            }
        }
        return actions;
    }
}
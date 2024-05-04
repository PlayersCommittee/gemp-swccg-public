package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.AddBattleDestinyEffect;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.DuelDirections;
import com.gempukku.swccgo.logic.effects.DuelEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.evaluators.ConstantEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Lost
 * Title: Maul Strikes
 */
public class Card12_153 extends AbstractLostInterrupt {
    public Card12_153() {
        super(Side.DARK, 5, Title.Maul_Strikes, Uniqueness.UNRESTRICTED, ExpansionSet.CORUSCANT, Rarity.R);
        setLore("A Sith needs no introduction.");
        setGameText("If Maul in battle with a Jedi, add one battle destiny (two if Jedi is Qui-Gon). OR Use 1 Force to take any lightsaber into hand from Reserve Deck; reshuffle. OR Target a Jedi present with Maul; they duel: Both players draw 2 destiny. Loser (lowest total) is lost.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, PhysicalCard self) {
        List<PlayInterruptAction> actions = new LinkedList<PlayInterruptAction>();

        // Check condition(s)
        if (GameConditions.isDuringBattleWithParticipant(game, Filters.Maul)
                && GameConditions.canAddBattleDestinyDraws(game, self)) {
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Jedi, Filters.not(Filters.QuiGon)))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add one battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 1));
                            }
                        }
                );
                actions.add(action);
            }
            if (GameConditions.isDuringBattleWithParticipant(game, Filters.and(Filters.Jedi, Filters.QuiGon))) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Add two battle destiny");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                // Perform result(s)
                                action.appendEffect(
                                        new AddBattleDestinyEffect(action, 2));
                            }
                        }
                );
                actions.add(action);
            }
        }

        GameTextActionId gameTextActionId = GameTextActionId.MAUL_STRIKES__UPLOAD_LIGHTSABER;

        // Check condition(s)
        if (GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
            action.setText("Take lightsaber into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses("Take a lightsaber into hand from Reserve Deck",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.lightsaber, true));
                        }
                    }
            );
            actions.add(action);
        }

        // Check condition(s)
        final PhysicalCard maul = Filters.findFirstActive(game, self, Filters.Maul);
        if (maul != null) {
            Filter targetFilter = Filters.and(Filters.opponents(self), Filters.Jedi, Filters.presentWith(self, Filters.sameCardId(maul)));
            TargetingReason targetingReason = TargetingReason.TO_BE_DUELED;
            if (GameConditions.canTarget(game, self, targetingReason, targetFilter)) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self, gameTextActionId);
                action.setText("Initiate duel between Maul and a Jedi");
                // Choose target(s)
                action.appendTargeting(
                        new TargetCardOnTableEffect(action, playerId, "Choose Jedi", targetingReason, targetFilter) {
                            @Override
                            protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                                action.addAnimationGroup(maul, targetedCard);
                                // Allow response(s)
                                action.allowResponses("Initiate duel between " + GameUtils.getCardLink(maul) + " and " + GameUtils.getCardLink(targetedCard),
                                        new RespondablePlayCardEffect(action) {
                                            @Override
                                            protected void performActionResults(final Action targetingAction) {
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new DuelEffect(action, maul, targetedCard, new DuelDirections() {
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
                                                                return new ConstantEvaluator(0);
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
                                                            public void performDuelResults(Action duelAction, SwccgGame game, DuelState duelState) {
                                                                PhysicalCard losingCharacter = duelState.getLosingCharacter();
                                                                if (losingCharacter != null) {
                                                                    // Losing character is lost
                                                                    duelAction.appendEffect(
                                                                            new LoseCardFromTableEffect(duelAction, losingCharacter));
                                                                }
                                                            }
                                                        })
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
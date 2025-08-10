package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromHandAndLoseTheRestEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Emergency Deployment
 */
public class Card1_244 extends AbstractUsedInterrupt {
    public Card1_244() {
        super(Side.DARK, 3, Title.Emergency_Deployment, Uniqueness.UNRESTRICTED, ExpansionSet.PREMIERE, Rarity.U1);
        setLore("TIE fighters are stored in large racks far above the hangar deck. Catapulted into space by powerful tractor beam generators, TIEs can quickly scramble to engage the enemy.");
        setGameText("If opponent just initiated a battle where opponent has more than double your power, reveal up to 3 cards from your Reserve Deck. Of those 3, deploy anywhere (for free) any characters, starships, vehicles, devices or weapons. Any others are lost.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, final SwccgGame game, EffectResult effectResult, final PhysicalCard self) {
        String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (TriggerConditions.battleInitiated(game, effectResult, opponent)
                && GameConditions.hasReserveDeck(game, playerId)) {
            float playersPower = GameConditions.getBattlePower(game, playerId);
            float opponentsPower = GameConditions.getBattlePower(game, opponent);
            if ((2 * playersPower) < opponentsPower) {

                final PlayInterruptAction action = new PlayInterruptAction(game, self);
                action.setText("Draw cards from Reserve Deck");
                // Allow response(s)
                action.allowResponses(
                        new RespondablePlayCardEffect(action) {
                            @Override
                            protected void performActionResults(Action targetingAction) {
                                final int maxToDraw = Math.min(3, game.getGameState().getReserveDeckSize(playerId));
                                // Perform result(s)
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new IntegerAwaitingDecision("Choose number of cards to draw ", 1, maxToDraw, maxToDraw) {
                                                    @Override
                                                    public void decisionMade(final int numToDraw) throws DecisionResultInvalidException {
                                                        action.appendEffect(
                                                                new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, numToDraw) {
                                                                    @Override
                                                                    protected void cardsDrawnIntoHand(Collection<PhysicalCard> cards) {
                                                                        action.appendEffect(
                                                                                new DeployCardsFromHandAndLoseTheRestEffect(action, cards,
                                                                                        Filters.or(Filters.character, Filters.starship, Filters.vehicle, Filters.device, Filters.weapon), true));
                                                                    }
                                                                }
                                                        );
                                                    }
                                                }
                                        )
                                );
                            }
                        }
                );
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
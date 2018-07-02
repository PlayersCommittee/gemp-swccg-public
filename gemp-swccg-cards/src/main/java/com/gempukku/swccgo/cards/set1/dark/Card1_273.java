package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractUsedInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealUsedPileEffect;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseCardsFromUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RefreshPrintedDestinyValuesEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Used
 * Title: Trinto Duaba
 */
public class Card1_273 extends AbstractUsedInterrupt {
    public Card1_273() {
        super(Side.DARK, 5, "Trinto Duaba");
        setLore("A Stennes Shifter, a near-human race. Have ability to blend unnoticed into crowds. Trinto profits from turning lawbreakers over to Imperial authorities.");
        setGameText("If there are cards in the opponent's Used Pile during your battle phase, draw one destiny. All cards there with the same destiny number are lost.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, self, Phase.BATTLE)
                && GameConditions.hasUsedPile(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Draw destiny");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId, 1) {
                                        @Override
                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, final Float totalDestiny) {
                                            final GameState gameState = game.getGameState();
                                            if (totalDestiny == null) {
                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                return;
                                            }

                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                            action.appendEffect(
                                                    new RevealUsedPileEffect(action, opponent) {
                                                        @Override
                                                        protected void cardsRevealed(final List<PhysicalCard> revealedCards) {
                                                            action.appendEffect(
                                                                    new RefreshPrintedDestinyValuesEffect(action, revealedCards) {
                                                                        @Override
                                                                        protected void refreshedPrintedDestinyValues() {
                                                                            Collection<PhysicalCard> cardsToMakeLost = Filters.filter(revealedCards, game, Filters.destinyEqualTo(totalDestiny));
                                                                            if (!cardsToMakeLost.isEmpty()) {
                                                                                gameState.sendMessage("Result: Succeeded");
                                                                                action.appendEffect(
                                                                                        new LoseCardsFromUsedPileEffect(action, opponent, Filters.in(cardsToMakeLost)));
                                                                            } else {
                                                                                gameState.sendMessage("Result: No cards matching destiny number");
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
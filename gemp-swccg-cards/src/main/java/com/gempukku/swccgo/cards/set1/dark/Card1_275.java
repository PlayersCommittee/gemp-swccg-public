package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.AbstractLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealUsedPileEffect;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.effects.choose.StealCardsIntoUsedPileFromUsedPileEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Premiere
 * Type: Interrupt
 * Subtype: Lost
 * Title: Tusken Scavengers
 */
public class Card1_275 extends AbstractLostInterrupt {
    public Card1_275() {
        super(Side.DARK, 5, Title.Tusken_Scavengers);
        setLore("'Sand People always ride single file to hide their numbers.' They frequently attack strangers as they scavenge for useful equipment. Looted Luke's landspeeder.");
        setGameText("Use 1 Force to draw destiny. If destiny < the number of Tusken Raiders on table, you may scavenge (search through) the opponent's Used Pile. All vehicles, weapons or devices you find there are lost.");
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasUsedPile(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 1)
                && GameConditions.canSpot(game, self, Filters.Tusken_Raider)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Scavenge opponent's Used Pile");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new DrawDestinyEffect(action, playerId) {
                                        @Override
                                        protected void destinyDraws(final SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                                            final GameState gameState = game.getGameState();
                                            if (totalDestiny == null) {
                                                gameState.sendMessage("Result: Failed due to failed destiny draw");
                                                return;
                                            }

                                            int numberOfTuskenRaiders = Filters.countActive(game, self, Filters.Tusken_Raider);
                                            gameState.sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                                            gameState.sendMessage("Number of tusken raiders: " + numberOfTuskenRaiders);

                                            if (totalDestiny < numberOfTuskenRaiders) {
                                                gameState.sendMessage("Result: Succeeded");
                                                // Perform result(s)
                                                action.appendEffect(
                                                        new RevealUsedPileEffect(action, opponent) {
                                                            @Override
                                                            protected void cardsRevealed(List<PhysicalCard> revealedCards) {
                                                                final Collection<PhysicalCard> cardsFound = Filters.filter(revealedCards, game, Filters.or(Filters.vehicle, Filters.weapon, Filters.device));
                                                                if (!cardsFound.isEmpty()) {
                                                                    if (GameConditions.hasGameTextModification(game, self, ModifyGameTextType.TUSKEN_SCAVENGERS__MAY_STEAL_CARDS_FOUND)) {
                                                                        action.appendEffect(
                                                                                new PlayoutDecisionEffect(action, playerId,
                                                                                        new YesNoDecision("Do you want to steal vehicles, weapons, and devices found?") {
                                                                                            @Override
                                                                                            protected void yes() {
                                                                                                gameState.sendMessage(playerId + " chooses to steal vehicles, weapons, and devices found");
                                                                                                action.appendEffect(
                                                                                                        new StealCardsIntoUsedPileFromUsedPileEffect(action, playerId, Filters.in(cardsFound)));
                                                                                            }

                                                                                            @Override
                                                                                            protected void no() {
                                                                                                gameState.sendMessage(playerId + " chooses to not steal vehicles, weapons, and devices found");
                                                                                                action.appendEffect(
                                                                                                        new PutCardsFromUsedPileInLostPileEffect(action, opponent, Filters.in(cardsFound)));
                                                                                            }
                                                                                        }
                                                                                )
                                                                        );
                                                                    }
                                                                    else {
                                                                        action.appendEffect(
                                                                                new PutCardsFromUsedPileInLostPileEffect(action, opponent, Filters.in(cardsFound)));
                                                                    }
                                                                }
                                                            }
                                                        }
                                                );
                                            }
                                            else {
                                                gameState.sendMessage("Result: Failed");
                                            }
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
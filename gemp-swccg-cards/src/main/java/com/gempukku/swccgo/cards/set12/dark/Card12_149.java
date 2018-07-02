package com.gempukku.swccgo.cards.set12.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealOpponentsHandEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.decisions.UsedOrLostDecision;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Set: Coruscant
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Kill Them Immediately
 */
public class Card12_149 extends AbstractUsedOrLostInterrupt {
    public Card12_149() {
        super(Side.DARK, 3, "Kill Them Immediately", Uniqueness.UNIQUE);
        setLore("Darth Sidious instructed Nute Gunray to dispose of the Jedi ambassadors. Rune Haako was not so confident.");
        setGameText("Use 5 Force to reveal opponent's hand. If opponent has no duplicate cards there, place this Interrupt in Used Pile. Otherwise USED: place a revealed card in opponent's Used Pile. LOST: all cards opponent has two or more of are lost. (Immune to Sense.)");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasHand(game, opponent)
                && GameConditions.canUseForceToPlayInterrupt(game, playerId, self, 5)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self);
            action.setText("Reveal opponent's hand");
            action.setImmuneTo(Title.Sense);
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 5));
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RevealOpponentsHandEffect(action, playerId) {
                                        @Override
                                        protected void cardsRevealed(final List<PhysicalCard> revealedCards) {
                                            final GameState gameState = game.getGameState();
                                            if (Filters.canSpot(revealedCards, game, Filters.duplicatesOfInHand(opponent))) {
                                                action.appendEffect(
                                                        new PlayoutDecisionEffect(action, playerId,
                                                                new UsedOrLostDecision("Choose USED or LOST for this Interrupt") {
                                                                    @Override
                                                                    protected void typeChosen(CardSubtype subtype) {
                                                                        action.setPlayedAsSubtype(subtype);
                                                                        gameState.sendMessage(playerId + " chooses to play " + GameUtils.getCardLink(self) + " as " + subtype.getHumanReadable() + " Interrupt");
                                                                        if (subtype == CardSubtype.USED) {
                                                                            action.appendEffect(
                                                                                    new PutCardFromHandOnUsedPileEffect(action, playerId, opponent, Filters.in(revealedCards), false));
                                                                        }
                                                                        else {
                                                                            Collection<PhysicalCard> duplicateCards = Filters.filter(revealedCards, game, Filters.duplicatesOfInHand(opponent));
                                                                            action.appendEffect(
                                                                                    new LoseCardsFromHandEffect(action, opponent, duplicateCards));
                                                                        }
                                                                     }
                                                                }
                                                        )
                                                );
                                            }
                                            else {
                                                gameState.sendMessage("No duplicate cards found");
                                                action.appendEffect(
                                                        new PutCardFromVoidInUsedPileEffect(action, playerId, self));
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
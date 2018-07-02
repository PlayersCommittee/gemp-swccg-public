package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.AbstractUsedOrLostInterrupt;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.RevealTopCardOfCardPilesEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.*;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.*;


/**
 * Set: Dagobah
 * Type: Interrupt
 * Subtype: Used Or Lost
 * Title: Close Call
 */
public class Card4_138 extends AbstractUsedOrLostInterrupt {
    public Card4_138() {
        super(Side.DARK, 2, Title.Close_Call, Uniqueness.UNIQUE);
        setLore("If this little one doesn't pulverize you, the next one just might.");
        setGameText("USED: Cancel one asteroid destiny and cause it to be drawn again. LOST: Lose 1 Force. Opponent must reveal the top card of Reserve Deck, Force Pile and Used Pile. Card(s) with lowest destiny number greater than zero are lost.");
        addIcons(Icon.DAGOBAH);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self) {
        // Check condition(s)
        if (TriggerConditions.isAsteroidDestinyJustDrawn(game, effectResult)
                && GameConditions.canCancelDestinyAndCauseRedraw(game, playerId)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.USED);
            action.setText("Cancel destiny and cause re-draw");
            // Allow response(s)
            action.allowResponses(
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new CancelDestinyAndCauseRedrawEffect(action));
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self) {
        final String opponent = game.getOpponent(playerId);

        // Check condition(s)
        if (GameConditions.hasReserveDeck(game, opponent)
                && GameConditions.hasForcePile(game, opponent)
                && GameConditions.hasUsedPile(game, opponent)) {

            final PlayInterruptAction action = new PlayInterruptAction(game, self, CardSubtype.LOST);
            action.setText("Reveal top of card piles");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, 1, true));
            // Allow response(s)
            action.allowResponses("Reveal top card of opponent's Reserve Deck, Force Pile, and Used Pile",
                    new RespondablePlayCardEffect(action) {
                        @Override
                        protected void performActionResults(Action targetingAction) {
                            // Perform result(s)
                            action.appendEffect(
                                    new RevealTopCardOfCardPilesEffect(action, playerId, opponent, Arrays.asList(Zone.RESERVE_DECK, Zone.FORCE_PILE, Zone.USED_PILE)) {
                                        @Override
                                        protected void cardsRevealed(final List<PhysicalCard> revealedCards) {
                                            action.appendEffect(
                                                    new RefreshPrintedDestinyValuesEffect(action, revealedCards) {
                                                        @Override
                                                        protected void refreshedPrintedDestinyValues() {
                                                            GameState gameState = game.getGameState();
                                                            ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

                                                            final Map<PhysicalCard, Float> allCardsRevealed = new HashMap<PhysicalCard, Float>();
                                                            for (PhysicalCard revealedCard : revealedCards) {
                                                                allCardsRevealed.put(revealedCard, modifiersQuerying.getDestiny(gameState, revealedCard));
                                                            }
                                                            float lowestNonZeroDestiny = Float.MAX_VALUE;
                                                            for (PhysicalCard card : allCardsRevealed.keySet()) {
                                                                float destiny = allCardsRevealed.get(card);
                                                                if (destiny > 0) {
                                                                    lowestNonZeroDestiny = Math.min(lowestNonZeroDestiny, destiny);
                                                                }
                                                            }
                                                            List<PhysicalCard> cardsToLose = new LinkedList<PhysicalCard>();
                                                            for (PhysicalCard card : allCardsRevealed.keySet()) {
                                                                float destiny = allCardsRevealed.get(card);
                                                                if (destiny == lowestNonZeroDestiny) {
                                                                    cardsToLose.add(card);
                                                                }
                                                            }
                                                            if (!cardsToLose.isEmpty()) {
                                                                action.appendEffect(
                                                                        new LoseCardsFromCardPilesEffect(action, opponent, cardsToLose));
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
            return Collections.singletonList(action);
        }
        return null;
    }
}
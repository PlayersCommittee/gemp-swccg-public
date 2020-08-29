package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.TriggeringResultEffect;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInOwnCardPileResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An effect for peeking at the bottom cards of a specified card pile.
 */
public class PeekAtBottomCardsOfCardPileEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private String _cardPileOwner;
    private Zone _cardPile;
    private int _count;

    /**
     * Creates an effect for peeking at the bottom cards of a specified card pile.
     *
     * @param action        the action performing this effect
     * @param playerId      the player to peek at cards
     * @param cardPileOwner the owner of the card pile
     * @param cardPile      the card pile
     * @param count         the number of cards to peek at
     */
    public PeekAtBottomCardsOfCardPileEffect(Action action, String playerId, String cardPileOwner, Zone cardPile, int count) {
        super(action);
        _playerId = playerId;
        _cardPileOwner = cardPileOwner;
        _cardPile = cardPile;
        _count = count;
    }

    @Override
    protected void doPlayEffect(final SwccgGame game) {
        GameState gameState = game.getGameState();
        String cardPileText = (_playerId.equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s ")) + _cardPile.getHumanReadable();

        List<PhysicalCard> deck = gameState.getCardPile(_cardPileOwner, _cardPile);
        Collections.reverse(deck);
        int count = Math.min(deck.size(), _count);
        final List<PhysicalCard> bottomCards = new LinkedList<PhysicalCard>(deck.subList(0, count));
        if (!bottomCards.isEmpty()) {

            if (bottomCards.size() == 1)
                gameState.sendMessage(_playerId + " peeks at the bottom card of " + cardPileText);
            else
                gameState.sendMessage(_playerId + " peeks at the bottom " + bottomCards.size() + " cards of " + cardPileText);

            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new ArbitraryCardsSelectionDecision("Bottom card" + GameUtils.s(bottomCards) + " of " + cardPileText, bottomCards, Collections.<PhysicalCard>emptyList(), 0, 0) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            cardsPeekedAt(bottomCards);

                            // Check if player looked at cards in own card pile
                            if (_cardPileOwner.equals(_playerId)) {
                                _action.appendAfterEffect(new TriggeringResultEffect(_action, new LookedAtCardsInOwnCardPileResult(_cardPileOwner, _cardPile)));
                            }
                        }
                    });
        } else {
            gameState.sendMessage(cardPileText + " is empty");
        }
    }

    /**
     * A callback method for the cards peeked at.
     *
     * @param peekedAtCards the cards peeked at
     */
    protected void cardsPeekedAt(List<PhysicalCard> peekedAtCards) {
    }
}

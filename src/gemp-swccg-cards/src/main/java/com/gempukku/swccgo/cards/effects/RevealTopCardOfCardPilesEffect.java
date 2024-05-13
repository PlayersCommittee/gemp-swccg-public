package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.LookedAtCardsInCardPileResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An effect for revealing the top card of a specified card piles.
 */
public class RevealTopCardOfCardPilesEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private String _cardPileOwner;
    private List<Zone> _cardPiles;

    /**
     * Creates an effect for revealing the top card of a specified card piles.
     * @param action the action performing this effect
     * @param playerId the player to reveal the cards
     * @param cardPileOwner the owner of the card pile
     * @param cardPiles the card piles
     */
    public RevealTopCardOfCardPilesEffect(Action action, String playerId, String cardPileOwner, List<Zone> cardPiles) {
        super(action);
        _playerId = playerId;
        _cardPileOwner = cardPileOwner;
        _cardPiles = cardPiles;
    }

    @Override
    protected void doPlayEffect(final SwccgGame game) {
        GameState gameState = game.getGameState();

        final Map<PhysicalCard, String> topCardsMap = new HashMap<PhysicalCard, String>();
        final List<PhysicalCard> topCards = new ArrayList<PhysicalCard>();
        for (Zone cardPile : _cardPiles) {
            PhysicalCard topCard = gameState.getTopOfCardPile(_cardPileOwner, cardPile);
            if (topCard != null) {
                topCardsMap.put(topCard, cardPile.getHumanReadable());
                topCards.add(topCard);
            }
        }

        if (!topCardsMap.isEmpty()) {

            StringBuilder cardPileText = new StringBuilder(_playerId.equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s "));
            for (int i=0; i<_cardPiles.size(); ++i) {
                Zone cardPile = _cardPiles.get(i);
                if (i == 0) {
                    cardPileText.append(cardPile.getHumanReadable());
                }
                else if (i == _cardPiles.size()-1) {
                    if (_cardPiles.size() > 2) {
                        cardPileText.append(",");
                    }
                    cardPileText.append(" and").append(cardPile.getHumanReadable());
                }
                else {
                    cardPileText.append(", ").append(cardPile.getHumanReadable());
                }
            }
            gameState.sendMessage(_playerId + " reveals the top card of " + cardPileText);

            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new ArbitraryCardsSelectionDecision("Top card" + GameUtils.s(topCards) + " of " + cardPileText, topCards, Collections.<PhysicalCard>emptyList(), 0, 0, topCardsMap) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            cardsRevealed(topCards);

                            for (Zone cardPile : _cardPiles) {
                                game.getActionsEnvironment().emitEffectResult(new LookedAtCardsInCardPileResult(_playerId, _cardPileOwner, cardPile, _action.getActionSource()));
                            }
                        }
                    });
            game.getUserFeedback().sendAwaitingDecision(game.getOpponent(_playerId),
                    new ArbitraryCardsSelectionDecision("Top card" + GameUtils.s(topCards) + " of " + cardPileText, topCards, Collections.<PhysicalCard>emptyList(), 0, 0, topCardsMap) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                        }
                    });
        }
    }

    /**
     * A callback method for the cards revealed.
     * @param cards the cards revealed
     */
    protected void cardsRevealed(List<PhysicalCard> cards) {
    }
}

package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.List;

/**
 * An effect that causes the specified player to look at the cards in the specified card pile.
 */
class LookAtCardPileEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private String _cardPileOwner;
    private Zone _cardPile;

    /**
     * Creates an effect that causes the specified player to look at the cards in the specified card pile.
     * @param action the action performing this effect
     * @param playerId the performing player
     * @param cardPileOwner the card pile owner
     * @param cardPile the card pile
     */
    protected LookAtCardPileEffect(Action action, String playerId, String cardPileOwner, Zone cardPile) {
        super(action);
        _playerId = playerId;
        _cardPileOwner = cardPileOwner;
        _cardPile = cardPile;
    }

    public String getShownText() {
        return _cardPileOwner + "'s " + _cardPile.getHumanReadable();
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        final List<PhysicalCard> cardsInZone = gameState.getCardPile(_cardPileOwner, _cardPile);
        gameState.sendMessage(_playerId + " looks at " + _cardPileOwner + "'s " + _cardPile.getHumanReadable());

        game.getUserFeedback().sendAwaitingDecision(_playerId,
                new ArbitraryCardsSelectionDecision(getShownText(), cardsInZone, Collections.<PhysicalCard>emptyList(), 0, 0) {
                    @Override
                    public void decisionMade(String result) throws DecisionResultInvalidException {
                        cardsInCardPile(cardsInZone);
                    }
                });
    }

    /**
     * A callback method for the cards in card pile.
     * @param cardsInCardPile the cards in card pile
     */
    protected void cardsInCardPile(List<PhysicalCard> cardsInCardPile) {
    }
}

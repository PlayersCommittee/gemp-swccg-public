package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that reveals a card from own hand to opponent.
 */
public class RevealCardFromOwnHandEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private PhysicalCard _cardToReveal;

    /**
     * Creates an effect that reveals a card from own hand to opponent.
     * @param action the action performing this effect
     * @param playerId the player revealing card to opponent
     */
    public RevealCardFromOwnHandEffect(Action action, String playerId, PhysicalCard cardToReveal) {
        super(action);
        _playerId = playerId;
        _cardToReveal = cardToReveal;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (Filters.inHand(_playerId).accepts(game, _cardToReveal)) {
            game.getGameState().sendMessage(_playerId + " reveals " + GameUtils.getCardLink(_cardToReveal) + " from hand");

            game.getUserFeedback().sendAwaitingDecision(game.getOpponent(_playerId),
                    new ArbitraryCardsSelectionDecision("Revealed card", Collections.singleton(_cardToReveal), Collections.<PhysicalCard>emptyList(), 0, 0) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            cardRevealed(_cardToReveal);
                        }
                    }
            );
        }
    }

    /**
     * A callback method for the card revealed.
     * @param cardRevealed the card revealed
     */
    protected void cardRevealed(PhysicalCard cardRevealed) {
    }
}

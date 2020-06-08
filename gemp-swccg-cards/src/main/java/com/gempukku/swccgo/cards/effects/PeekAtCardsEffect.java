package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collection;
import java.util.Collections;

/**
 * An effect for peeking at specified cards (face down on table or stacked).
 */
public class PeekAtCardsEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private Collection<PhysicalCard> _cards;

    /**
     * Creates an effect for peeking at specified cards (face down on table or stacked).
     * @param action the action performing this effect
     * @param playerId the player to peek
     * @param cards the cards to peek at
     */
    public PeekAtCardsEffect(Action action, String playerId, Collection<PhysicalCard> cards) {
        super(action);
        _playerId = playerId;
        _cards = cards;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        final GameState gameState = game.getGameState();

        if (!_cards.isEmpty()) {
            if (_cards.size() == 1)
                gameState.sendMessage(_playerId + " peeks at a face-down card");
            else
                gameState.sendMessage(_playerId + " peeks at " + _cards.size() + " face-down cards");

            game.getUserFeedback().sendAwaitingDecision(_playerId,
                    new ArbitraryCardsSelectionDecision("Face-down card" + GameUtils.s(_cards), _cards, Collections.<PhysicalCard>emptyList(), 0, 0) {
                        @Override
                        public void decisionMade(String result) throws DecisionResultInvalidException {
                            gameState.cardAffectsCards(_playerId, _action.getActionSource(), _cards);
                        }
                    });
        }
    }
}

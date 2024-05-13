package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ShufflingResult;

import java.util.ArrayList;
import java.util.List;

/**
 * An effect that shuffles the specified player's hand and Used Pile into Reserve Deck.
 */
public class ShuffleHandAndUsedPileIntoReserveDeckEffect extends AbstractSuccessfulEffect {
    private String _playerId;

    /**
     * Creates an effect that shuffles the specified player's hand and Used Pile into Reserve Deck.
     * @param action the action performing this effect
     * @param playerId the player
     */
    public ShuffleHandAndUsedPileIntoReserveDeckEffect(Action action, String playerId) {
        super(action);
        _playerId = playerId;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        List<PhysicalCard> cardsToShuffleIntoReserveDeck = new ArrayList<PhysicalCard>(gameState.getHand(_playerId));
        cardsToShuffleIntoReserveDeck.addAll(gameState.getUsedPile(_playerId));
        if (!cardsToShuffleIntoReserveDeck.isEmpty()) {

            gameState.sendMessage(_playerId + " shuffles hand and Used Pile into Reserve Deck");
            gameState.removeCardsFromZone(cardsToShuffleIntoReserveDeck);
            gameState.shuffleCardsIntoPile(cardsToShuffleIntoReserveDeck, _playerId, Zone.RESERVE_DECK);
            game.getActionsEnvironment().emitEffectResult(new ShufflingResult(_action.getActionSource(), _playerId, _playerId, Zone.RESERVE_DECK, true));
        }
    }
}

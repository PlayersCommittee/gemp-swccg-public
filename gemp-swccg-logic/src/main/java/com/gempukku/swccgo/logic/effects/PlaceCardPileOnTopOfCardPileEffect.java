package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.PutCardInCardPileFromOffTableResult;

/**
 * An effect to place a card pile on top of another card pile.
 */
class PlaceCardPileOnTopOfCardPileEffect extends AbstractSuccessfulEffect {
    private String _cardPileOwner;
    private Zone _fromPile;
    private Zone _toPile;

    /**
     * Creates an effect to place a card pile on top of another card pile.
     * @param action the action performing this effect
     * @param cardPileOwner the owner of the card piles
     * @param fromPile the card pile to place on the other card pile
     * @param toPile the card pile to move the cards to
     */
    protected PlaceCardPileOnTopOfCardPileEffect(Action action, String cardPileOwner, Zone fromPile, Zone toPile) {
        super(action);
        _cardPileOwner = cardPileOwner;
        _fromPile = fromPile;
        _toPile = toPile;
    }

    @Override
    public String getText(SwccgGame game) {
        return "Place " + _fromPile.getHumanReadable() + " on " + _toPile.getHumanReadable();
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        if (gameState.getCardPile(_cardPileOwner, _fromPile).isEmpty())
            return;

        String playerNameForMsg = _action.getPerformingPlayer().equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s ");
        gameState.sendMessage(_action.getPerformingPlayer() + " places " + playerNameForMsg + _fromPile.getHumanReadable() + " on " + playerNameForMsg + _toPile.getHumanReadable());
        gameState.placeCardPileOnCardPile(_cardPileOwner, _fromPile, _toPile);

        game.getActionsEnvironment().emitEffectResult(
                new PutCardInCardPileFromOffTableResult(_action, null, _cardPileOwner, _toPile, false));
    }
}

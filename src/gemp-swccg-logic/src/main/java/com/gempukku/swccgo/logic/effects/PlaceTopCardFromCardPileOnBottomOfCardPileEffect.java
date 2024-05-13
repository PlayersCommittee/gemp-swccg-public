package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.PutCardInCardPileFromOffTableResult;

import java.util.Collections;

/**
 * An effect to place the top card of a card pile on bottom of a card pile.
 */
public class PlaceTopCardFromCardPileOnBottomOfCardPileEffect extends AbstractSuccessfulEffect {
    private String _cardPileOwner;
    private Zone _fromPile;
    private Zone _toPile;

    /**
     * Creates an effect to place the top card of a card pile on bottom of a card pile.
     * @param action the action performing this effect
     * @param cardPileOwner the owner of the card piles
     * @param fromPile the card pile to take the card from
     * @param toPile the card pile to move the card to
     */
    public PlaceTopCardFromCardPileOnBottomOfCardPileEffect(Action action, String cardPileOwner, Zone fromPile, Zone toPile) {
        super(action);
        _cardPileOwner = cardPileOwner;
        _fromPile = fromPile;
        _toPile = toPile;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        PhysicalCard card = gameState.getTopOfCardPile(_cardPileOwner, _fromPile);
        if (card == null)
            return;

        boolean hidden = (!game.getGameState().isCardPileFaceUp(_cardPileOwner, _fromPile) && !game.getGameState().isCardPileFaceUp(_cardPileOwner, _toPile));

        String cardInfo = hidden ? "a card" : GameUtils.getCardLink(card);
        String playerNameForMsg = _action.getPerformingPlayer().equals(_cardPileOwner) ? "" : (_cardPileOwner + "'s ");
        gameState.sendMessage(_action.getPerformingPlayer() + " places " + cardInfo + " from the top of " + playerNameForMsg + _fromPile.getHumanReadable() + " to the bottom of " + playerNameForMsg + _toPile.getHumanReadable());
        gameState.removeCardsFromZone(Collections.singleton(card));
        gameState.addCardToZone(card, _toPile, _cardPileOwner);

        game.getActionsEnvironment().emitEffectResult(
                new PutCardInCardPileFromOffTableResult(_action, card, _cardPileOwner, _toPile, false));
    }
}

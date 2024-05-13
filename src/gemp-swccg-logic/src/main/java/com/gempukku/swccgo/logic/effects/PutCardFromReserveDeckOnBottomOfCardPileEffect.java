package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.PutCardInCardPileFromOffTableResult;

import java.util.Collections;

public class PutCardFromReserveDeckOnBottomOfCardPileEffect extends AbstractStandardEffect {
    private PhysicalCard _card;
    private Zone _cardPile;
    private boolean _hidden;
    private String _cardPileOwner;

    // TODO: Update these effects to be as generic as needed

    public PutCardFromReserveDeckOnBottomOfCardPileEffect(Action action, PhysicalCard card, Zone cardPile, boolean hidden) {
        super(action);
        _card = card;
        _cardPile = cardPile;
        _hidden = hidden;
        _cardPileOwner = card.getOwner();
    }

    public PutCardFromReserveDeckOnBottomOfCardPileEffect(Action action, PhysicalCard card, String cardPileOwner, Zone cardPile, boolean hidden) {
        super(action);
        _card = card;
        _cardPile = cardPile;
        _hidden = hidden;
        _cardPileOwner = cardPileOwner;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return GameUtils.getZoneFromZoneTop(_card.getZone()) == Zone.RESERVE_DECK;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (!isPlayableInFull(game))
            return new FullEffectResult(false);

        if (_hidden) {
            _hidden = !game.getGameState().isCardPileFaceUp(_card.getZoneOwner(), _cardPile)
                    || (game.getGameState().getCardPile(_card.getZoneOwner(), _cardPile).size() > 1);
        }

        String cardInfo = _hidden ? "a card" : GameUtils.getCardLink(_card);
        GameState gameState = game.getGameState();
        gameState.sendMessage(_action.getPerformingPlayer() + " puts " + cardInfo + " from Reserve Deck to the bottom of " + _cardPile.getHumanReadable());
        gameState.removeCardsFromZone(Collections.singleton(_card));
        gameState.addCardToZone(_card, _cardPile, _cardPileOwner);

        game.getActionsEnvironment().emitEffectResult(
                new PutCardInCardPileFromOffTableResult(_action, _card, _cardPileOwner, _cardPile, false));

        return new FullEffectResult(true);
    }
}

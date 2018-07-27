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

public class PutCardFromLostPileOnTopOfCardPileEffect extends AbstractStandardEffect {
    private PhysicalCard _card;
    private Zone _cardPile;
    private boolean _hidden;

    // TODO: Update these effects to be as generic as needed

    public PutCardFromLostPileOnTopOfCardPileEffect(Action action, PhysicalCard card, Zone cardPile, boolean hidden) {
        super(action);
        _card = card;
        _cardPile = cardPile;
        _hidden = hidden;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return GameUtils.getZoneFromZoneTop(_card.getZone()) == Zone.LOST_PILE;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (!isPlayableInFull(game))
            return new FullEffectResult(false);

        if (_hidden) {
            _hidden = !game.getGameState().isCardPileFaceUp(_card.getZoneOwner(), _cardPile);
        }

        String cardInfo = _hidden ? "a card" : GameUtils.getCardLink(_card);
        String message = ((_action.getPerformingPlayer() != null) ? _action.getPerformingPlayer() : _card.getOwner());
        if (_cardPile == Zone.LOST_PILE) {
            message += " moves " + cardInfo + " to ";
        }
        else {
            message += " puts " + cardInfo + " from Lost Pile on ";
        }
        message += "top of " + _cardPile.getHumanReadable();

        GameState gameState = game.getGameState();
        gameState.sendMessage(message);
        gameState.removeCardsFromZone(Collections.singleton(_card));
        gameState.addCardToTopOfZone(_card, _cardPile, _card.getOwner());

        game.getActionsEnvironment().emitEffectResult(
                new PutCardInCardPileFromOffTableResult(_action, _card, _card.getZoneOwner(), _cardPile, false));

        return new FullEffectResult(true);
    }
}

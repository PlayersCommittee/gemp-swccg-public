package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

public class DrawOneCardFromReserveDeckEffect extends AbstractStandardEffect {
    private String _playerId;

    public DrawOneCardFromReserveDeckEffect(Action action, String playerId) {
        super(action);
        _playerId = playerId;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return !game.getGameState().isZoneEmpty(_playerId, Zone.RESERVE_DECK);
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (!isPlayableInFull(game))
            return new FullEffectResult(false);

        GameState gameState = game.getGameState();
        PhysicalCard card = gameState.getTopOfCardPile(_playerId, Zone.RESERVE_DECK);
        gameState.removeCardsFromZone(Collections.singleton(card));
        gameState.addCardToZone(card, Zone.HAND, _playerId);

        return new FullEffectResult(true);
    }
}

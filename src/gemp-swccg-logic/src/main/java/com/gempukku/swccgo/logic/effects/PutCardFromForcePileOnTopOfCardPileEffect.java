package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

public class PutCardFromForcePileOnTopOfCardPileEffect extends AbstractStandardEffect {
    private String _playerId;
    private PhysicalCard _card;
    private Zone _cardPile;
    private boolean _hidden;

    public PutCardFromForcePileOnTopOfCardPileEffect(Action action, String playerId, PhysicalCard card, Zone cardPile, boolean hidden) {
        super(action);
        _playerId = playerId;
        _card = card;
        _cardPile = cardPile;
        _hidden = hidden;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return GameUtils.getZoneFromZoneTop(_card.getZone()) == Zone.FORCE_PILE;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (!isPlayableInFull(game))
            return new FullEffectResult(false);

        String cardInfo = _hidden ? "a card" : GameUtils.getCardLink(_card);
        String playerNameForMsg = _playerId.equals(_card.getZoneOwner()) ? "" : _card.getZoneOwner() + "'s ";
        GameState gameState = game.getGameState();
        gameState.sendMessage(_playerId + " puts " + cardInfo + " from " + playerNameForMsg + "Force Pile to the top of " + _cardPile.getHumanReadable());
        gameState.removeCardsFromZone(Collections.singleton(_card));
        gameState.addCardToTopOfZone(_card, _cardPile, _card.getOwner());

        return new FullEffectResult(true);
    }
}

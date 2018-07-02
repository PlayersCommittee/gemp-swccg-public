package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

public class PutCardFromCardPileOnBottomOfCardPileEffect extends AbstractStandardEffect {
    private String _playerId;
    private PhysicalCard _card;
    private boolean _hidden;

    public PutCardFromCardPileOnBottomOfCardPileEffect(Action action, String playerId, PhysicalCard card, boolean hidden) {
        super(action);
        _playerId = playerId;
        _card = card;
        _hidden = hidden;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        Zone cardPile = GameUtils.getZoneFromZoneTop(_card.getZone());
        return (cardPile == Zone.RESERVE_DECK || cardPile == Zone.FORCE_PILE || cardPile == Zone.USED_PILE || cardPile == Zone.LOST_PILE);
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (!isPlayableInFull(game))
            return new FullEffectResult(false);

        String cardInfo = _hidden ? "a card" : GameUtils.getCardLink(_card);
        Zone cardPile = GameUtils.getZoneFromZoneTop(_card.getZone());
        String playerNameForMsg = _playerId.equals(_card.getZoneOwner()) ? "" : _card.getZoneOwner() + "'s ";
        GameState gameState = game.getGameState();
        gameState.sendMessage(_playerId + " puts " + cardInfo + " from " + playerNameForMsg + cardPile.getHumanReadable() + " to the bottom of " + cardPile.getHumanReadable());
        gameState.removeCardsFromZone(Collections.singleton(_card));
        gameState.addCardToZone(_card, cardPile, _card.getOwner());

        return new FullEffectResult(true);
    }
}

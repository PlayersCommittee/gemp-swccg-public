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
    private Zone _newZonePlacement;

    public PutCardFromCardPileOnBottomOfCardPileEffect(Action action, String playerId, PhysicalCard card, Zone newZonePlacement, boolean hidden) {
        super(action);
        _playerId = playerId;
        _card = card;
        _hidden = hidden;
        _newZonePlacement = newZonePlacement;
    }

    public PutCardFromCardPileOnBottomOfCardPileEffect(Action action, String playerId, PhysicalCard card, boolean hidden) {
        this(action, playerId, card, card.getZone(), hidden);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return (_newZonePlacement == Zone.RESERVE_DECK || _newZonePlacement == Zone.FORCE_PILE || _newZonePlacement == Zone.USED_PILE || _newZonePlacement == Zone.LOST_PILE);
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (!isPlayableInFull(game))
            return new FullEffectResult(false);

        String cardInfo = _hidden ? "a card" : GameUtils.getCardLink(_card);
        Zone cardPile = GameUtils.getZoneFromZoneTop(_card.getZone());
        String playerNameForMsg = _playerId.equals(_card.getZoneOwner()) ? "" : _card.getZoneOwner() + "'s ";
        GameState gameState = game.getGameState();
        gameState.sendMessage(_playerId + " puts " + cardInfo + " from " + playerNameForMsg + cardPile.getHumanReadable() + " to the bottom of " + _newZonePlacement.getHumanReadable());
        gameState.removeCardsFromZone(Collections.singleton(_card));
        gameState.addCardToZone(_card, _newZonePlacement, _card.getOwner());

        return new FullEffectResult(true);
    }
}

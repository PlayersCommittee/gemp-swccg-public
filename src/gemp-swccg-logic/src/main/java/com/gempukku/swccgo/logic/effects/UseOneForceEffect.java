package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.UseForceResult;

import java.util.Collections;

public class UseOneForceEffect extends AbstractStandardEffect {
    private String _playerId;
    private boolean _firstUsed;
    private boolean _lastUsed;
    private PhysicalCard _card;
    private boolean _revealCard;
    private boolean _allowFrozen;

    public UseOneForceEffect(Action action, String playerId, boolean revealCard) {
        this(action, playerId, true, true, revealCard, false);
    }

    public UseOneForceEffect(Action action, String playerId, boolean firstUsed, boolean lastUsed) {
        this(action, playerId, firstUsed, lastUsed, false, false);
    }

    public UseOneForceEffect(Action action, String playerId, boolean firstUsed, boolean lastUsed, boolean allowFrozen) {
        this(action, playerId, firstUsed, lastUsed, false, allowFrozen);
    }

    private UseOneForceEffect(Action action, String playerId, boolean firstUsed, boolean lastUsed, boolean revealCard, boolean allowFrozen) {
        super(action);
        _playerId = playerId;
        _firstUsed = firstUsed;
        _lastUsed = lastUsed;
        _revealCard = revealCard;
        _allowFrozen = allowFrozen;
    }

    public PhysicalCard getCard() {
        return _card;
    }

    public String getPlayerId() {
        return _playerId;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return game.getModifiersQuerying().getForceAvailableToUse(game.getGameState(), _playerId)>0
                || (_allowFrozen && game.getGameState().getFrozenPileSize(_playerId) > 0);
    }

    public boolean canUseForce(SwccgGame game) {
        if (!game.getGameState().getForcePile(_playerId).isEmpty())
            return true;
        return _allowFrozen && game.getGameState().getFrozenPileSize(_playerId) > 0;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (canUseForce(game)) {
            if (!game.getGameState().getForcePile(_playerId).isEmpty()) {
                _card = game.getGameState().getTopOfForcePile(_playerId);
                game.getGameState().playerUsesForce(_playerId, _firstUsed, _lastUsed);
            } else {
                // Beggar exception: use top of Frozen Pile as Force
                _card = game.getGameState().getTopOfFrozenPile(_playerId);
                game.getGameState().removeCardsFromZone(Collections.singleton(_card), !_firstUsed, !_lastUsed);
                game.getGameState().addCardToTopOfZone(_card, Zone.USED_PILE, _playerId, true, !_firstUsed, !_lastUsed);
            }
            if (_revealCard) {
                game.getGameState().sendMessage(_playerId + " uses 1 Force - " + GameUtils.getCardLink(_card));
            }
            forceUsed(_card);
            game.getActionsEnvironment().emitEffectResult(new UseForceResult(_playerId));
            return new FullEffectResult(true);
        }

        return new FullEffectResult(false);
    }

    protected void forceUsed(PhysicalCard card) {
    }
}

package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.UseForceResult;

public class UseOneForceEffect extends AbstractStandardEffect {
    private String _playerId;
    private boolean _firstUsed;
    private boolean _lastUsed;
    private PhysicalCard _card;
    private boolean _revealCard;

    public UseOneForceEffect(Action action, String playerId, boolean revealCard) {
        this(action, playerId, true, true, revealCard);
    }

    public UseOneForceEffect(Action action, String playerId, boolean firstUsed, boolean lastUsed) {
        this(action, playerId, firstUsed, lastUsed, false);
    }

    private UseOneForceEffect(Action action, String playerId, boolean firstUsed, boolean lastUsed, boolean revealCard) {
        super(action);
        _playerId = playerId;
        _firstUsed = firstUsed;
        _lastUsed = lastUsed;
        _revealCard = revealCard;
    }

    public PhysicalCard getCard() {
        return _card;
    }

    public String getPlayerId() {
        return _playerId;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return game.getModifiersQuerying().getForceAvailableToUse(game.getGameState(), _playerId)>0;
    }

    public boolean canUseForce(SwccgGame game) {
        return !game.getGameState().getForcePile(_playerId).isEmpty();
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (canUseForce(game)) {
            _card = game.getGameState().getTopOfForcePile(_playerId);
            game.getGameState().playerUsesForce(_playerId, _firstUsed, _lastUsed);
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

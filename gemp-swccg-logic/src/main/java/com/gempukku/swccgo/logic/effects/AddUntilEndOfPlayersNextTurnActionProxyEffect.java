package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.ActionProxy;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

public class AddUntilEndOfPlayersNextTurnActionProxyEffect extends PassthruEffect {
    private ActionProxy _actionProxy;
    private String _playerId;

    public AddUntilEndOfPlayersNextTurnActionProxyEffect(Action action, ActionProxy actionProxy, String playerId) {
        super(action);
        _actionProxy = actionProxy;
        _playerId = playerId;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        game.getActionsEnvironment().addUntilEndOfPlayersNextTurnActionProxy(_actionProxy, _playerId);
    }
}
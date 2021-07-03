package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.RecirculateResult;

public class RecirculateEffect extends AbstractStandardEffect {
    private String _player;

    public RecirculateEffect(Action action, String player) {
        super(action);
        _player = player;
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return true;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        GameState gameState = game.getGameState();
        if (!gameState.getUsedPile(_player).isEmpty()) {
            gameState.sendMessage(_player + " re-circulates");
            gameState.recirculate(_player);
            game.getActionsEnvironment().emitEffectResult(new RecirculateResult(_action.getPerformingPlayer()));
            return new FullEffectResult(true);
        }
        return new FullEffectResult(false);
    }
}

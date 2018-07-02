package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForceDrainState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that cancels the current Force drain.
 */
public class CancelForceDrainEffect extends AbstractStandardEffect {

    /**
     * Creates an effect that cancels the current Force drain.
     * @param action the action performing this effect
     */
    public CancelForceDrainEffect(Action action) {
        super(action);
    }

    @Override
    public boolean isPlayableInFull(SwccgGame game) {
        return canPerformingPlayerCancelForceDrain(game);
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        if (!canPerformingPlayerCancelForceDrain(game))
            return new FullEffectResult(false);

        ForceDrainState forceDrainState = game.getGameState().getForceDrainState();
        forceDrainState.cancel();
        if (_action.getPerformingPlayer() != null)
            game.getGameState().sendMessage(_action.getPerformingPlayer() + " cancels Force drain at " + GameUtils.getCardLink(forceDrainState.getLocation()) + " using " + GameUtils.getCardLink(_action.getActionSource()));
        else
            game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " cancels Force drain at " + GameUtils.getCardLink(forceDrainState.getLocation()));

        return new FullEffectResult(true);
    }

    /**
     * Private method to determine if current Force drain can be canceled performing player.
     * @param game the game
     * @return true or false
     */
    private boolean canPerformingPlayerCancelForceDrain(SwccgGame game) {
        ForceDrainState forceDrainState = game.getGameState().getForceDrainState();
        return forceDrainState != null && forceDrainState.canContinue()
                && !game.getModifiersQuerying().cantCancelForceDrainAtLocation(game.getGameState(), forceDrainState.getLocation(), _action.getActionSource(), _action.getPerformingPlayer(), forceDrainState.getPlayerId());
    }
}

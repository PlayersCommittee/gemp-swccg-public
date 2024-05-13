package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DuelState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.DuelCanceledResult;

/**
 * An effect that cancels the current duel.
 */
public class CancelDuelEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that cancels the current duel.
     * @param action the action performing this effect
     */
    public CancelDuelEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        DuelState duelState = gameState.getDuelState();
        if (duelState !=null && duelState.canContinue(game)) {
            gameState.sendMessage(_action.getPerformingPlayer() + " cancels " + (duelState.isEpicDuel() ? "epic " : "") + "duel using " + GameUtils.getCardLink(_action.getActionSource()));
            duelState.cancel();
            game.getActionsEnvironment().emitEffectResult(new DuelCanceledResult(_action.getPerformingPlayer()));
        }
    }
}

package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CancelBattleResult;

public class CancelBattleEffect extends PassthruEffect {

    public CancelBattleEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        BattleState battleState = game.getGameState().getBattleState();
        if (battleState != null && battleState.canContinue(game)) {

            if ((_action.getPerformingPlayer() != null
                    && game.getModifiersQuerying().mayNotCancelBattle(game.getGameState(), _action.getPerformingPlayer(), battleState.getBattleLocation()))) {

                game.getGameState().sendMessage(_action.getPerformingPlayer() + " may not cancel battle");

            } else {
                battleState.cancel();

                if (_action.getPerformingPlayer() != null)
                    game.getGameState().sendMessage(_action.getPerformingPlayer() + " cancels battle at " + GameUtils.getCardLink(battleState.getBattleLocation()) + " using " + GameUtils.getCardLink(_action.getActionSource()));
                else
                    game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " cancels battle at " + GameUtils.getCardLink(battleState.getBattleLocation()));

                game.getActionsEnvironment().emitEffectResult(new CancelBattleResult(_action.getPerformingPlayer(), _action.getActionSource(), battleState.getBattleLocation()));
            }
        }
    }
}

package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.AttackState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.CancelAttackResult;

public class CancelAttackEffect extends PassthruEffect {

    public CancelAttackEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        AttackState attackState = game.getGameState().getAttackState();
        if (attackState !=null && attackState.canContinue()) {
            attackState.cancel();
            game.getGameState().sendMessage(_action.getPerformingPlayer() + " cancels attack using " + GameUtils.getCardLink(_action.getActionSource()));
            game.getActionsEnvironment().emitEffectResult(new CancelAttackResult(_action.getPerformingPlayer()));
        }
    }
}

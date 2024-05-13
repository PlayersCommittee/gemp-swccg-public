package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.PassthruEffect;

/**
 * An effect that cancels a card action from performing its results.
 */
public class CancelCardResultEffect extends PassthruEffect {
    private Effect _effect;

    /**
     * Creates an effect that cancels a card action from performing its results.
     * @param action the action performing this effect
     * @param effect the respondable card action effect
     */
    public CancelCardResultEffect(Action action, Effect effect) {
        super(action);
        _effect = effect;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (!_effect.isCanceled()) {
            _effect.cancel(_action.getActionSource());
            game.getGameState().sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " cancels " + GameUtils.getCardLink(_effect.getAction().getActionSource()) + " from performing its result");
        }
    }
}

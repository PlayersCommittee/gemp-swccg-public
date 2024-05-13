package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that switches the players' total power in battle.
 */
public class SwitchTotalPowerInBattleEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect switches the players' total power in battle.
     * @param action the action performing this effect
     */
    public SwitchTotalPowerInBattleEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        BattleState battleState = gameState.getBattleState();
        if (battleState != null) {
            gameState.sendMessage(_action.getPerformingPlayer() + " switches players' total power in battle using " + GameUtils.getCardLink(_action.getActionSource()));
            battleState.switchPreBattleDestinyTotalPower();
        }
    }
}

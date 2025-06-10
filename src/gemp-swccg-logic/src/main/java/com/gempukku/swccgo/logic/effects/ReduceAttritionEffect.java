package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that reduces attrition against the specified player.
 */
public class ReduceAttritionEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private float _amount;

    /**
     * Creates an effect that reduces attrition against the specified player.
     * @param action the action performing this effect
     * @param playerId the player against whom attrition is reduced
     * @param amount the amount to reduce attrition
     */
    public ReduceAttritionEffect(Action action, String playerId, float amount) {
        super(action);
        _playerId = playerId;
        _amount = amount;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        BattleState battleState = gameState.getBattleState();

        if (_amount > 0 && battleState != null && battleState.getAttritionTotal(game, _playerId) > 0) {
            gameState.sendMessage("Attrition against " + _playerId + " is reduced by " + GuiUtils.formatAsString(_amount));
            modifiersEnvironment.addUntilEndOfBattleModifier(
                    new AttritionModifier(_action.getActionSource(), -_amount, _playerId));
        }
    }
}

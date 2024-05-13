package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ModifiersEnvironment;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that subtracts a specified amount from opponent's attrition.
 */
public class SubtractFromOpponentsAttritionEffect extends AbstractSuccessfulEffect {
    private float _amount;

    /**
     * Creates an effect that subtracts a specified amount from opponent's attrition.
     * @param action the action performing this effect
     * @param amount the amount
     */
    public SubtractFromOpponentsAttritionEffect(Action action, float amount) {
        super(action);
        _amount = amount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        BattleState battleState = gameState.getBattleState();
        if (battleState != null && _amount > 0) {
            String playerId = _action.getPerformingPlayer();
            ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();

            modifiersEnvironment.addUntilEndOfBattleModifier(
                    new AttritionModifier(_action.getActionSource(), -_amount, playerId));

            String msg = playerId + " reduces opponent's attrition by " + GuiUtils.formatAsString(_amount) + " using " + GameUtils.getCardLink(_action.getActionSource());
            game.getGameState().sendMessage(msg);
        }
    }
}

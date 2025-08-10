package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersEnvironment;
import com.gempukku.swccgo.logic.modifiers.TotalPowerDuringBattleModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that subtracts a specified amount from opponent's total power and attrition.
 */
public class SubtractFromOpponentsTotalPowerAndAttritionEffect extends AbstractSuccessfulEffect {
    private float _amount;

    /**
     * Creates an effect that subtracts a specified amount from opponent's total power and attrition.
     * @param action the action performing this effect
     * @param amount the amount
     */
    public SubtractFromOpponentsTotalPowerAndAttritionEffect(Action action, float amount) {
        super(action);
        _amount = amount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        BattleState battleState = gameState.getBattleState();
        if (battleState != null && _amount > 0) {
            String playerId = _action.getPerformingPlayer();
            String opponent = game.getOpponent(playerId);
            ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();

            modifiersEnvironment.addUntilEndOfBattleModifier(
                    new AttritionModifier(_action.getActionSource(), -_amount, playerId));
            modifiersEnvironment.addUntilEndOfBattleModifier(
                    new TotalPowerDuringBattleModifier(_action.getActionSource(), -_amount, opponent));

            String msg = playerId + " reduces opponent's attrition and total power by " + GuiUtils.formatAsString(_amount) + " using " + GameUtils.getCardLink(_action.getActionSource());
            game.getGameState().sendMessage(msg);
        }
    }
}

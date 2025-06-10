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
 * An effect that increases attrition against the specified player.
 */
public class AddToAttritionEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private float _amount;
    private boolean _cumulative;

    /**
     * Creates an effect that increases attrition against the specified player.
     * @param action the action performing this effect
     * @param playerId the player against whom attrition is increased
     * @param amount the amount to increase attrition
     */
    public AddToAttritionEffect(Action action, String playerId, float amount) {
        this(action, playerId, amount, false);
    }

    /**
     * Creates an effect that increases attrition against the specified player.
     * @param action the action performing this effect
     * @param playerId the player against whom attrition is increased
     * @param amount the amount to increase attrition
     * @param cumulative true if the modifying is cumulative, otherwise false
     */
    public AddToAttritionEffect(Action action, String playerId, float amount, boolean cumulative) {
        super(action);
        _playerId = playerId;
        _amount = amount;
        _cumulative = cumulative;
    }

    @Override
    public void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersEnvironment modifiersEnvironment = game.getModifiersEnvironment();
        BattleState battleState = gameState.getBattleState();

        if (_amount > 0 && battleState != null) {
            String opponent = game.getOpponent(_playerId);
            if (battleState.getNumBattleDestinyDrawn(opponent) > 0) {
                gameState.sendMessage("Attrition against " + _playerId + " is increased by " + GuiUtils.formatAsString(_amount));
                modifiersEnvironment.addUntilEndOfBattleModifier(
                        new AttritionModifier(_action.getActionSource(), _amount, _playerId, _cumulative));
            }
            else {
                gameState.sendMessage(opponent + " did not draw battle destiny, so there is no attrition against " + _playerId + " to be increased");
            }
        }
    }
}

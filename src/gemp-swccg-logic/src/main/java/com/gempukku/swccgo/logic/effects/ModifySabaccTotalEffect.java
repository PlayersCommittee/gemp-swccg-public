package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.SabaccState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.SabaccTotalModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that modifies a player's Sabacc total.
 */
public class ModifySabaccTotalEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private float _modifierAmount;

    /**
     * Creates an effect that modifies a player's Sabacc total.
     * @param action the action performing this effect.
     * @param playerId the player whose Sabacc total is modified
     * @param modifierAmount the amount to modify
     */
    public ModifySabaccTotalEffect(Action action, String playerId, float modifierAmount) {
        super(action);
        _playerId = playerId;
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        SabaccState sabaccState = gameState.getSabaccState();
        if (sabaccState != null) {
            PhysicalCard source = _action.getActionSource();

            game.getModifiersEnvironment().addUntilEndOfSabaccModifier(
                    new SabaccTotalModifier(source, _modifierAmount, _playerId));
            if (_modifierAmount > 0) {
                gameState.sendMessage(GameUtils.getCardLink(source) + " adds " + GuiUtils.formatAsString(_modifierAmount) + " to " + _playerId + "'s Sabacc total");
            }
            else if (_modifierAmount < 0) {
                gameState.sendMessage(GameUtils.getCardLink(source) + " subtracts " + GuiUtils.formatAsString(-_modifierAmount) + " from " + _playerId + "'s Sabacc total");
            }
        }
    }
}

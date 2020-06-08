package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.TotalCarbonFreezingDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that modifies the total carbon-freezing destiny.
 */
public class ModifyTotalCarbonFreezingDestinyEffect extends AbstractSuccessfulEffect {
    private float _modifierAmount;

    /**
     * Creates an effect that modifies the total carbon-freezing destiny.
     * @param action the action performing this effect.
     * @param modifierAmount the amount to modify
     */
    public ModifyTotalCarbonFreezingDestinyEffect(Action action, float modifierAmount) {
        super(action);
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (drawDestinyEffect.getDestinyType() == DestinyType.CARBON_FREEZING_DESTINY) {

                game.getModifiersEnvironment().addUntilEndOfDrawDestinyModifier(
                        new TotalCarbonFreezingDestinyModifier(_action.getActionSource(), _modifierAmount));
                if (_modifierAmount > 0) {
                    gameState.sendMessage(_action.getPerformingPlayer() + " adds " + GuiUtils.formatAsString(_modifierAmount) + " to total carbon-freezing destiny");
                }
                else if (_modifierAmount < 0) {
                    gameState.sendMessage(_action.getPerformingPlayer() + " subtracts " + GuiUtils.formatAsString(-_modifierAmount) + " from total carbon-freezing destiny");
                }
                game.getGameState().sendMessage(drawDestinyEffect.getPlayerDrawingDestiny() + "'s total carbon-freezing destiny is " + GuiUtils.formatAsString(drawDestinyEffect.getTotalDestiny(game)));
            }
        }
    }
}

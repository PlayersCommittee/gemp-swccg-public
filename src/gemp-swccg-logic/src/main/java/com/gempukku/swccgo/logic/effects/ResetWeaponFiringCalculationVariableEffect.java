package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.modifiers.ResetCalculationVariableModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that reset the value for X for when weapon being fired.
 */
public class ResetWeaponFiringCalculationVariableEffect extends AbstractSuccessfulEffect {
    private Filterable _weapon;
    private float _resetValue;

    /**
     * Creates an effect that resets the initial value for X a specified weapon being fired.
     * @param action the action performing this effect.
     * @param weapon the weapon
     * @param resetValue the reset value
     */
    public ResetWeaponFiringCalculationVariableEffect(Action action, Filterable weapon, float resetValue) {
        super(action);
        _weapon = weapon;
        _resetValue = resetValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState != null) {
            game.getModifiersEnvironment().addUntilEndOfWeaponFiringModifier(
                    new ResetCalculationVariableModifier(_action.getActionSource(), _weapon, _resetValue, Variable.X));
        }
    }
}

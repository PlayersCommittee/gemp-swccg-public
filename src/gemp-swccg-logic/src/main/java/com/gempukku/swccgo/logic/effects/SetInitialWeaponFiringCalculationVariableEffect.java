package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Variable;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.modifiers.SetInitialCalculationVariableModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that sets the initial value for X for when weapon being fired.
 */
public class SetInitialWeaponFiringCalculationVariableEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _weapon;
    private float _initialValue;

    /**
     * Creates an effect that sets the initial value for X for when weapon being fired.
     * @param action the action performing this effect.
     * @param weapon the weapon
     * @param initialValue the reset value
     */
    public SetInitialWeaponFiringCalculationVariableEffect(Action action, PhysicalCard weapon, float initialValue) {
        super(action);
        _weapon = weapon;
        _initialValue = initialValue;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState != null) {
            game.getModifiersEnvironment().addUntilEndOfWeaponFiringModifier(
                    new SetInitialCalculationVariableModifier(_action.getActionSource(), _weapon, _initialValue, Variable.X));
        }
    }
}

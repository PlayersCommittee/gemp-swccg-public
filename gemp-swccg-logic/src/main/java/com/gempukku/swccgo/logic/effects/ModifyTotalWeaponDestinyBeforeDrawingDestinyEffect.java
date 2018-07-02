package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that modifies the total weapon destiny before weapon destiny is drawn.
 */
public class ModifyTotalWeaponDestinyBeforeDrawingDestinyEffect extends AbstractSuccessfulEffect {
    private float _modifierAmount;

    /**
     * Creates an effect that modifies the total weapon destiny before weapon destiny is drawn.
     * @param action the action performing this effect.
     * @param modifierAmount the amount to modify
     */
    public ModifyTotalWeaponDestinyBeforeDrawingDestinyEffect(Action action, float modifierAmount) {
        super(action);
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        WeaponFiringState weaponFiringState = gameState.getWeaponFiringState();
        if (weaponFiringState != null) {

            Filterable weaponFilter;
            if (gameState.getWeaponFiringState().getPermanentWeaponFiring() != null)
                weaponFilter = Filters.sameBuiltIn(gameState.getWeaponFiringState().getPermanentWeaponFiring());
            else
                weaponFilter = Filters.sameCardId(gameState.getWeaponFiringState().getCardFiring());

            game.getModifiersEnvironment().addUntilEndOfWeaponFiringModifier(
                    new TotalWeaponDestinyModifier(_action.getActionSource(), weaponFilter, _modifierAmount));

            if (_modifierAmount > 0) {
                gameState.sendMessage(_action.getPerformingPlayer() + " adds " + GuiUtils.formatAsString(_modifierAmount) + " to total weapon destiny");
            }
            else if (_modifierAmount < 0) {
                gameState.sendMessage(_action.getPerformingPlayer() + " subtracts " + GuiUtils.formatAsString(-_modifierAmount) + " from total weapon destiny");
            }
        }
    }
}

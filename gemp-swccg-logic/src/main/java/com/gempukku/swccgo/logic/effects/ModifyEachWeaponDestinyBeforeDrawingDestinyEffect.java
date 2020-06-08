package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.game.state.WeaponFiringState;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that modifies the each weapon destiny draw before weapon destiny is drawn.
 */
public class ModifyEachWeaponDestinyBeforeDrawingDestinyEffect extends AbstractSuccessfulEffect {
    private float _modifierAmount;

    /**
     * Creates an effect that modifies each weapon destiny draw before weapon destiny is drawn.
     * @param action the action performing this effect.
     * @param modifierAmount the amount to modify
     */
    public ModifyEachWeaponDestinyBeforeDrawingDestinyEffect(Action action, float modifierAmount) {
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
                    new EachWeaponDestinyModifier(_action.getActionSource(), weaponFilter, _modifierAmount));

            if (_modifierAmount > 0) {
                gameState.sendMessage(_action.getPerformingPlayer() + " adds " + GuiUtils.formatAsString(_modifierAmount) + " to each weapon destiny draw");
            }
            else if (_modifierAmount < 0) {
                gameState.sendMessage(_action.getPerformingPlayer() + " subtracts " + GuiUtils.formatAsString(-_modifierAmount) + " from each weapon destiny draw");
            }
        }
    }
}

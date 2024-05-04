package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.TotalWeaponDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that modifies the total weapon destiny.
 */
public class ModifyTotalWeaponDestinyEffect extends AbstractSuccessfulEffect {
    private float _modifierAmount;

    /**
     * Creates an effect that modifies the total weapon destiny.
     * @param action the action performing this effect.
     * @param modifierAmount the amount to modify
     */
    public ModifyTotalWeaponDestinyEffect(Action action, float modifierAmount) {
        super(action);
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (drawDestinyEffect.getDestinyType() == DestinyType.WEAPON_DESTINY) {

                Filterable weaponFilter;
                if (gameState.getWeaponFiringState().getPermanentWeaponFiring() != null)
                    weaponFilter = Filters.sameBuiltIn(gameState.getWeaponFiringState().getPermanentWeaponFiring());
                else
                    weaponFilter = Filters.sameCardId(gameState.getWeaponFiringState().getCardFiring());

                game.getModifiersEnvironment().addUntilEndOfDrawDestinyModifier(
                        new TotalWeaponDestinyModifier(_action.getActionSource(), weaponFilter, _modifierAmount));

                if (_modifierAmount > 0) {
                    gameState.sendMessage(_action.getPerformingPlayer() + " adds " + GuiUtils.formatAsString(_modifierAmount) + " to total weapon destiny");
                }
                else if (_modifierAmount < 0) {
                    gameState.sendMessage(_action.getPerformingPlayer() + " subtracts " + GuiUtils.formatAsString(-_modifierAmount) + " from total weapon destiny");
                }
                game.getGameState().sendMessage(drawDestinyEffect.getPlayerDrawingDestiny() + "'s total weapon destiny is " + GuiUtils.formatAsString(drawDestinyEffect.getTotalDestiny(game)));
            }
        }
    }
}

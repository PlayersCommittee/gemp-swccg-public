package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.ModifiersQuerying;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that modifies the just drawn destiny.
 */
public class ModifyDestinyEffect extends AbstractSuccessfulEffect {
    private float _modifierAmount;

    /**
     * Creates an effect that modifies the just drawn destiny.
     * @param action the action performing this effect.
     * @param modifierAmount the amount to modify
     */
    public ModifyDestinyEffect(Action action, float modifierAmount) {
        super(action);
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        String playerId = _action.getPerformingPlayer();
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (!drawDestinyEffect.isDestinyCanceled()
                    && !drawDestinyEffect.isDestinyReset()
                    && drawDestinyEffect.getDrawnDestinyCard() != null) {

                // Check if battle destiny modifiers affect total battle destiny instead
                if (drawDestinyEffect.getPlayerDrawingDestiny().equals(playerId)
                        && drawDestinyEffect.getDestinyType() == DestinyType.BATTLE_DESTINY
                        && modifiersQuerying.hasFlagActive(gameState, ModifierFlag.BATTLE_DESTINY_MODIFIERS_AFFECT_TOTAL_BATTLE_DESTINY_INSTEAD, playerId)) {

                    if (modifiersQuerying.mayNotModifyTotalBattleDestiny(game.getGameState(), drawDestinyEffect.getPlayerDrawingDestiny(), playerId)) {
                        gameState.sendMessage(playerId + " may not modify " + drawDestinyEffect.getPlayerDrawingDestiny() + "'s total battle destiny");
                        return;
                    }

                    game.getModifiersEnvironment().addUntilEndOfBattleModifier(
                            new TotalBattleDestinyModifier(_action.getActionSource(), _modifierAmount, playerId));
                    if (_modifierAmount > 0) {
                        gameState.sendMessage(playerId + " adds " + GuiUtils.formatAsString(_modifierAmount) + " to total battle destiny");
                    }
                    else if (_modifierAmount < 0) {
                        gameState.sendMessage(playerId + " subtracts " + GuiUtils.formatAsString(_modifierAmount) + " from total battle destiny");
                    }
                    return;
                }

                if (modifiersQuerying.mayNotModifyDestinyDraw(game.getGameState(), playerId)) {
                    gameState.sendMessage(playerId + " may not modify the " + drawDestinyEffect.getDestinyType().getHumanReadable());
                    return;
                }

                drawDestinyEffect.modifyDestiny(_modifierAmount);
                if (_modifierAmount > 0) {
                    gameState.sendMessage(playerId + " adds " + GuiUtils.formatAsString(_modifierAmount)
                            + " to make the " + drawDestinyEffect.getDestinyType().getHumanReadable() + " " + GuiUtils.formatAsString(drawDestinyEffect.getDestinyDrawValue()));
                }
                else if (_modifierAmount < 0) {
                    gameState.sendMessage(playerId + " subtracts " + GuiUtils.formatAsString(-_modifierAmount)
                            + " to make the " + drawDestinyEffect.getDestinyType().getHumanReadable() + " " + GuiUtils.formatAsString(drawDestinyEffect.getDestinyDrawValue()));
                }
            }
        }
    }
}

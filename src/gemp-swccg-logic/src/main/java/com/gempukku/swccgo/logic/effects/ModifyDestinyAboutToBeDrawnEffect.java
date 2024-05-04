package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.EachDrawnDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.DestinyWhenDrawnForDestinyModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that modifies a destiny about to be drawn.
 */
public class ModifyDestinyAboutToBeDrawnEffect extends AbstractSuccessfulEffect {
    private float _modifierAmount;

    /**
     * Creates an effect that modifies a destiny about to be drawn.
     * @param action the action performing this effect.
     * @param modifierAmount the amount to modify
     */
    public ModifyDestinyAboutToBeDrawnEffect(Action action, float modifierAmount) {
        super(action);
        _modifierAmount = modifierAmount;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        EachDrawnDestinyState eachDrawnDestinyState = gameState.getTopEachDrawnDestinyState();
        if (eachDrawnDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = eachDrawnDestinyState.getDrawDestinyEffect();
            if (drawDestinyEffect.getSubstituteDestiny() == null) {
                PhysicalCard source = _action.getActionSource();

                game.getModifiersEnvironment().addUntilEndOfEachDrawnDestinyModifier(
                        new DestinyWhenDrawnForDestinyModifier(source, Filters.any, _modifierAmount));
                if (_modifierAmount > 0) {
                    gameState.sendMessage(GameUtils.getCardLink(source) + " adds " + GuiUtils.formatAsString(_modifierAmount) + " to about to be drawn " + drawDestinyEffect.getDestinyType().getHumanReadable());
                }
                else if (_modifierAmount < 0) {
                    gameState.sendMessage(GameUtils.getCardLink(source) + " subtracts " + GuiUtils.formatAsString(-_modifierAmount) + " from about to be drawn " + drawDestinyEffect.getDestinyType().getHumanReadable());
                }
            }
        }
    }
}

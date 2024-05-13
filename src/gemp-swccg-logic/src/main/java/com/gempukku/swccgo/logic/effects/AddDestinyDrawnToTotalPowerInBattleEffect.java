package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.DrawDestinyState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.GuiUtils;

/**
 * An effect that adds the value of the just drawn destiny to the performing player's total power in the current battle.
 */
public class AddDestinyDrawnToTotalPowerInBattleEffect extends AbstractSuccessfulEffect {

    /**
     * Creates an effect that adds the value of the just drawn destiny to the performing player's total power in the current battle.
     * @param action the action performing this effect
     */
    public AddDestinyDrawnToTotalPowerInBattleEffect(Action action) {
        super(action);
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        DrawDestinyState drawDestinyState = gameState.getTopDrawDestinyState();
        if (drawDestinyState != null) {
            DrawDestinyEffect drawDestinyEffect = drawDestinyState.getDrawDestinyEffect();
            if (!drawDestinyEffect.isDestinyCanceled()) {
                float destinyValue = Math.max(0, drawDestinyEffect.getDestinyDrawValue());
                gameState.sendMessage(_action.getPerformingPlayer() + " adds " + GuiUtils.formatAsString(destinyValue) + " to total power in battle using " + GameUtils.getCardLink(_action.getActionSource()));
                game.getModifiersEnvironment().addUntilEndOfBattleModifier(
                        new TotalPowerModifier(_action.getActionSource(), Filters.battleLocation, destinyValue, _action.getPerformingPlayer()));
            }
        }
    }
}

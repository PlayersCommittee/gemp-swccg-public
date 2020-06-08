package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that doubles a player's power present in battle.
 */
public class DoublePowerPresentInBattleEffect extends AbstractSuccessfulEffect {
    private String _playerId;

    /**
     * Creates an effect that doubles a player's power present in battle.
     * @param action the action performing this effect
     * @param playerId the player whose power present in battle is doubled
     */
    public DoublePowerPresentInBattleEffect(Action action, String playerId) {
        super(action);
        _playerId = playerId;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        BattleState battleState = gameState.getBattleState();
        if (battleState == null)
            return;

        gameState.sendMessage(_playerId + "'s power present in battle is doubled");

        float powerPresent = game.getModifiersQuerying().getTotalPowerAtLocation(gameState, battleState.getBattleLocation(), _playerId, true, true);
        game.getModifiersEnvironment().addUntilEndOfBattleModifier(
                new TotalPowerModifier(_action.getActionSource(), Filters.battleLocation, powerPresent, _playerId));
    }
}

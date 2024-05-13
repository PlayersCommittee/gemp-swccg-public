package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that satisfies all remaining battle damage against the specified player.
 */
public class SatisfyAllBattleDamageEffect extends AbstractSuccessfulEffect {
    private String _playerId;

    /**
     * Creates an effect that satisfies all remaining battle damage against the specified player.
     * @param action the action performing this effect
     * @param playerId the player whose battle damage is satisfied
     */
    public SatisfyAllBattleDamageEffect(Action action, String playerId) {
        super(action);
        _playerId = playerId;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        BattleState battleState = gameState.getBattleState();

        gameState.sendMessage(_playerId + "'s remaining battle damage is satisfied");
        battleState.satisfyAllBattleDamage(_playerId);
    }
}

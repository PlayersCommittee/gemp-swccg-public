package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that satisfies all remaining battle damage and attrition against the specified player.
 */
public class SatisfyAllBattleDamageAndAttritionEffect extends AbstractSuccessfulEffect {
    private String _playerId;

    /**
     * Creates an effect that satisfies all remaining battle damage and attrition against the specified player.
     * @param action the action performing this effect
     * @param playerId the player whose battle damage and attrition are satisfied
     */
    public SatisfyAllBattleDamageAndAttritionEffect(Action action, String playerId) {
        super(action);
        _playerId = playerId;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        BattleState battleState = gameState.getBattleState();

        gameState.sendMessage(_playerId + "'s remaining battle damage and attrition is satisfied");
        battleState.satisfyAllBattleDamage(_playerId);
        battleState.satisfyAllAttrition(_playerId);
    }
}

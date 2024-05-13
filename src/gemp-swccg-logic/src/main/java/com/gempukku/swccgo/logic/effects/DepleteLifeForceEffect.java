package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player's Life Force to be depleted.
 */
public class DepleteLifeForceEffect extends AbstractSuccessfulEffect {
    private String _playerId;

    /**
     * Creates an effect that causes the specified player's Life Force to be depleted.
     * @param action the action performing this effect
     * @param playerId the player whose Life Force is depleted
     */
    public DepleteLifeForceEffect(Action action, String playerId) {
        super(action);
        _playerId = playerId;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        PhysicalCard source = _action.getActionSource();
        GameState gameState = game.getGameState();
        gameState.sendMessage(GameUtils.getCardLink(source) + " causes " + _playerId + "'s Life Force to be depleted");
        gameState.lifeForceDepleted(_playerId);
    }
}

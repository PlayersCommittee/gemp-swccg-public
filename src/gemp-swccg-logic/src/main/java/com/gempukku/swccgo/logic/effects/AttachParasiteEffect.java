package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ParasiteAttachedResult;

/**
 * An effect that attaches a parasite creature to a host.
 */
public class AttachParasiteEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _creature;
    private PhysicalCard _host;

    /**
     * Creates an effect that attaches a parasite creature to a host.
     * @param action the action performing this effect
     * @param creature the creature
     * @param host the host
     */
    public AttachParasiteEffect(Action action, PhysicalCard creature, PhysicalCard host) {
        super(action);
        _creature = creature;
        _host = host;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        if (_action.getPerformingPlayer() != null)
            gameState.sendMessage(_action.getPerformingPlayer() + " attaches " + GameUtils.getCardLink(_creature) + " to " + GameUtils.getCardLink(_host));
        else
            gameState.sendMessage(GameUtils.getCardLink(_creature) + " attaches to " + GameUtils.getCardLink(_host));
        gameState.moveCardToAttached(_creature, _host);
        game.getActionsEnvironment().emitEffectResult(new ParasiteAttachedResult(_action.getPerformingPlayer(), _creature, _host));
    }
}

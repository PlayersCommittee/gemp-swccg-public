package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ParasiteDetachedResult;

/**
 * An effect that detaches a parasite creature.
 */
public class DetachParasiteEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _creature;

    /**
     * Creates an effect that detaches a parasite creature.
     * @param action the action performing this effect
     * @param creature the creature
     */
    public DetachParasiteEffect(Action action, PhysicalCard creature) {
        super(action);
        _creature = creature;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(gameState, _creature);
        PhysicalCard attachedTo = _creature.getAttachedTo();
        if (location != null && attachedTo != null) {
            if (_action.getPerformingPlayer() != null)
                gameState.sendMessage(_action.getPerformingPlayer() + " detaches " + GameUtils.getCardLink(_creature) + " from " + GameUtils.getCardLink(attachedTo));
            else
                gameState.sendMessage(GameUtils.getCardLink(_creature) + " detaches from " + GameUtils.getCardLink(attachedTo));
            gameState.moveCardToLocation(_creature, location, true);
            game.getActionsEnvironment().emitEffectResult(new ParasiteDetachedResult(_action.getPerformingPlayer(), _creature, attachedTo));
        }
    }
}

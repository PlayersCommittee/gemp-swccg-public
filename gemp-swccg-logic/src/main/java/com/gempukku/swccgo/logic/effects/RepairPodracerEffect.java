package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.RepairedPodracerResult;

/**
 * An effect that 'repairs' a 'damaged' Podracer.
 */
public class RepairPodracerEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;

    /**
     * Creates an effect that 'repairs' a 'damaged' Podracer.
     * @param action the action performing this effect
     * @param card the Podracer
     */
    public RepairPodracerEffect(Action action, PhysicalCard card) {
        super(action);
        _card = card;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (!_card.isDamaged())
            return;

        GameState gameState = game.getGameState();
        gameState.sendMessage(GameUtils.getCardLink(_card) + " is 'repaired'");
        _card.setDamaged(false);
        gameState.invertCard(game, _card, false);

        game.getActionsEnvironment().emitEffectResult(new RepairedPodracerResult(_action.getPerformingPlayer(), _card));
    }
}

package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.DamagedPodracerResult;

/**
 * An effect that causes a Podracer to become 'damaged'.
 */
public class DamagePodracerEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;

    /**
     * Creates an effect that causes a Podracer to become 'damaged'.
     * @param action the action performing this effect
     * @param card the Podracer
     */
    public DamagePodracerEffect(Action action, PhysicalCard card) {
        super(action);
        _card = card;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_card.isDamaged())
            return;

        GameState gameState = game.getGameState();
        gameState.sendMessage(GameUtils.getCardLink(_card) + " is 'damaged'");
        _card.setDamaged(true);
        gameState.invertCard(game, _card, true);

        game.getActionsEnvironment().emitEffectResult(new DamagedPodracerResult(_action.getPerformingPlayer(), _card));
    }
}

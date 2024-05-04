package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.PutUndercoverResult;

/**
 * An effect makes a card become an 'undercover spy'.
 */
public class PutUndercoverEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;

    /**
     * Creates an effect that makes a card become an 'undercover spy'.
     * @param action the action performing this effect
     * @param card the card to become an 'undercover spy'
     */
    public PutUndercoverEffect(Action action, PhysicalCard card) {
        super(action);
        _card = card;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_card.isUndercover())
            return;

        GameState gameState = game.getGameState();
        gameState.sendMessage(GameUtils.getCardLink(_card) + " is 'undercover'");
        gameState.putUndercover(_card);

        game.getActionsEnvironment().emitEffectResult(new PutUndercoverResult(_action.getPerformingPlayer(), _card));
    }
}

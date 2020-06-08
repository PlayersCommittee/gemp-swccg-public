package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.ConcealedResult;

/**
 * An effect that causes a specified card to attach to another card and become 'concealed'.
 */
public class AttachAndConcealEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private PhysicalCard _attachTo;

    /**
     * Creates an effect that causes a specified card to attach to another card and become 'concealed'.
     * @param action the action performing this effect
     * @param card the card to become 'concealed'
     * @param attachTo the card to be attached to
     */
    public AttachAndConcealEffect(Action action, PhysicalCard card, PhysicalCard attachTo) {
        super(action);
        _card = card;
        _attachTo = attachTo;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        gameState.sendMessage(GameUtils.getCardLink(_card) + " attaches to " + GameUtils.getCardLink(_attachTo) + " using " + GameUtils.getCardLink(_action.getActionSource()) + " and is 'concealed'");
        _card.setConcealed(true);
        gameState.moveCardToAttached(_card, _attachTo);
        game.getActionsEnvironment().emitEffectResult(new ConcealedResult(_action.getPerformingPlayer(), _card));
    }
}

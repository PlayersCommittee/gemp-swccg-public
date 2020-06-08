package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.UnconcealedResult;

/**
 * An effect that causes a specified card to detach from another card and no longer be 'concealed'.
 */
public class DetachAndUnconcealEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;

    /**
     * Creates an effect that causes a specified card to detach from another card and no longer be 'concealed'.
     * @param action the action performing this effect
     * @param card the card
     */
    public DetachAndUnconcealEffect(Action action, PhysicalCard card) {
        super(action);
        _card = card;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        PhysicalCard location = game.getModifiersQuerying().getLocationThatCardIsAt(gameState, _card);

        gameState.sendMessage(GameUtils.getCardLink(_card) + " detaches from " + GameUtils.getCardLink(_card.getAttachedTo()));
        _card.setConcealed(false);
        gameState.moveCardToLocation(_card, location, true);
        game.getActionsEnvironment().emitEffectResult(new UnconcealedResult(_action.getPerformingPlayer(), _card));
    }
}

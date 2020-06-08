package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.AttachedCardFromTableResult;

/**
 * An effect that attaches a card on table to another card on table.
 */
public class AttachCardFromTableEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToAttach;
    private PhysicalCard _attachToCard;

    /**
     * Creates an effect that attaches a card on table to another card on table.
     * @param action the action performing this effect
     * @param cardToAttach the card to be attached
     * @param attachToCard the card to attach it to
     */
    public AttachCardFromTableEffect(Action action, PhysicalCard cardToAttach, PhysicalCard attachToCard) {
        super(action);
        _cardToAttach = cardToAttach;
        _attachToCard = attachToCard;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        if (_action.getPerformingPlayer() != null)
            gameState.sendMessage(_action.getPerformingPlayer() + " places " + GameUtils.getCardLink(_cardToAttach) + " on " + GameUtils.getCardLink(_attachToCard));
        else
            gameState.sendMessage(GameUtils.getCardLink(_cardToAttach) + " is placed on " + GameUtils.getCardLink(_attachToCard));
        gameState.moveCardToAttached(_cardToAttach, _attachToCard);
        game.getActionsEnvironment().emitEffectResult(new AttachedCardFromTableResult(_action.getPerformingPlayer(), _cardToAttach, _attachToCard));
    }
}

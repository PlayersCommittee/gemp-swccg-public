package com.gempukku.swccgo.cards.effects.takeandputcards;

import com.gempukku.swccgo.common.CardState;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.timing.AbstractStandardEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.StackedFromHandResult;

import java.util.Collections;

/**
 * An effect to stack the specified card on the specified card.
 */
abstract class StackOneCardEffect extends AbstractStandardEffect {
    protected PhysicalCard _card;
    private PhysicalCard _stackOn;
    private boolean _faceDown;
    private String _msgText;

    /**
     * Creates an effect that causes the specified card to be stacked on the specified card.
     * @param action the action performing this effect
     * @param card the card
     * @param stackOn the card to stack on
     * @param faceDown true if card is to be stacked face down, otherwise false
     * @param msgText the message to send
     */
    protected StackOneCardEffect(Action action, PhysicalCard card, PhysicalCard stackOn, boolean faceDown, String msgText) {
        super(action);
        _card = card;
        _stackOn = stackOn;
        _faceDown = faceDown;
        _msgText = msgText;
    }

    @Override
    protected FullEffectResult playEffectReturningResult(SwccgGame game) {
        GameState gameState = game.getGameState();
        CardState prevCardState = game.getModifiersQuerying().getCardState(gameState, _card, false, false, false, false, false, false, false, false);
        gameState.removeCardsFromZone(Collections.singleton(_card));
        gameState.stackCard(_card, _stackOn, _faceDown, !_faceDown && (prevCardState == CardState.ACTIVE || prevCardState == CardState.INACTIVE), false);
        gameState.sendMessage(_msgText);

        game.getActionsEnvironment().emitEffectResult(
                new StackedFromHandResult(_action, _card, _stackOn));

        // A callback that can be used to schedule the next card to be put in card pile
        afterCardStacked();

        return new FullEffectResult(true);
    }

    protected abstract void afterCardStacked();
}

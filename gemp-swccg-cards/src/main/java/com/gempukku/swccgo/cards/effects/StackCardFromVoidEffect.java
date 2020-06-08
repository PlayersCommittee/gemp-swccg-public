package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that stacks a card from void on a specified card.
 */
public class StackCardFromVoidEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _card;
    private PhysicalCard _stackOn;

    /**
     * Creates an effect that stacks a card from void on a specified card.
     * @param action the action performing this effect
     * @param card the card to stack
     * @param stackOn the card to stack the card on
     */
    public StackCardFromVoidEffect(Action action, PhysicalCard card, PhysicalCard stackOn) {
        super(action);
        _card = card;
        _stackOn = stackOn;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        if (_card.getZone() == Zone.VOID) {
            GameState gameState = game.getGameState();
            if (_action.getPerformingPlayer() != null)
                gameState.sendMessage(_action.getPerformingPlayer() + " stacks " + GameUtils.getCardLink(_card) + " on " + GameUtils.getCardLink(_stackOn));
            else
                gameState.sendMessage(_card.getOwner() + " stacks " + GameUtils.getCardLink(_card) + " on " + GameUtils.getCardLink(_stackOn));
            gameState.removeCardFromZone(_card);
            gameState.stackCard(_card, _stackOn, false, false, false);
        }
    }
}

package com.gempukku.swccgo.cards.effects;

import com.gempukku.swccgo.common.CardSubtype;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that changes the subtype of an Interrupt after it has started to be played.
 */
public class ChangePlayedInterruptSubtypeEffect extends AbstractSuccessfulEffect {
    private PlayInterruptAction _playInterruptAction;
    private CardSubtype _newSubtypePlayedAs;

    /**
     * Creates an effect that re-targets the Interrupt to new target.
     * @param action the action performing this effect
     * @param playInterruptAction the Interrupt that needs its subtype changed
     * @param newSubtypePlayedAs the old targets
     */
    public ChangePlayedInterruptSubtypeEffect(Action action, PlayInterruptAction playInterruptAction, CardSubtype newSubtypePlayedAs) {
        super(action);
        _playInterruptAction = playInterruptAction;
        _newSubtypePlayedAs = newSubtypePlayedAs;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        if (_playInterruptAction.getPlayedAsSubtype()!=_newSubtypePlayedAs) {
            _playInterruptAction.setPlayedAsSubtype(_newSubtypePlayedAs);

            gameState.sendMessage(GameUtils.getCardLink(_action.getActionSource()) + " changes " + GameUtils.getCardLink(_playInterruptAction.getPlayedCard()) + " to a " + _newSubtypePlayedAs.getHumanReadable() + " Interrupt");
            gameState.cardAffectsCard(_action.getPerformingPlayer(), _action.getActionSource(), _playInterruptAction.getPlayedCard());
        }
    }
}

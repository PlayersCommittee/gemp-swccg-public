package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.NumCardsDrawnInStartingHandModifier;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

/**
 * An effect that causes the specified player to draw a specified number of cards when drawing opening hand.
 */
public class ModifyNumCardsDrawnInStartingHandEffect extends AbstractSuccessfulEffect {
    private String _playerId;
    private int _numCards;

    /**
     * Creates an effect that causes the specified player to draw a specified number of cards when drawing opening hand.
     * @param action the action performing this effect
     * @param playerId the player
     * @param numCards the number of cards
     */
    public ModifyNumCardsDrawnInStartingHandEffect(Action action, String playerId, int numCards) {
        super(action);
        _playerId = playerId;
        _numCards = numCards;
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        PhysicalCard source = _action.getActionSource();
        game.getModifiersEnvironment().addUntilEndOfGameModifier(
                new NumCardsDrawnInStartingHandModifier(source, _numCards, _playerId));
    }
}

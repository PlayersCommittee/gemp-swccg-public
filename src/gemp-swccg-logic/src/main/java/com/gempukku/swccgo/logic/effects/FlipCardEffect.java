package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.results.DoubleSidedCardFlippedResult;

/**
 * An effect that flips a double-sided card to the other side.
 */
public class FlipCardEffect extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToFlip;
    private boolean _toBack;

    /**
     * Creates an effect that flips the Objective to the other side.
     * @param action the action performing this effect
     * @param cardToFlip the card to flip
     */
    public FlipCardEffect(Action action, PhysicalCard cardToFlip) {
        super(action);
        _cardToFlip = cardToFlip;
        _toBack = !_cardToFlip.isFlipped();
    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();
        ModifiersQuerying modifiersQuerying = game.getModifiersQuerying();

        if (_cardToFlip.isDoubleSided()
                && _cardToFlip.isFlipped() != _toBack
                && !modifiersQuerying.cannotBeFlipped(gameState, _cardToFlip)) {

            String oldCardLink = GameUtils.getCardLink(_cardToFlip);
            gameState.flipCard(game, _cardToFlip, _toBack);
            String newCardLink = GameUtils.getCardLink(_cardToFlip);
            gameState.sendMessage(oldCardLink + " is flipped to " + newCardLink);

            // Emit effect result that card was flipped
            game.getActionsEnvironment().emitEffectResult(new DoubleSidedCardFlippedResult(_action.getPerformingPlayer(), _cardToFlip));
        }
    }
}

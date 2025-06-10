package com.gempukku.swccgo.logic.effects;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.AbstractSuccessfulEffect;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;

/**
 * An effect that flips a single-sided, stacked card to the other side.
 */
public class FlipSingleSidedStackedCard extends AbstractSuccessfulEffect {
    private PhysicalCard _cardToFlip;
    private boolean _toFaceUp;

    /**
     * Creates an effect that flips the single-sided, stacked card to the other side.
     * @param action the action performing this effect
     * @param cardToFlip the card to flip
     */
    public FlipSingleSidedStackedCard(Action action, PhysicalCard cardToFlip) {
        super(action);
        _cardToFlip = cardToFlip;
        _toFaceUp = _cardToFlip.getZone().isFaceDown();

    }

    @Override
    protected void doPlayEffect(SwccgGame game) {
        GameState gameState = game.getGameState();

        // This only works for stacked card!
        if (_cardToFlip.isDoubleSided() || (_cardToFlip.getStackedOn() == null)) {
            throw new UnsupportedOperationException("This effect only function on stacked, single-sided cards");
        }

        // Unstack and then re-stack it in the new state. This ensures that
        // the front-end updates correctly and that the card's properties
        // get set correctly
        boolean stackedInactive = _cardToFlip.isStackedAsInactive();
        boolean stackedJediTest = _cardToFlip.isStackedAsViaJediTest5();
        PhysicalCard cardStackedOn = _cardToFlip.getStackedOn();
        if (cardStackedOn != null) {
            gameState.removeCardsFromZone(Collections.singleton(_cardToFlip));
            gameState.stackCard(_cardToFlip, cardStackedOn, !_toFaceUp, stackedInactive, stackedJediTest);
        }

        String facingDirection = "face up";
        if (!_toFaceUp) {
            facingDirection = "face down";
        }
        gameState.sendMessage("Flipped: " + GameUtils.getCardLink(_cardToFlip) + " to " + facingDirection);
    }
}

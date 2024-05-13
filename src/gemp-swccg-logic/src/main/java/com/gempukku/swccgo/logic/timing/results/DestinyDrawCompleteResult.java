package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a destiny draw is completed (including responses to the draw).
 */
public class DestinyDrawCompleteResult extends EffectResult {
    private DrawDestinyEffect _drawDestinyEffect;

    /**
     * Creates an effect result that is triggered when a destiny draw is completed (including responses to the draw).
     * @param action the action
     * @param drawDestinyEffect the draw destiny effect
     */
    public DestinyDrawCompleteResult(Action action, DrawDestinyEffect drawDestinyEffect) {
        super(Type.COMPLETE_DESTINY_DRAW, action.getPerformingPlayer());
        _drawDestinyEffect = drawDestinyEffect;
    }

    /**
     * Gets the source action for drawing destiny.
     * @return the source action
     */
    public Action getSourceAction() {
        return _drawDestinyEffect.getAction();
    }

    /**
     * Gets the card drawn for destiny, or null if substituted destiny.
     * @return the card, or null
     */
    public PhysicalCard getCard() {
        return _drawDestinyEffect.getDrawnDestinyCard();
    }

    /**
     * Gets the value of the destiny draw.
     * @return the value
     */
    public float getDestinyValue() {
        return _drawDestinyEffect.getDestinyDrawValue();
    }

    /**
     * Gets the destiny type.
     * @return the destiny type
     */
    public DestinyType getDestinyType() {
        return _drawDestinyEffect.getDestinyType();
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just completed " + getDestinyType().getHumanReadable() + " draw";
    }
}

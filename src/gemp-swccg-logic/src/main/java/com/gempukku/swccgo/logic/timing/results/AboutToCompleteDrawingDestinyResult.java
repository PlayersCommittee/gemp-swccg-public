package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when a player is about to complete the draw destiny process and calculate the total destiny.
 */
public class AboutToCompleteDrawingDestinyResult extends EffectResult {
    private DrawDestinyEffect _drawDestinyEffect;

    /**
     * Creates an effect result that is emitted when a player is about to complete the draw destiny process and calculate the total destiny.
     * @param action the action
     * @param drawDestinyEffect the draw destiny effect
    */
    public AboutToCompleteDrawingDestinyResult(Action action, DrawDestinyEffect drawDestinyEffect) {
        super(Type.DRAWING_DESTINY_COMPLETE, action.getPerformingPlayer());
        _drawDestinyEffect = drawDestinyEffect;
    }

    /**
     * Gets the destiny type.
     * @return the destiny type
     */
    public DestinyType getDestinyType() {
        return _drawDestinyEffect.getDestinyType();
    }

    /**
     * Gets the total destiny.
     * @return the total destiny
     */
    public Float getTotalDestiny(SwccgGame game) {
        return _drawDestinyEffect.getTotalDestiny(game);
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Just completed drawing " + getDestinyType().getHumanReadable();
    }
}

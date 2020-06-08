package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted to perform costs for a player to draw a card for destiny.
 */
public class CostToDrawDestinyCardResult extends EffectResult {
    private DrawDestinyEffect _drawDestinyEffect;

    /**
     * Creates an effect result that is emitted to perform costs for a player to draw a card for destiny.
     * @param action the action
     * @param drawDestinyEffect the draw destiny effect
    */
    public CostToDrawDestinyCardResult(Action action, DrawDestinyEffect drawDestinyEffect) {
        super(Type.COST_TO_DRAW_DESTINY_CARD, action.getPerformingPlayer());
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
     * Sets the cost to draw the current destiny card as failed.
     * @param chosenByPlayer true if the failing the cost was chosen by the player, otherwise this was required
     */
    public void costToDrawCardFailed(boolean chosenByPlayer) {
        _drawDestinyEffect.costToDrawCardFailed(chosenByPlayer);
    }

    /**
     * Determines if the cost to draw the current destiny card failed.
     * @return true if cost failed, otherwise false
     */
    public boolean isCostToDrawCardFailed() {
        return _drawDestinyEffect.isCostToDrawCardFailed();
    }

    /**
     * Gets the number of destiny drawn so far.
     * @return the number of destiny drawn so far
     */
    public int getNumDestinyDrawnSoFar() {
        return _drawDestinyEffect.getNumDestinyDrawnSoFar();
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Determine costs to draw card for " + getDestinyType().getHumanReadable();
    }
}

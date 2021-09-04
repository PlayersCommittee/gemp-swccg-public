package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when Force loss is initiated.
 */
public class ForceLossInitiatedResult extends EffectResult {
    private PhysicalCard _source;
    private float _amount;

    /**
     * Creates an effect result that is triggered when Force loss is initiated.
     * @param action the action performing this effect result
     * @param source the source of the Force loss
     * @param amount the amount of Force to be lost
     */
    public ForceLossInitiatedResult(Action action, PhysicalCard source, float amount) {
        super(Type.FORCE_LOSS_INITIATED, action.getPerformingPlayer());
        _source = source;
        _amount = amount;
    }

    /**
     * Gets the source of the Force loss.
     * @return the source
     */
    public PhysicalCard getSource() {
        return _source;
    }

    /**
     * Gets the initial amount of Force to be lost.
     * @return the amount
     */
    public float getAmount() {
        return _amount;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Force loss initiated by " + GameUtils.getCardLink(_source);
    }
}

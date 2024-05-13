package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during a 'blown away' effect for actions that involve calculating Force loss to be performed.
 */
public class BlownAwayCalculateForceLossStepResult extends EffectResult {
    private PhysicalCard _actionSource;
    private PhysicalCard _blownAwayCard;
    private boolean _bySuperlaser;

    /**
     * Creates an effect result that is triggered during a 'blown away' effect for actions that involve calculating Force
     * loss to be performed.
     * @param action the action performing this effect result
     * @param blownAwayCard the card 'blown away'
     * @param bySuperlaser true if 'blown away' by Superlaser, otherwise false
     */
    public BlownAwayCalculateForceLossStepResult(Action action, PhysicalCard blownAwayCard, boolean bySuperlaser) {
        super(Type.BLOWN_AWAY_CALCULATE_FORCE_LOSS_STEP, action.getPerformingPlayer());
        _actionSource = action.getActionSource();
        _blownAwayCard = blownAwayCard;
        _bySuperlaser = bySuperlaser;
    }

    /**
     * Gets card that is the source of the 'blown away' action.
     * @return the source of the action
     */
    public PhysicalCard getSourceCard() {
        return _actionSource;
    }

    /**
     * Gets the 'blown away' card.
     * @return the 'blown away' card
     */
    public PhysicalCard getBlownAwayCard() {
        return _blownAwayCard;
    }

    /**
     * Determines if 'blown away' by Superlaser.
     * @return true or false
     */
    public boolean isBySuperlaser() {
        return _bySuperlaser;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(getBlownAwayCard()) + " 'blown away' - calculate Force loss";
    }
}

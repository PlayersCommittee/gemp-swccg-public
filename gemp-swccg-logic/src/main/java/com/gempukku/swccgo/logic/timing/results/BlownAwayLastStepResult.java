package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during a 'blown away' effect for actions that are not performed as responses to
 * BlownAwayForceLossStepResult or BlownAwayRelocateStepResult.
 */
public class BlownAwayLastStepResult extends EffectResult {
    private PhysicalCard _blownAwayCard;

    /**
     * Creates an effect result that is triggered during a 'blown away' effect for actions that are not performed as responses
     * to BlownAwayForceLossStepResult or BlownAwayRelocateStepResult.
     * @param action the action performing this effect result
     * @param blownAwayCard the card 'blown away'
     */
    public BlownAwayLastStepResult(Action action, PhysicalCard blownAwayCard) {
        super(Type.BLOWN_AWAY_LAST_STEP, action.getPerformingPlayer());
        _blownAwayCard = blownAwayCard;
    }

    /**
     * Gets the 'blown away' card.
     * @return the 'blown away' card
     */
    public PhysicalCard getBlownAwayCard() {
        return _blownAwayCard;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(getBlownAwayCard()) + " just 'blown away'";
    }
}

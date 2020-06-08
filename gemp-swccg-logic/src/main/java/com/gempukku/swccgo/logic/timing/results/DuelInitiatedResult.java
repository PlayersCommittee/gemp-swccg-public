package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during a duel when the duel is initiated.
 */
public class DuelInitiatedResult extends EffectResult {
    private boolean _isEpicDuel;

    /**
     * Creates an effect result that is triggered during a duel when the duel is initiated.
     * @param action the action performing this effect result
     * @param isEpicDuel true if epic duel, otherwise false
     */
    public DuelInitiatedResult(Action action, boolean isEpicDuel) {
        super(Type.DUEL_INITIATED, action.getPerformingPlayer());
        _isEpicDuel = isEpicDuel;
    }

    /**
     * Determines if duel is an epic duel.
     * @return true if epic duel, otherwise false
     */
    public boolean isEpicDuel() {
        return _isEpicDuel;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        if (isEpicDuel()) {
            return "Epic duel just initiated";
        }
        else {
            return "Duel just initiated";
        }
    }
}

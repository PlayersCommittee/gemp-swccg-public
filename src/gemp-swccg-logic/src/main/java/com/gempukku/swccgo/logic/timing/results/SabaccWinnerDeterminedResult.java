package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during a sabacc game when the winner has been determined.
 */
public class SabaccWinnerDeterminedResult extends EffectResult {
    private PhysicalCard _winningCharacter;

    /**
     * Creates an effect result that is triggered during a sabacc game when the winner has been determined.
     * @param action the action performing this effect result
     * @param winningCharacter the character that won sabacc
     */
    public SabaccWinnerDeterminedResult(Action action, PhysicalCard winningCharacter) {
        super(Type.SABACC_WINNER_DETERMINED, action.getPerformingPlayer());
        _winningCharacter = winningCharacter;
    }

    /**
     * Gets the character that won the sabacc game.
     * @return the character that won the sabacc game, or null
     */
    public PhysicalCard getWinningCharacter() {
        return _winningCharacter;
    }


    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Sabacc winner and loser determined";
    }
}

package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when the winner and loser of a duel are determined, but before any results of the duel.
 */
public class DuelResultDeterminedResult extends EffectResult {
    private String _winner;
    private PhysicalCard _winningCharacter;
    private String _loser;
    private PhysicalCard _losingCharacter;

    /**
     * Creates an effect result that is emitted when the winner and loser of a duel are determined, but before any results of the duel.
     * @param action the action performing this effect result
     * @param winner the winning player
     * @param winningCharacter the winning character
     * @param loser the losing player
     * @param losingCharacter the losing character
     */
    public DuelResultDeterminedResult(Action action, String winner, PhysicalCard winningCharacter, String loser, PhysicalCard losingCharacter) {
        super(Type.DUEL_RESULT_DETERMINED, action.getPerformingPlayer());
        _winner = winner;
        _winningCharacter = winningCharacter;
        _loser = loser;
        _losingCharacter = losingCharacter;
}

    /**
     * Gets the winning player.
     * @return the winning player, or null if duel ended in a tie
     */
    public String getWinner() {
        return _winner;
    }

    /**
     * Gets the winning character.
     * @return the winning character, or null if duel ended in a tie
     */
    public PhysicalCard getWinningCharacter() {
        return _winningCharacter;
    }

    /**
     * Gets the losing player.
     * @return the losing player, or null if duel ended in a tie
     */
    public String getLoser() {
        return _loser;
    }

    /**
     * Gets the losing character.
     * @return the losing character, or null if duel ended in a tie
     */
    public PhysicalCard getLosingCharacter() {
        return _losingCharacter;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Duel winner and loser determined";
    }
}

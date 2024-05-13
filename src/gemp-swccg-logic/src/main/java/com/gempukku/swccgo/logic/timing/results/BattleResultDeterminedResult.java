package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * The effect result that is emitted when the winner and loser of a battle are determined.
 */
public class BattleResultDeterminedResult extends EffectResult {
    private String _winner;
    private String _loser;
    private PhysicalCard _location;

    /**
     * Creates an effect result that is emitted when the winner and loser of a battle are determined.
     * @param winner the winner, or null if battle is a tie
     * @param loser the loser, or null if battle is a tie
     * @param location the battle location
     */
    public BattleResultDeterminedResult(String winner, String loser, PhysicalCard location) {
        super(Type.BATTLE_RESULT_DETERMINED, null);
        _winner = winner;
        _loser = loser;
        _location = location;
    }

    /**
     * Gets the winning player.
     * @return the winning player, or null if battle is a tie
     */
    public String getWinner() {
        return _winner;
    }

    /**
     * Gets the losing player.
     * @return the losing player, or null if battle is a tie
     */
    public String getLoser() {
        return _loser;
    }

    /**
     * Gets the battle location.
     * @return the battle location
     */
    public PhysicalCard getLocation() {
        return _location;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Battle winner and loser determined";
    }
}

package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a Program Trap is 'exploding'.
 */
public class ExplodingProgramTrapResult extends EffectResult {
    private PhysicalCard _programTrap;

    /**
     * Creates an effect result is triggered when a Program Trap is 'exploding'.
     * @param programTrap the Program Trap
     */
    public ExplodingProgramTrapResult(PhysicalCard programTrap) {
        super(Type.EXPLODING_PROGRAM_TRAP, null);
        _programTrap = programTrap;
    }

    /**
     * Gets the Program Trap.
     * @return the site
     */
    public PhysicalCard getProgramTrap() {
        return _programTrap;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return GameUtils.getCardLink(_programTrap) + " is 'exploding'";
    }
}

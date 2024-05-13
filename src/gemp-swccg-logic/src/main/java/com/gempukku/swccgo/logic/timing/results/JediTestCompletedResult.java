package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered when a Jedi Test is completed.
 */
public class JediTestCompletedResult extends EffectResult {
    private PhysicalCard _jediTest;
    private PhysicalCard _apprentice;

    /**
     * Creates an effect result that is triggered during a battle when the battle has just ended.
     * @param action the action performing this effect result
     * @param jediTest the Jedi Test that was completed
     * @param apprentice the apprentice that completed the Jedi Test
     */
    public JediTestCompletedResult(Action action, PhysicalCard jediTest, PhysicalCard apprentice) {
        super(Type.JEDI_TEST_COMPLETED, action.getPerformingPlayer());
        _jediTest = jediTest;
        _apprentice = apprentice;
    }

    /**
     * Gets the Jedi Test that was completed.
     * @return the Jedi Test
     */
    public PhysicalCard getJediTest() {
        return _jediTest;
    }

    /**
     * Gets the apprentice that completed the Jedi Test.
     * @return the apprentice that completed the Jedi Test
     */
    public PhysicalCard getCompletedBy() {
        return _apprentice;
    }

    /**
     * Gets the text to show to describe the effect result.
     * @param game the game
     * @return the text
     */
    @Override
    public String getText(SwccgGame game) {
        return "Jedi Test, " + GameUtils.getCardLink(_jediTest) + ", just completed";
    }
}

package com.gempukku.swccgo.logic.timing.results;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

/**
 * This effect result is triggered during a battle when the battle is initiated.
 */
public class BattleInitiatedResult extends EffectResult {
    private PhysicalCard _location;

    /**
     * Creates an effect result that is triggered during a battle when the battle is initiated.
     * @param action the action performing this effect result
     * @param location the battle location
     */
    public BattleInitiatedResult(Action action, PhysicalCard location) {
        super(Type.BATTLE_INITIATED, action.getPerformingPlayer());
        _location = location;
    }

    /**
     * Gets the battle location.
     * @return the location
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
        return "Battle just initiated at " + GameUtils.getCardLink(getLocation());
    }
}
